package controllers

import com.google.inject.{ Inject, Singleton }
import models.Review
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.iteratee.Enumerator
import play.api.mvc.{ Action, AnyContent, Controller }
import reports.ReportBuilder
import services.{ ProductService, ReviewService }
import utils.Awaits

@Singleton
class ReviewController @Inject()(
    val messagesApi: MessagesApi,
    val productService: ProductService,
    val service: ReviewService)
    extends Controller
    with I18nSupport {
  val reviewForm: Form[Review] = Form(
    mapping(
      "id" -> optional(longNumber),
      "productId" -> optional(longNumber),
      "author" -> nonEmptyText,
      "comment" -> nonEmptyText)(Review.apply)(Review.unapply))

  def index: Action[AnyContent] = Action { implicit request =>
    val reviews = Awaits.get(5, service.findAll()).getOrElse(Seq())
    Ok(views.html.review_index(reviews))
  }

  def blank: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.review_details(None, reviewForm, productService.findAllProducts()))
  }

  def details(id: Long): Action[AnyContent] = Action { implicit request =>
    val review = Awaits.get(5, service.findById(id)).get
    Ok(
      views.html
        .review_details(Some(id), reviewForm.fill(review), productService.findAllProducts()))
  }

  def insert: Action[AnyContent] = Action { implicit request =>
    reviewForm
      .bindFromRequest()
      .fold(
        form => BadRequest(views.html.review_details(None, form, productService.findAllProducts())),
        review =>
          if (review.productId == null || review.productId.isEmpty)
            Redirect(routes.ReviewController.blank())
              .flashing("error" -> "Product ID cannot be null!")
          else {
            service.insert(review)
            Redirect(routes.ReviewController.index())
              .flashing("success" -> Messages("success.insert", "New review created!"))
        }
      )
  }

  def update(id: Long): Action[AnyContent] = Action { implicit request =>
    reviewForm
      .bindFromRequest()
      .fold(
        form =>
          Ok(views.html.review_details(Some(id), form, productService.findAllProducts()))
            .flashing("error" -> "Fix the errors!"),
        review => {
          service.update(id, review)
          Redirect(routes.ReviewController.index())
            .flashing("success" -> Messages("success.update", review.id))
        }
      )
  }

  def remove(id: Long): Action[AnyContent] = Action { implicit request =>
    val result = Awaits.get(5, service.findById(id))

    result
      .map { review =>
        service.remove(id)
        Redirect(routes.ReviewController.index())
          .flashing("success" -> Messages("success.delete", review.id))
      }
      .getOrElse(NotFound)
  }

  def report(): Action[AnyContent] = Action { implicit request =>
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    Ok.chunked(Enumerator.fromStream(ReportBuilder.toPdf("Reviews.jrxml")))
      .withHeaders(CONTENT_TYPE -> "application/octet-stream")
      .withHeaders(CONTENT_DISPOSITION -> "attachment; filename=report-reviews.pdf")
  }
}
