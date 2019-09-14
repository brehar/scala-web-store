package actors

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Await
import scala.concurrent.duration._

object ActorHelper {
  def get(msg: Any, actor: ActorRef): String = {
    implicit val timeout: Timeout = Timeout(5 seconds)

    val result = (actor ? msg).mapTo[String].map(_.toString)
    Await.result(result, 5 seconds)
  }
}
