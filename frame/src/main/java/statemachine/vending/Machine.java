package statemachine.vending;

/**
 * @description: 售卖机
 * @author: movesan
 * @create: 2020-10-12 10:21
 **/
public class Machine {

    //机器本身包含所有的状态机
    private State soldOutState;
    private State noCoinState;
    private State hasCoinState;
    private State soldState;

    private State state; //机器的当前状态
    private int count = 0;//机器中当前饮料的数量

    /**
     * 初始化机器，引入所有的状态机，初始化饮料数量，初始化机器状态
     *
     * @param count
     */
    public Machine(int count) {
        this.soldOutState = new SoldOutState(this);
        this.noCoinState = new NoCoinState(this);
        this.hasCoinState = new HasCoinState(this);
        this.soldState = new SoldState(this);
        this.count = count;
        if (this.count > 0) {
            this.state = noCoinState;
        }
    }

    /**
     * 释放饮料时的内部处理程序
     */
    public void releaseBall() {
        System.out.println("一瓶饮料正在出货中...");
        if (count > 0) {
            count -= 1;
        }
    }

    public void insertCoin() {
        // 投币
        state.insertCoin();
    }

    public void ejectCoin() {
        state.ejectCoin();
    }

    public void clickButton() {
        state.clickButton();
        state.dispense();
    }

    public State getSoldOutState() {
        return soldOutState;
    }

    public State getNoCoinState() {
        return noCoinState;
    }

    public State getHasCoinState() {
        return hasCoinState;
    }

    public State getSoldState() {
        return soldState;
    }

    public State getState() {
        return state;
    }

    public int getCount() {
        return count;
    }

    public void setState(State state) {
        this.state = state;
    }
}
