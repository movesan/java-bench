package statemachine.vending;

/**
 * @description: 售完状态
 * @author: movesan
 * @create: 2020-10-12 10:22
 **/
public class SoldOutState implements State {

    private Machine machine;

    public SoldOutState(Machine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin() {
        System.out.println("已售完!");
    }

    @Override
    public void ejectCoin() {
        System.out.println("对不起, 已售完!");
    }

    @Override
    public void clickButton() {
        System.out.println("已售完!");
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
