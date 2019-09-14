package controllers

import actors.{ ChatBotAdminActor, ChatUserActor }
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{ Inject, Singleton }
import play.api.libs.streams.ActorFlow
import play.api.mvc.{ Action, AnyContent, Controller, Flash, WebSocket }

@Singleton
class ChatController @Inject()(implicit val system: ActorSystem, materializer: Materializer)
    extends Controller {
  ChatBotAdminActor(system)

  def index_socket: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.chat_index()(Flash(Map())))
  }

  def ws: WebSocket = WebSocket.accept[String, String] { implicit request =>
    ActorFlow.actorRef(out => ChatUserActor.props(system)(out))
  }
}
