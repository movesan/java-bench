package statemachine.squirrel;

import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

/**
 * @description: 快速开始
 * @author: movesan
 * @create: 2020-10-12 13:49
 **/
public class QuickStartSample {

    // 1.定义状态机时间
    enum FSMEvent {
        ToA, ToB, ToC, ToD
    }

    // 2. 状态机
    @StateMachineParameters(stateType = String.class, eventType = FSMEvent.class, contextType = Integer.class)
    static class StateMachineSample extends AbstractUntypedStateMachine {
        /**
         * 从A状态到B状态
         *
         * @param from    源状态
         * @param to      目标状态
         * @param event   事件
         * @param context 上下文
         */
        protected void fromAToB(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Transition from '" + from + "' to '" + to + "' on event '" + event +
                    "' with context '" + context + "'.");
        }

        /**
         * 进入B状态
         *
         * @param from    源状态
         * @param to      目标状态
         * @param event   事件
         * @param context 上下文
         */
        protected void ontoB(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Entry State \'" + to + "\'.");
        }
    }

    public static void main(String[] args) {
        // 3. 生成状态转移
        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachineSample.class);
        builder.externalTransition().from("A").to("B").on(FSMEvent.ToB).callMethod("fromAToB");
        builder.onEntry("B").callMethod("ontoB");

        // 4. 使用状态机
        UntypedStateMachine fsm = builder.newStateMachine("A");
        fsm.fire(FSMEvent.ToB, 10);

        System.out.println("Current state is " + fsm.getCurrentState());
    }
}
