package storm.wordcount;

import java.util.Map;
import java.util.Random;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

/**
 * @description:
 * @author: Mr.Move
 * @create: 2018-11-16 10:29
 **/
public class RandomSentenceSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;

    private String[] sentences = new String[]{"the cow jumped over the moon", "an apple a day keeps the doctor away",
            "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature"};

    /**
     * 初始化的一些操作放到这里
     *
     * @param conf      配置信息
     * @param context   应用的上下文
     * @param collector 向下游输出数据的收集器
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    /**
     * 处理业务逻辑，在最后向下游输出数据
     */
    @Override
    public void nextTuple() {
        //随机生成句子
        String sentence = this.sentences[new Random().nextInt(sentences.length)];
        System.out.println("生成的句子为 --> " + sentence);
        //向下游输出
        this.collector.emit(new Values(sentence));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //定义向下游输出的名称
        declarer.declare(new Fields("sentence"));
    }
}
