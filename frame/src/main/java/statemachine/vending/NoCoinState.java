package statemachine.vending;

/**
 * @description: 机器处于没有投硬币的状态
 * @author: movesan
 * @create: 2020-10-12 10:19
 **/
public class NoCoinState implements State {

    private Machine machine;

    private String tip = "请投入一个硬币!";

    public NoCoinState(Machine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin() {
        System.out.println(tip);
        machine.setState(machine.getHasCoinState());
    }

    @Override
    public void ejectCoin() {
        System.out.println(tip);
    }

    @Override
    public void clickButton() {
        System.out.println(tip);
    }

    @Override
    public void dispense() {
        System.out.println(tip);
    }
}
