package flink.cep;

import flink.cep.beans.OrderDetail;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/4/2 10:05
 * @version 1.0
 */
public class BaseCEP {

    /**
     * 个体模式
     */
    @Test
    public void individual() {
        Pattern<OrderDetail, OrderDetail> pattern = Pattern.<OrderDetail>begin("start");
        // 单例模式
        Pattern<OrderDetail, OrderDetail> singleton = pattern
                .where(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return false;
                    }
                })
                .or(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return false;
                    }
                })
                .until(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return false;
                    }
                });

        // 循环模式
        Pattern<OrderDetail, OrderDetail> loop = pattern
                // ---------------------------- 量词 ----------------------------
                .times(2) // 必须2次
                .times(2, 5) // 2,3,4,5次都可以
                .times(2, 5).greedy() // 2,3,4,5次都可以，尽可能重复匹配
                .times(2).optional() // 0 or 2 次
                .oneOrMore() // 1次或者多次
                .timesOrMore(2).optional().greedy() // 0,2 或者多次，并且尽可能的重复匹配
                // ---------------------------- 条件 ----------------------------
                // 简单条件
                // 通过.where()方法对事件中的字段进行判断筛选，决定是否接收该事件
                .where(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return false;
                    }
                })
                // 组合条件
                // 将简单的条件进行合并；or()方法表示或逻辑相连，where的直接组合就相当于与and
                .or(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return false;
                    }
                })
                // 终止条件
                // 如果使用了oneOrMore或者oneOrMore.optional，建议使用.until()作为终止条件，以便清理状态。
                .until(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return false;
                    }
                });


    }

    /**
     * 组合模式（多个个体模式的组合）
     */
    @Test
    public void combine() {
        // 组合模式是将多个个体组合，要以 begin 开始，其中每项为单个个体，需指定个体模式名称
        Pattern<OrderDetail, OrderDetail> combine = Pattern.<OrderDetail>begin("start")
                .next("pattern name") // 严格连续，中间没有任何不匹配事件
                .followedBy("pattern name") // 宽松连续，忽略匹配的事件之间的不匹配的事件
                .followedByAny("pattern name") // 不确定的宽松连续，一个匹配的事件能够再次使用
                .notNext("pattern name") // 不希望出现某种连续，不想让某个事件严格近邻前一个事件发生
                .notFollowedBy("pattern name") // 不希望出现某种连续，不想让某个事件在两个事件之间发生
                .within(Time.minutes(5)); // 为模式指定时间约束，用来要求在多长时间内匹配有效

    }

    /**
     * 模式组
     *      一个组合模式作为条件嵌套在个体模式里Pattern(（ab）c)
     */
    @Test
    public void pattern() {
        // 创建订单之后15分钟之内一定要付款，否则取消订单

        // 定义 Pattern
        Pattern<OrderDetail, OrderDetail> pattern = Pattern
                // 开始事件1：下单事件 order
                .<OrderDetail>begin("order").where(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return "1".equals(orderDetail.getStatus());
                    }
                })
                // 宽松匹配事件2：付款事件 pay
                .followedBy("pay").where(new SimpleCondition<OrderDetail>() {
                    @Override
                    public boolean filter(OrderDetail orderDetail) throws Exception {
                        return "2".equals(orderDetail.getStatus());
                    }
                })
                // 时间约束，15分钟内匹配才有效
                .within(Time.minutes(15));

    }
}