package controllers

import com.google.inject.{ Inject, Singleton }
import models.{ ImagesJson, ProductsJson, ReviewsJson }
import play.api.mvc.{ Action, AnyContent, Controller }
import services.{ ImageService, ProductService, ReviewService }
import utils.Awaits

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

  def listAllImages: Action[AnyContent] = Action { implicit request =>
    val future = imageService.findAll()
    val images = Awaits.get(5, future)
    val json = ImagesJson.toJson(images)
    Ok(json)
  }
}
