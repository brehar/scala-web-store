package controllers

import backpressure.LeakyBucket
import com.google.inject.{ Inject, Singleton }
import models.{ ImagesJson, ProductsJson, ReviewsJson }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, Controller }
import services.{ ImageService, ProductService, ReviewService }
import utils.Awaits

import scala.concurrent.duration._

@Singleton
class RestApiController @Inject()(
    val productService: ProductService,
    val reviewService: ReviewService,
    val imageService: ImageService)
    extends Controller {
  def listAllProducts: Action[AnyContent] = Action { implicit request =>
    val future = productService.findAll()
    val products = Awaits.get(5, future)
    val json = ProductsJson.toJson(products)
    Ok(json)
  }

  def listAllReviews: Action[AnyContent] = Action { implicit request =>
    val future = reviewService.findAll()
    val reviews = Awaits.get(5, future)
    val json = ReviewsJson.toJson(reviews)
    Ok(json)
  }

  var bucket = new LeakyBucket(5, 60 seconds)

  def processImages: JsValue = {
    val future = imageService.findAll()
    val images = Awaits.get(5, future)
    ImagesJson.toJson(images)
  }

  def processFailure: JsValue = Json.toJson("Too many requests. Please try again later.")

  def listAllImages: Action[AnyContent] = Action { implicit request =>
    if (bucket.dropToBucket()) Ok(processImages)
    else InternalServerError(processFailure.toString())
  }
}
