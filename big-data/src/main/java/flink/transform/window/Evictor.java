package flink.transform.window;

import flink.common.StreamData;
import flink.common.StreamExecutionEnvironmentBuilder;
import flink.common.mock.DataMock;
import flink.common.mock.MockFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.evictors.TimeEvictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.DeltaTrigger;
import org.junit.Test;

import java.time.Duration;

/**
 * @description: 触发窗口计算前或计算后，剔除窗口内数据
 *
 *               注：当使用 evictor 时，由于 evictor 中需要全量 element，所以此时增量聚合函数失效，将退化为全量聚合
 *
 * @author yangbin216
 * @date 2022/3/9 9:50
 * @version 1.0
 */
public class Evictor {

    public static final String JOIN_KEY = "order_no";

    /**
     * 场景：
     *      有这样一个车辆区间测试的需求，车辆每分钟上报当前位置与车速，每行进10公里，计算区间内最近15分钟最高车速。
     *
     *      TimeEvictor: 窗口计算时，只保留最近N段时间范围的 element
     *
     *
     * @throws Exception
     */
    @Test
    public void timeEvictor() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> dataSource = MockFactory.addSource(env, "data", new DataMock(), 2);

        DataStream<StreamData> orderStream = dataSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> result = orderStream
                .keyBy(e -> e.getFields().get(JOIN_KEY))
                .window(GlobalWindows.create())
                .trigger(DeltaTrigger.of(100000,
                        new DeltaFunction<StreamData>() {
                            @Override
                            public double getDelta(StreamData oldDataPoint, StreamData newDataPoint) {
                                return Double.parseDouble(newDataPoint.getFields().get("km")) - Double.parseDouble(newDataPoint.getFields().get("km"));
                            }
                        }, orderStream.getType().createSerializer(env.getConfig())))
                .evictor(TimeEvictor.of(Time.minutes(15)))
                .max("speed");

        result.print();
        env.execute("timeEvictor Example");
    }


    public static void main(String[] args) {
        System.out.print(a("i am a programmer"));
    }


    public static String a(String s) {
        StringBuilder res = new StringBuilder();
        int l = s.length();
        int j = 0;
        for (int i = 0; i < s.length() - 1; i ++) {
            if (s.charAt(i) != ' ') {
                String word = s.substring(j, i);
                System.out.println(word);
                res.append(test(word)).append(" ");
                j = i + 1;
            }
        }
        return res.toString();
    }
    public static String test(String word) {
        StringBuilder res = new StringBuilder();
        for (int i = word.length() - 1; i >=0; i--) {
            res.append(word.charAt(i));
        }

        System.out.println(res);
        return res.toString();
    }

}