package statemachine.vending;

/**
 * @description: 机器正在售卖的状态
 * @author: movesan
 * @create: 2020-10-12 10:20
 **/
public class SoldState implements State {

    private Machine machine;

    public SoldState(Machine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin() {
        System.out.println("请稍等，我们已经给您一瓶饮料了!");
    }

    @Override
    public void ejectCoin() {
        System.out.println("对不起, 您已经按了选择按钮!");
    }

    @Override
    public void clickButton() {
        System.out.println("按两次按钮不会额外提供饮料!");
    }

    @Override
    public void dispense() {
        System.out.println("剩余可售数量:" + machine.getCount());
        if (machine.getCount() > 0) {
            machine.releaseBall();
            machine.setState(machine.getNoCoinState());
        } else {
            System.out.println("库存不足,退币中.....!");
            machine.setState(machine.getSoldOutState());
        }
    }
}
