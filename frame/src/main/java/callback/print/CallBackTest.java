package callback.print;

/**
 * @description: 测试类
 * @author: Mr.Move
 * @create: 2018-07-31 15:04
 **/
public class CallBackTest {

    public static void main(String[] args){

        String printText = "一份简历";

        System.out.println("需要打印的内容是 ---> " + printText);
        // 同步回调打印机
//        new PrinterSyn().print(new CallBack() {
//            @Override
//            public void printFinishedCallback(String msg) {
//                System.out.println("打印机告诉我的消息是 ---> " + msg);
//            }
//        }, printText);

        // 异步回调打印机
        new PrinterASyn().print(new CallBack() {
            @Override
            public void printFinishedCallback(String msg) {
                System.out.println("打印机告诉我的消息是 ---> " + msg);
            }
        }, printText);

        System.out.println("我在等待 打印机 给我反馈");
    }
}
