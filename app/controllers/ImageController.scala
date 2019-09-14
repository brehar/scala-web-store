package controllers

import com.google.inject.{ Inject, Singleton }
import models.Image
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.iteratee.Enumerator
import play.api.mvc.{ Action, AnyContent, Controller }
import reports.ReportBuilder
import services.{ ImageService, ProductService }
import utils.Awaits

@Singleton
class ImageController @Inject()(
    val messagesApi: MessagesApi,
    val productService: ProductService,
    val service: ImageService)
    extends Controller
    with I18nSupport {
  val imageForm: Form[Image] = Form(
    mapping("id" -> optional(longNumber), "productId" -> optional(longNumber), "url" -> text)(
      Image.apply)(Image.unapply))

  def index: Action[AnyContent] = Action { implicit request =>
    val images = Awaits.get(5, service.findAll()).getOrElse(Seq())
    Ok(views.html.image_index(images))
  }

  def blank: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.image_details(None, imageForm, productService.findAllProducts()))
  }

  def details(id: Long): Action[AnyContent] = Action { implicit request =>
    val image = Awaits.get(5, service.findById(id)).get
    Ok(views.html.image_details(Some(id), imageForm.fill(image), productService.findAllProducts()))
  }

  def insert: Action[AnyContent] = Action { implicit request =>
    imageForm
      .bindFromRequest()
      .fold(
        form => BadRequest(views.html.image_details(None, form, productService.findAllProducts())),
        image =>
          if (image.productId == null || image.productId.isEmpty)
            Redirect(routes.ImageController.blank())
              .flashing("error" -> "Product ID cannot be null!")
          else {
            if (image.url == null || image.url == "")
              image.url = "/assets/images/default_product.png"

            service.insert(image)
            Redirect(routes.ImageController.index())
              .flashing("success" -> Messages("success.insert", "New image created!"))
        }
      )
  }

  def update(id: Long): Action[AnyContent] = Action { implicit request =>
    imageForm
      .bindFromRequest()
      .fold(
        form =>
          Ok(views.html.image_details(Some(id), form, null)).flashing("error" -> "Fix the errors!"),
        image => {
          service.update(id, image)
          Redirect(routes.ImageController.index())
            .flashing("success" -> Messages("success.update", image.id))
        }
      )
  }

  def remove(id: Long): Action[AnyContent] = Action { implicit request =>
    val result = Awaits.get(5, service.findById(id))

    result
      .map { image =>
        service.remove(id)
        Redirect(routes.ImageController.index())
          .flashing("success" -> Messages("success.delete", image.id))
      }
      .getOrElse(NotFound)
  }

  def report(): Action[AnyContent] = Action { implicit request =>
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    Ok.chunked(Enumerator.fromStream(ReportBuilder.toPdf("Images.jrxml")))
      .withHeaders(CONTENT_TYPE -> "application/octet-stream")
      .withHeaders(CONTENT_DISPOSITION -> "attachment; filename=report-images.pdf")
  }
}
