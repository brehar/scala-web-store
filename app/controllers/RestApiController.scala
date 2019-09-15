package controllers

import backpressure.LeakyBucket
import com.google.inject.{ Inject, Singleton }
import io.swagger.annotations.{ Api, ApiOperation, ApiResponse, ApiResponses }
import models.{ Image, ImagesJson, Product, ProductsJson, Review, ReviewsJson }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, Controller }
import services.{ ImageService, ProductService, ReviewService }
import utils.Awaits

import scala.concurrent.duration._

@Singleton
@Api(value = "/api", description = "REST operations on products, reviews, and images.")
class RestApiController @Inject()(
    val productService: ProductService,
    val reviewService: ReviewService,
    val imageService: ImageService)
    extends Controller {
  @ApiOperation(
    nickname = "listAllProducts",
    value = "Find All Products",
    notes = "Returns all products.",
    response = classOf[Product],
    httpMethod = "GET")
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal Server Error"),
      new ApiResponse(code = 200, message = "JSON response with data")))
  def listAllProducts: Action[AnyContent] = Action { implicit request =>
    val future = productService.findAll()
    val products = Awaits.get(5, future)
    val json = ProductsJson.toJson(products)
    Ok(json)
  }

  @ApiOperation(
    nickname = "listAllReviews",
    value = "Find All Reviews",
    notes = "Returns all reviews.",
    response = classOf[Review],
    httpMethod = "GET")
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal Server Error"),
      new ApiResponse(code = 200, message = "JSON response with data")))
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

  @ApiOperation(
    nickname = "listAllImages",
    value = "Find All Images",
    notes = "Returns all images with throttling of 5 requests/second.",
    response = classOf[Image],
    httpMethod = "GET"
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal Server Error"),
      new ApiResponse(code = 200, message = "JSON response with data")))
  def listAllImages: Action[AnyContent] = Action { implicit request =>
    if (bucket.dropToBucket()) Ok(processImages)
    else InternalServerError(processFailure.toString())
  }
}
