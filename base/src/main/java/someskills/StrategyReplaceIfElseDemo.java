package someskills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;

/**
 * @description: 策略模式代替if else
 * 参考网址：https://mp.weixin.qq.com/s/hkypvNBkRjPM6HM51_jW9g
 *
 * @author: movesan
 * @create: 2020-06-24 14:29
 **/
public class StrategyReplaceIfElseDemo {

    /**
     * ----------------- 传统的 if else 解决方法 -----------------
     * 当每个业务逻辑有 3 4 行时，用传统的策略模式不值得，直接的if else又显得不易读
     */
    public String getCheckResult(String order) {
        if ("校验1".equals(order)) {
            return "执行业务逻辑1";
        } else if ("校验2".equals(order)) {
            return "执行业务逻辑2";
        } else if ("校验3".equals(order)) {
            return "执行业务逻辑3";
        } else if ("校验4".equals(order)) {
            return "执行业务逻辑4";
        } else if ("校验5".equals(order)) {
            return "执行业务逻辑5";
        } else if ("校验6".equals(order)) {
            return "执行业务逻辑6";
        } else if ("校验7".equals(order)) {
            return "执行业务逻辑7";
        } else if ("校验8".equals(order)) {
            return "执行业务逻辑8";
        } else if ("校验9".equals(order)) {
            return "执行业务逻辑9";
        }
        return "不在处理的逻辑中返回业务错误";
    }

    /**
     * ----------------- map 函数式接口方式 -----------------
     * 业务逻辑分派Map
     * Function为函数式接口，下面代码中 Function<String, String> 的含义是接收一个Stirng类型的变量，返回一个String类型的结果
     */
    private Map<String, Function<String, String>> checkResultDispatcher = new HashMap<>();

    /**
     * 初始化 业务逻辑分派Map 其中value 存放的是 lambda表达式
     */
    @PostConstruct
    public void checkResultDispatcherInit() {
        checkResultDispatcher.put("校验1", order -> String.format("对%s执行业务逻辑1", order));
        checkResultDispatcher.put("校验2", order -> String.format("对%s执行业务逻辑2", order));
        checkResultDispatcher.put("校验3", order -> String.format("对%s执行业务逻辑3", order));
        checkResultDispatcher.put("校验4", order -> String.format("对%s执行业务逻辑4", order));
        checkResultDispatcher.put("校验5", order -> String.format("对%s执行业务逻辑5", order));
        checkResultDispatcher.put("校验6", order -> String.format("对%s执行业务逻辑6", order));
        checkResultDispatcher.put("校验7", order -> String.format("对%s执行业务逻辑7", order));
        checkResultDispatcher.put("校验8", order -> String.format("对%s执行业务逻辑8", order));
        checkResultDispatcher.put("校验9", order -> String.format("对%s执行业务逻辑9", order));
    }

    public String getCheckResultSuper(String order) {
        //从逻辑分派Dispatcher中获得业务逻辑代码，result变量是一段lambda表达式
        Function<String, String> result = checkResultDispatcher.get(order);
        if (result != null) {
            //执行这段表达式获得String类型的结果
            return result.apply(order);
        }
        return "不在处理的逻辑中返回业务错误";
    }

    /**
     * ----------------- 扩展 -----------------
     */
    // Map 中的 value 也可以改为自定义的函数式接口，如下
    @FunctionalInterface
    private interface SomethingHandler {
        void process(String param);
    }
    private Map<String, SomethingHandler> someHandlerMap = new HashMap<>();


    // 如要执行多个处理逻辑，value 可为函数式接口 list
    private Map<String, List<SomethingHandler>> handlerMap = new HashMap<>();

    @PostConstruct
    public void somethingInit() {
        addHandler("校验1", order -> String.format("对%s执行业务逻辑1", order));
        addHandler("校验2", order -> String.format("对%s执行业务逻辑2", order));
    }

    private void addHandler(String param, SomethingHandler handler) {
        List<SomethingHandler> handlerList = handlerMap.getOrDefault(param, new ArrayList<>());
        handlerList.add(handler);
        handlerMap.putIfAbsent(param, handlerList);
    }


}
