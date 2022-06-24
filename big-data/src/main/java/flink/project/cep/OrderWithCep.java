package flink.project.cep;

import flink.common.mock.MockFactory;
import flink.project.cep.beans.OrderEvent;
import flink.project.cep.beans.OrderMock;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.PatternTimeoutFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 恶意登录检测
 *          如果同一用户在一段时间内连续登录失败多次，则发出警告
 */
public class OrderWithCep {
    public static void main(String[] args) throws Exception {
        // 1. 设置运行环境
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // 2.设置watermark及定义事件时间
        WatermarkStrategy<OrderEvent> wms = WatermarkStrategy
                .<OrderEvent>forBoundedOutOfOrderness(Duration.ofMinutes(1))
                .withTimestampAssigner((event, timestamp) -> event.getOrderCreateTime());

        // 3.mock 登录数据
        SingleOutputStreamOperator<OrderEvent> orderStream = MockFactory.addSource(env, "order_logs", new OrderMock(), 2);


        // 4. 定义一个匹配模式
        // order -> pay, within 15m
        Pattern<OrderEvent, OrderEvent> orderEventPattern = Pattern
                .<OrderEvent>begin("order").where(new SimpleCondition<OrderEvent>() {
                    @Override
                    public boolean filter(OrderEvent value) throws Exception {
                        return "1".equals(value.getStatus());
                    }
                })
                .followedBy("pay").where(new SimpleCondition<OrderEvent>() {
                    @Override
                    public boolean filter(OrderEvent value) throws Exception {
                        return "2".equals(value.getStatus());
                    }
                })
                .within(Time.minutes(15));


        // 5. 将匹配模式应用到数据流上，得到一个pattern stream
        PatternStream<OrderEvent> patternStream = CEP.pattern(orderStream.keyBy(OrderEvent::getOrderId), orderEventPattern);

        OutputTag<OrderEvent> timeoutTag = new OutputTag<>("order_timeout_tag");
        // 6. 检出符合匹配条件的复杂事件，进行转换处理，得到报警信息
        SingleOutputStreamOperator<OrderEvent> warningStream = patternStream.select(timeoutTag, new OrderPatternTimeoutFunction(), new OrderPatternFunction());

        // 7. 打印超过 15 分钟还未支付的订单
        warningStream.getSideOutput(timeoutTag).print();

        env.execute("order without pay detect with cep job");
    }

    public static class OrderPatternTimeoutFunction implements PatternTimeoutFunction<OrderEvent, OrderEvent> {

        @Override
        public OrderEvent timeout(Map<String, List<OrderEvent>> pattern, long l) throws Exception {
            OrderEvent orderEvent = pattern.get("order").iterator().next();
            System.out.println("订单支付超时：" + orderEvent.getOrderId());
            return orderEvent;
        }
    }

    // 实现自定义的PatternSelectFunction
    public static class OrderPatternFunction implements PatternSelectFunction<OrderEvent, OrderEvent> {
        @Override
        public OrderEvent select(Map<String, List<OrderEvent>> pattern) throws Exception {
            OrderEvent orderEvent = pattern.get("pay").iterator().next();
            System.out.println("订单支付成功：" + orderEvent.getOrderId());
            return orderEvent;
        }
    }
}
