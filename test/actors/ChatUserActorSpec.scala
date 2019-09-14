package actors

import akka.actor.{ Actor, ActorSystem, Props }
import akka.testkit.{ TestActorRef, TestKit, TestProbe }
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration._

class OutActor extends Actor {
  def receive: Receive = {
    case _: Any => ()
  }
}

class ChatUserActorSpec extends PlaySpec {
  class Actors extends TestKit(ActorSystem("test"))

  "ChatUserActor" should {
    "join the chat room and send a message" in new Actors {
      val probe = new TestProbe(system)
      val actorOutRef: TestActorRef[OutActor] = TestActorRef[OutActor](Props[OutActor])
      val actorRef: TestActorRef[ChatUserActor] =
        TestActorRef[ChatUserActor](ChatUserActor.props(system)(actorOutRef))
      val userActor: ChatUserActor = actorRef.underlyingActor
      assert(userActor.context != null)

      val msg = "testUser:test msg"
      probe.send(actorRef, msg)
      actorRef.receive(msg)
      receiveOne(2000 millis)
    }
  }
}
