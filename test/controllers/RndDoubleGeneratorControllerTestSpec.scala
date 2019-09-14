package controllers

import org.scalatestplus.play.{ HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec }
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration._

class RndDoubleGeneratorControllerTestSpec
    extends PlaySpec
    with OneServerPerSuite
    with OneBrowserPerSuite
    with HtmlUnitFactory {
  val injector: Injector = new GuiceApplicationBuilder().injector()
  val ws: WSClient = injector.instanceOf(classOf[WSClient])

  "Assuming ng_microservices is down, rx number" must {
    "work" in {
      val future = ws.url(s"http://localhost:$port/rnd/rxbat").get().map(_.body)
      val response = Await.result(future, 15.seconds)
      response mustBe "2.3000000000000007"
    }
  }
}
