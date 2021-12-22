package kafka;

import kafka.admin.TopicCommand;
import org.junit.Test;

/**
 * @description: 主题 demo
 * @author: movesan
 * @create: 2020-09-26 11:15
 **/
public class TopicDemo {

    public static final String HOST_NAME = "192.168.1.114";

    @Test
    public void createTopic(){
        String[] options = new String[]{
                "--zookeeper", HOST_NAME + ":2181/kafka",
                "--create",
                "--replication-factor", "3",
                "--partitions", "3",
                "--topic", "test"
        };
        kafka.admin.TopicCommand.main(options);
    }

    @Test
    public void describeTopic(){
        String[] options = new String[]{
                "--zookeeper", HOST_NAME + ":2181/kafka",
                "--describe",
                "--topic", "test"
        };
        kafka.admin.TopicCommand.main(options);
    }

    @Test
    public void listTopic(){
        String[] options = new String[]{
                "--zookeeper", HOST_NAME + ":2181/kafka",
                "--list"
        };
        kafka.admin.TopicCommand.main(options);
    }
}
