package statemachine.vending;

/**
 * @description: 状态接口
 * @author: movesan
 * @create: 2020-10-12 10:18
 **/
public interface State {

    /**
     * 投入硬币
     */
    void insertCoin();
    /**
     * 根据选择按钮情况，处理结果，返回处理结果，释放饮料
     */
    void ejectCoin();
    /**
     * 按下饮料选择按钮
     */
    void clickButton();
    /**
     * 机器释放饮料，处理机器内部状态，返回初始可投币状态
     */
    void dispense();
}
