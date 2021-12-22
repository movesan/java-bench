package callback.timetool;

/**
 * @description: 测试类
 * @author: Mr.Move
 * @create: 2018-07-31 15:40
 **/
public class TimeTest {

    public static void main(String[] args){
        TimeTools tool = new TimeTools();
        tool.testTime(new CallBack(){
            //定义execute方法
            @Override
            public void execute(){
                //这里可以加放一个或多个要测试运行时间的方法
                TestObject.testMethod();
            }
        });
    }
}
