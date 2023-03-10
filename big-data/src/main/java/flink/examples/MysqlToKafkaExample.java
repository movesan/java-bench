package flink.examples;

import com.alibaba.fastjson.JSONObject;
import flink.common.source.mysql.Employee;
import flink.common.source.mysql.MysqlSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MysqlToKafkaExample {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // mysql source
        DataStreamSource<Employee> source = env.addSource(new MysqlSource());
//        SingleOutputStreamOperator<WeiboTopN.UserBehavior> source = MockFactory.addSource(env, "blog_logs", new WeiboTopN.UserBehaviorMock(), 2);

        DataStream<String> stream = source.map((MapFunction<Employee, String>) JSONObject::toJSONString);

        stream.print();
        // kafka sink
//        stream.sinkTo(KafkaFactory.getKafkaSink("employees"));
        env.execute();

    }
}
