package callback.print;

/**
 * @description: 同步回调打印机
 * @author: Mr.Move
 * @create: 2018-07-31 14:55
 **/
public class PrinterSyn implements Printer {

    @Override
    public void print(CallBack callBack, String text) {
        System.out.println("正在打印 . . . ");
        try {
            Thread.currentThread();
            Thread.sleep(3000);// 毫秒
        } catch (Exception e) {
        }

        callBack.printFinishedCallback("打印完成");
    }

}
