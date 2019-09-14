package actors

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import akka.event.LoggingReceive

class ChatUserActor(room: ActorRef, out: ActorRef) extends Actor with ActorLogging {
  override def preStart(): Unit = room ! JoinChatRoom

  def receive: Receive = LoggingReceive {
    case ChatMessage(name, text) if sender() == room =>
      val result: String = s"$name:$text"
      out ! result
    case text: String => room ! ChatMessage(text.split(":")(0), text.split(":")(1))
    case other => log.error("issue - not expected: " + other)
  }
}

object ChatUserActor {
  def props(system: ActorSystem)(out: ActorRef): Props =
    Props(new ChatUserActor(ChatRoomActor(system), out))
}
