package storm.wordcount;

import java.util.Map;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

/**
 * @description:
 * @author: Mr.Move
 * @create: 2018-11-16 10:38
 **/
public class PrintBolt extends BaseRichBolt {

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    }

    @Override
    public void execute(Tuple input) {
        String word = input.getStringByField("word");
        Integer count = input.getIntegerByField("count");

        // 打印上游传递的数据
        System.out.println(word + " : " + count);

        // 注意：这里不需要再向下游传递数据了，因为没有下游了
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
