package callback.print;

/**
 * @description: 异步回调打印机
 * @author: Mr.Move
 * @create: 2018-07-31 15:00
 **/
public class PrinterASyn implements Printer {

    @Override
    public void print(final CallBack callBack, String text) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("正在打印 . . . ");
                try {
                    Thread.currentThread();
                    Thread.sleep(3000);// 毫秒
                } catch (Exception e) {
                }

                callBack.printFinishedCallback("打印完成");
            }
        }).start();
    }
}
