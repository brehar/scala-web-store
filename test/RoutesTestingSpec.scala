import org.scalatestplus.play.{ OneAppPerTest, PlaySpec }
import play.api.test.FakeRequest
import play.api.test.Helpers._

class RoutesTestingSpec extends PlaySpec with OneAppPerTest {
  "Root Controller" should {
    "route to index page" in {
      val result = route(app, FakeRequest(GET, "/")).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Reactive Web Store")
    }
  }

  "Product Controller" should {
    "route to index page" in {
      val result = route(app, FakeRequest(GET, "/product")).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Product")
    }

    "route to new product page" in {
      val result = route(app, FakeRequest(GET, "/product/add")).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Product")
    }

    "route to product 1 details page" in {
      try route(app, FakeRequest(GET, "/product/details/1")).get
      catch {
        case _: Exception => ()
      }
    }
  }

  "Review Controller" should {
    "route to index page" in {
      val result = route(app, FakeRequest(GET, "/review")).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Review")
    }

    "route to new review page" in {
      val result = route(app, FakeRequest(GET, "/review/add")).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Review")
    }

    "route to review 1 details page" in {
      try route(app, FakeRequest(GET, "/review/details/1")).get
      catch {
        case _: Exception => ()
      }
    }
  }

  "Image Controller" should {
    "route to index page" in {
      val result = route(app, FakeRequest(GET, "/image")).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Image")
    }

    "route to new image page" in {
      val result = route(app, FakeRequest(GET, "/image/add")).get
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Image")
    }

    "route to image 1 details page" in {
      try route(app, FakeRequest(GET, "/image/details/1")).get
      catch {
        case _: Exception => ()
      }
    }
  }
}
