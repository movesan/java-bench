package others;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @description: 仓位分配
 * @author: movesan
 * @create: 2020-08-21 16:59
 **/
public class SpaceDispatchDemo {

    public static void main(String[] args) {
        spaceDispatch();

    }

    public static void spaceDispatch() {
        String[] deviceNoArr = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        // 每个设备的作业率
        Map<String, BigDecimal> workRateMap = new HashMap<>();
        // 每个设备的作业数
        Map<String, Integer> workCountMap = new HashMap<>();
        // 总作业数
        int sum = 0;
        // 下一个轮到的首个设备下标
        int deviceStart = 0;
        int shipCount = 0;
        while (true) {
            // 船舱数
            int shipSpace = randomShipSpace();
            System.out.println("船舱数：" + shipSpace);
            int deviceNext = deviceStart;
            for (int i = 0; i < shipSpace; i++) {
                // 具体设备 {"A", "B", "C", "D", "E", "F", "G", "H", "I"}
                String deviceNo = deviceNoArr[deviceNext];
                int deviceWorkCount = (workCountMap.get(deviceNo) == null ? 0 : workCountMap.get(deviceNo)) + 1;
                BigDecimal rate = BigDecimal.valueOf(deviceWorkCount).divide(BigDecimal.valueOf(++sum), 3, BigDecimal.ROUND_CEILING);
                workRateMap.put(deviceNo, rate);
                workCountMap.put(deviceNo, deviceWorkCount);

                deviceNext++;
                if (deviceNext > (deviceNoArr.length - 1)) {
                    deviceNext = 0;
                }
            }
            deviceStart++;
            if (deviceStart > (deviceNoArr.length - 1)) {
                deviceStart = 0;
            }

            List<Map.Entry<String, BigDecimal>> sortedList = sortRateMap(workRateMap);
            BigDecimal firstRate = sortedList.get(0).getValue();//获取集合内第一个元素
            BigDecimal lastRate = sortedList.get(sortedList.size() - 1).getValue();//获取集合内最后一个元素
            BigDecimal error = firstRate.subtract(lastRate);

            shipCount++;
            System.out.println("==========================");
            System.out.println(sortedList);
            System.out.println("最小使用率：" + lastRate);
            System.out.println("最大使用率：" + firstRate);
            System.out.println(error);
            if (error.compareTo(BigDecimal.valueOf(0.001)) <= 0) {
                System.out.println("船次数量：" + shipCount);
                break;
            }
        }
    }

    public static List<Map.Entry<String, BigDecimal>> sortRateMap(Map<String, BigDecimal> workRateMap) {
        List<Map.Entry<String, BigDecimal>> list = new ArrayList<Map.Entry<String, BigDecimal>>(workRateMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return list;
    }

    public static int randomShipSpace() {
        int[] spaceArr = {5, 5, 7, 7, 7, 9, 9, 9, 9, 9};
        Random random = new Random();
        return spaceArr[random.nextInt(9)];
    }

}
