package statemachine.vending;

/**
 * @description: 测试类
 * @author: movesan
 * @create: 2020-10-12 10:22
 **/
public class StateMachineTest {

    public static void main(String[] args) {
        Machine machine = new Machine(10);
        for (int i = 0; i < 12; i++) {
            machine.insertCoin();
            machine.clickButton();
        }

    }
}
