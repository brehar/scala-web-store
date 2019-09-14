package controllers

import com.google.inject.{ Inject, Singleton }
import play.api.mvc.{ Action, AnyContent, Controller }
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler
import services.PriceService

import scala.concurrent.Future

@Singleton
class RxController @Inject()(priceService: PriceService) extends Controller {
  def prices: Action[AnyContent] = Action { implicit request =>
    val sourceObservable = priceService.generatePrices
    val rxResult = Observable
      .create(sourceObservable.subscribe)
      .subscribeOn(IOScheduler())
      .take(1)
      .flatMap { x =>
        Observable.just(x)
      }
      .toBlocking
      .first

    Ok("RxScala price suggested is = " + rxResult)
  }

  def pricesAsync: Action[AnyContent] = Action.async { implicit request =>
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val sourceObservable = priceService.generatePrices
    val rxResult = Observable
      .create(sourceObservable.subscribe)
      .subscribeOn(IOScheduler())
      .take(1)
      .flatMap { x =>
        Observable.just(x)
      }
      .toBlocking
      .first

    Future(Ok("RxScala price suggested is = " + rxResult))
  }
}
