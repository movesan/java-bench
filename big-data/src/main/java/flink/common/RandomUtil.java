package flink.common;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/3/8 16:52
 * @version 1.0
 */
public class RandomUtil {

    /**
     * 生成 [m,n] 的数字
     * int randNumber =rand.nextInt(MAX - MIN + 1) + MIN;
     * */
    public static int intervalRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min + 1)) + min;
    }

    /**
     * 随机返回集合中某个元素
     * @param list
     * @return
     */
    public static String getRandomElement(List<String> list) {
        Collections.shuffle(list);
        return list.get(0);
    }
}