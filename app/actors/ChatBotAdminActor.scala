package actors

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Cancellable, Props }
import akka.event.LoggingReceive
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.duration._

class ChatBotAdminActor(system: ActorSystem) extends Actor with ActorLogging {
  val room: ActorRef = ChatRoomActor(system)
  val cancellable: Cancellable = system.scheduler.schedule(0 seconds, 10 seconds, self, Tick)

  override def preStart(): Unit = room ! JoinChatRoom

  def receive: Receive = LoggingReceive {
    case ChatMessage(_, _) => ()
    case text: String => room ! ChatMessage(text.split(":")(0), text.split(":")(1))
    case Tick =>
      val response: String = "AdminBot:" + ActorHelper.get(GetStats, room)
      sender() ! response
    case other => log.error("issue - not expected: " + other)
  }
}

object ChatBotAdminActor {
  var bot: ActorRef = _

  def apply(system: ActorSystem): ActorRef = this.synchronized {
    if (bot == null) bot = system.actorOf(Props(new ChatBotAdminActor(system)))
    bot
  }
}
