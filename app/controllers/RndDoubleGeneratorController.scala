package controllers

import com.google.inject.{ Inject, Singleton }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ Action, AnyContent, Controller }
import services.RndService

@Singleton
class RndDoubleGeneratorController @Inject()(service: RndService) extends Controller {
  def rndDouble: Action[AnyContent] = Action { implicit request =>
    Ok(service.next().toString)
  }

  def rndCall: Action[AnyContent] = Action.async { implicit request =>
    service.call().map(res => Ok(res))
  }

  def rxCall: Action[AnyContent] = Action { implicit request =>
    Ok(service.rxScalaCall().toBlocking.first.toString)
  }

  def rxScalaCallBatch: Action[AnyContent] = Action { implicit request =>
    Ok(service.rxScalaCallBatch().toBlocking.first.toString)
  }
}
