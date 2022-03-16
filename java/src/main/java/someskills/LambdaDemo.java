package someskills;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/1/19 18:05
 * @version 1.0
 */
public class LambdaDemo {

    public static void main(String[] args) {
//        List<Integer> list = new ArrayList<>();
//        Stream.of(1, 2, 3, 4)
//                .peek(e -> {
//                    list.add(e);
//                    System.out.println("e:" + e);
//                })
//                .forEach(list::add);
////                .collect(Collectors.toList());
//        System.out.println(list);
//
//
//        List<String> art = new ArrayList<>();
//        int total = art.stream().map(e -> 5).reduce(0, (acc, e) -> acc + e);
//
//        List<String> a = new ArrayList<>();
//        Map<String, String> b = new HashMap<>();

//        Stream<String> names = Stream.of("John", "Paul", "George", "John", "Paul", "John");
//
//        Map<String, Integer> countMap = new HashMap<>();
//        names.collect(Collectors.groupingBy(name -> name)).forEach((k, v) -> countMap.put(k, v.size()));
//
//        Map<String, Long> cm = names.collect(Collectors.groupingBy(name -> name, Collectors.counting()));
//
//        System.out.println(countMap);

        Fibonacci fibonacci = new Fibonacci();
        fibonacci.fibonacci(9);
        System.out.println(fibonacci);
    }

    public static void test() {
        // 5 种 lambda 表达式写法
        Runnable noArguments = () -> System.out.println("Hello World");
        ActionListener oneArgument = event -> System.out.println("button clicked");
        Runnable multiStatement = () -> {
            System.out.print("Hello");
            System.out.println(" World");
        };
        BinaryOperator<Long> add = (x, y) -> x + y;
        BinaryOperator<Long> addExplicit = (Long x, Long y) -> x + y;


        // 引用值，而不是变量: Lambda 表达式中引用的局部变量必须是final 或既成事实上的final 变量
        // 这种行为也解释了为什么Lambda 表达式也被称为闭包。未赋值的变量与周边环境隔离起来，进而被绑定到一个特定的值。
        String name = "curry";
//        name = "stephen"; // 如果赋值后，会导致 name 编译报错
        Runnable constant = () -> System.out.println("Hello World" + name);


        // 函数接口是只有一个抽象方法的接口，用作Lambda 表达式的类型
    }


}