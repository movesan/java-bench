package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

/**
 * @description:
 *
 * @author movesan
 * @date 2021/12/20 16:56
 * @version 1.0
 */
public class HelloActor extends UntypedAbstractActor {

    // 该Actor在收到消息message后，会打印Hello message
    @Override
    public void onReceive(Object message) throws Throwable {
        System.out.printf("Hello %s%n", message);
    }

    public static void main(String[] args) {
        // 创建Actor系统，Actor不能脱离ActorSystem存在
        ActorSystem system = ActorSystem.create("HelloSystem");
        // 创建HelloActor
        ActorRef actorRef = system.actorOf(Props.create(HelloActor.class));
        // 发送消息给HelloActor
        actorRef.tell("Actor", ActorRef.noSender());
    }
}