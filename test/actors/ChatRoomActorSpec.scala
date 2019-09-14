package actors

import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ TestActorRef, TestKit, TestProbe }
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration._

class ChatRoomActorSpec extends PlaySpec {
  class Actors extends TestKit(ActorSystem("test"))

  "ChatRoomActor" should {
    "accept joins in the chat room" in new Actors {
      val probe1 = new TestProbe(system)
      val probe2 = new TestProbe(system)
      val actorRef: TestActorRef[ChatRoomActor] = TestActorRef[ChatRoomActor](Props[ChatRoomActor])
      val roomActor: ChatRoomActor = actorRef.underlyingActor
      assert(roomActor.users.isEmpty)

      probe1.send(actorRef, JoinChatRoom)
      probe2.send(actorRef, JoinChatRoom)
      awaitCond(roomActor.users.size == 2, 100 millis)

      assert(roomActor.users.contains(probe1.ref))
      assert(roomActor.users.contains(probe2.ref))
    }

    "get stats from the chat room" in new Actors {
      val probe = new TestProbe(system)
      val actorRef: TestActorRef[ChatRoomActor] = TestActorRef[ChatRoomActor](Props[ChatRoomActor])
      val roomActor: ChatRoomActor = actorRef.underlyingActor
      assert(roomActor.users.isEmpty)

      probe.send(actorRef, JoinChatRoom)
      awaitCond(roomActor.users.size == 1, 100 millis)
      assert(roomActor.users.contains(probe.ref))

      probe.send(actorRef, GetStats)
      receiveOne(2000 millis)
    }
  }
}
