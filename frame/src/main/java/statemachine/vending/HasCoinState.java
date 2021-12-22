package statemachine.vending;

/**
 * @description: 机器处于有硬币，有饮料，没有按下按钮的状态
 * @author: movesan
 * @create: 2020-10-12 10:20
 **/
public class HasCoinState implements State {

    private Machine machine;

    public HasCoinState(Machine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin() {
        System.out.println("已投币，无需再投币!");
    }

    @Override
    public void ejectCoin() {
        System.out.println("已投入硬币，退币!");
        machine.setState(machine.getNoCoinState());
    }

    @Override
    public void clickButton() {
        System.out.println("按下选择按钮 ... ");
        machine.setState(machine.getSoldState());
    }

    @Override
    public void dispense() {
        System.out.println("没有饮料被释放!");
    }
}
