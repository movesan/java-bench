package storm.wordcount;

import java.util.HashMap;
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
 * @create: 2018-11-16 10:36
 **/
public class WordCountBolt extends BaseRichBolt {

    private Map<String, Integer> wordMaps = new HashMap<String, Integer>();

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String word = input.getStringByField("word");
        Integer count = this.wordMaps.get(word);
        if (null == count) {
            count = 0;
        }
        count++;
        this.wordMaps.put(word, count);

        // 向下游输出数据，注意这里输出的多个字段数据
        this.collector.emit(new Values(word, count));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));

    }
}
