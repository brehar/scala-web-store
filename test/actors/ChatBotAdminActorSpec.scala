package actors

import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ ImplicitSender, TestActorRef, TestKit }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

class ChatBotAdminActorSpec
    extends TestKit(ActorSystem("test"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {
  "ChatBotAdminActor" should {
    "be able to create admin bot in the chat room and tick" in {
      val actorRef = TestActorRef[ChatBotAdminActor](Props(new ChatBotAdminActor(system)))
      val botActor = actorRef.underlyingActor

      assert(botActor.context != null)
      awaitCond(botActor.room != null)
    }
  }
}
