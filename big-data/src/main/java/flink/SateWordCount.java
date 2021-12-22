package flink;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @ClassName: WordCount
 * @Description:
 * @Version: 1.0
 */
public class SateWordCount {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // socket文本流
        DataStream<String> inputStream = env.socketTextStream("localhost", 7788);

        DataStream<WordReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new WordReading(fields[0], new Integer(fields[1]));
        });

        SingleOutputStreamOperator<Tuple2<String, Integer>> resultStream = dataStream
                .keyBy("word")
                .flatMap(new WordCountFunction());

        resultStream.print();

        env.execute();
    }

    public static class WordCountFunction extends RichFlatMapFunction<WordReading, Tuple2<String, Integer>> {

        private ValueState<Integer> countState;

        @Override
        public void open(Configuration parameters) throws Exception {
            countState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("count_state", Integer.class));
        }

        @Override
        public void flatMap(WordReading value, Collector<Tuple2<String, Integer>> out) throws Exception {
            Integer count = countState.value();
            Integer countNew = count + value.getCount();
            countState.update(countNew);

            out.collect(new Tuple2<String, Integer>(value.getWord(), countNew));
        }
    }

    public static class WordReading implements java.io.Serializable {
        private String word;
        private int count;

        public WordReading() {
        }

        public WordReading(String word, int count) {
            this.word = word;
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "WordReading{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

}
