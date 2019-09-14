package actors

import akka.actor.{ Actor, ActorRef, ActorSystem, Props, Terminated }
import akka.event.LoggingReceive

class ChatRoomActor extends Actor {
  var users: Set[ActorRef] = Set[ActorRef]()

  def receive: Receive = LoggingReceive {
    case msg: ChatMessage => users.foreach(_ ! msg)
    case JoinChatRoom =>
      users += sender()
      context.watch(sender())
    case GetStats =>
      val stats: String = "online users [" + users.size + "] - users [" + users
        .map(_.hashCode())
        .mkString(" | ") + "]"
      sender() ! stats
    case Terminated(user) => users -= user
  }
}

object ChatRoomActor {
  var room: ActorRef = _

  def apply(system: ActorSystem): ActorRef = this.synchronized {
    if (room == null) room = system.actorOf(Props[ChatRoomActor])
    room
  }
}
