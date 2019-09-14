import org.scalatestplus.play.{ OneAppPerTest, PlaySpec }
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ApplicationSpec extends PlaySpec with OneAppPerTest {
  "Routes" should {
    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/someEndpoint")).map(status(_)) mustBe Some(NOT_FOUND)
    }
  }

  "HomeController" should {
    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get
      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Reactive Web Store")
    }
  }

  "RndController" should {
    "return a random number" in {
      contentAsString(route(app, FakeRequest(GET, "/rnd/rxbat")).get) mustBe "2.3000000000000007"
    }
  }
}
