package flink.project.cep;

import com.google.common.collect.Lists;
import flink.common.mock.MockFactory;
import flink.project.cep.beans.LoginEvent;
import flink.project.cep.beans.LoginFailWarning;
import flink.project.cep.beans.LoginMock;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * 恶意登录检测
 *          如果同一用户在一段时间内连续登录失败多次，则发出警告
 */
public class LoginFail {
    public static void main(String[] args) throws Exception {
        // 1. 设置运行环境
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // 2.设置watermark及定义事件时间
        WatermarkStrategy<LoginEvent> wms = WatermarkStrategy
                .<LoginEvent>forBoundedOutOfOrderness(Duration.ofMinutes(1))
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());

        // 3.mock 登录数据
        SingleOutputStreamOperator<LoginEvent> loginDS = MockFactory.addSource(env, "login_logs", new LoginMock(), 2)
                .assignTimestampsAndWatermarks(wms);

        // 自定义处理函数检测连续登录失败事件
        SingleOutputStreamOperator<LoginFailWarning> warningStream = loginDS
                .keyBy(LoginEvent::getUserId)
                .process(new LoginFailDetectWarning(3, 10000));

        warningStream.print();

        env.execute("login fail detect job");
    }

    /**
     * 通过 timer 实现
     *
     *      缺点：恶意登录只能在 2s 后才能检测到
     */
    public static class LoginFailDetectWarning0 extends KeyedProcessFunction<Long, LoginEvent, LoginFailWarning> {
        // 定义属性，最大连续登录失败次数
        private Integer maxFailTimes;

        public LoginFailDetectWarning0(Integer maxFailTimes) {
            this.maxFailTimes = maxFailTimes;
        }

        // 定义状态：保存2秒内所有的登录失败事件
        ListState<LoginEvent> loginFailEventListState;
        // 定义状态：保存注册的定时器时间戳
        ValueState<Long> timerTsState;

        @Override
        public void open(Configuration parameters) throws Exception {
            loginFailEventListState = getRuntimeContext().getListState(new ListStateDescriptor<LoginEvent>("login-fail-list", LoginEvent.class));
            timerTsState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timer-ts", Long.class));
        }

        @Override
        public void processElement(LoginEvent value, Context ctx, Collector<LoginFailWarning> out) throws Exception {
            // 判断当前登录事件类型
            if ("fail".equals(value.getLoginState())) {
                // 1. 如果是失败事件，添加到列表状态中
                loginFailEventListState.add(value);
                // 如果没有定时器，注册一个2秒之后的定时器
                if (timerTsState.value() == null) {
                    Long ts = (value.getTimestamp() + 2) * 1000L;
                    ctx.timerService().registerEventTimeTimer(ts);
                    timerTsState.update(ts);
                }
            } else {
                // 2. 如果是登录成功，删除定时器，清空状态，重新开始
                if (timerTsState.value() != null)
                    ctx.timerService().deleteEventTimeTimer(timerTsState.value());
                loginFailEventListState.clear();
                timerTsState.clear();
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<LoginFailWarning> out) throws Exception {
            // 定时器触发，说明2秒内没有登录成功来，判断ListState中失败的个数
            ArrayList<LoginEvent> loginFailEvents = Lists.newArrayList(loginFailEventListState.get());
            Integer failTimes = loginFailEvents.size();

            if (failTimes >= maxFailTimes) {
                // 如果超出设定的最大失败次数，输出报警
                out.collect(new LoginFailWarning(ctx.getCurrentKey(),
                        loginFailEvents.get(0).getTimestamp(),
                        loginFailEvents.get(failTimes - 1).getTimestamp(),
                        "login fail in 2s for " + failTimes + " times"));
            }

            // 清空状态
            loginFailEventListState.clear();
            timerTsState.clear();
        }
    }

    /**
     * 只通过 ListState 实现
     *
     *      优点：当有第二次失败时，可以立刻检测到
     */
    public static class LoginFailDetectWarning extends KeyedProcessFunction<Long, LoginEvent, LoginFailWarning> {
        // 定义属性，最大连续登录失败次数
        private Integer maxFailTimes;
        // 最大连续失败时间（毫秒）
        private Integer maxFailInterval;

        public LoginFailDetectWarning(Integer maxFailTimes, Integer maxFailInterval) {
            this.maxFailTimes = maxFailTimes;
            this.maxFailInterval = maxFailInterval;
        }

        // 定义状态：保存2秒内所有的登录失败事件
        ListState<LoginEvent> loginFailEventListState;

        @Override
        public void open(Configuration parameters) throws Exception {
            loginFailEventListState = getRuntimeContext().getListState(new ListStateDescriptor<LoginEvent>("login-fail-list", LoginEvent.class));
        }

        // 以登录事件作为判断报警的触发条件，不再注册定时器
        @Override
        public void processElement(LoginEvent value, Context ctx, Collector<LoginFailWarning> out) throws Exception {
            // 判断当前事件登录状态
            if ("fail".equals(value.getLoginState())) {
                // 1. 如果是登录失败，获取状态中之前的登录失败事件，继续判断是否已有失败事件
                Iterator<LoginEvent> iterator = loginFailEventListState.get().iterator();

                if (iterator.hasNext()) {
                    int size = Lists.newArrayList(loginFailEventListState.get()).size();
                    // 1.1 如果已经有登录失败事件，继续判断时间戳是否在2秒之内
                    // 获取已有的登录失败事件
                    LoginEvent firstFailEvent = iterator.next();
                    // 如果登录失败次数大于给定次数，且在时间范围内
                    if ((size + 1 == maxFailTimes)
                            && value.getTimestamp() - firstFailEvent.getTimestamp() <= maxFailInterval) {
                        // 1.1.1 如果在2秒之内，输出报警
                        out.collect(new LoginFailWarning(value.getUserId(), firstFailEvent.getTimestamp(), value.getTimestamp(), "login fail " + maxFailTimes + " times in " + maxFailInterval / 1000 + "s"));
                    }

                    // TODO 始终保存最近 maxFailTimes LRU
                    loginFailEventListState.clear();
                    loginFailEventListState.add(value);
                } else {
                    // 1.2 如果没有登录失败，直接将当前事件存入ListState
                    loginFailEventListState.add(value);
                }
            } else {
                // 2. 如果是登录成功，直接清空状态
                loginFailEventListState.clear();
            }
        }
    }
}
