package callback.timetool;

/**
 * @description: 时间统计工具类
 * @author: Mr.Move
 * @create: 2018-07-31 15:36
 **/
public class TimeTools {

    public void testTime(CallBack callBack) {
        long begin = System.currentTimeMillis(); //测试起始时间
        callBack.execute(); ///进行回调操作
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin)); //打印使用时间
    }
}
