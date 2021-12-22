package storm.wordcount;

import java.util.Map;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * @description:
 * @author: Mr.Move
 * @create: 2018-11-16 10:34
 **/
public class SplitSentenceBolt extends BaseRichBolt {

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        // 通过Tuple的getValueByField获取上游传递的数据，其中"sentence"是定义的字段名称
        String sentence = input.getStringByField("sentence");

        // 进行分割处理
        String[] words = sentence.split(" ");

        // 向下游输出数据
        for (String word : words) {
            this.collector.emit(new Values(word));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}
