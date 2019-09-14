package controllers

import com.google.inject.{ Inject, Singleton }
import models.Product
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.iteratee.Enumerator
import play.api.mvc.{ Action, AnyContent, Controller }
import reports.ReportBuilder
import services.ProductService
import utils.Awaits

@Singleton
class ProductController @Inject()(val messagesApi: MessagesApi, val service: ProductService)
    extends Controller
    with I18nSupport {
  val productForm: Form[Product] = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "details" -> text,
      "price" -> bigDecimal)(Product.apply)(Product.unapply))

  def index: Action[AnyContent] = Action { implicit request =>
    val products = Awaits.get(5, service.findAll()).getOrElse(Seq())
    Ok(views.html.product_index(products))
  }

  def blank: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.product_details(None, productForm))
  }

  def details(id: Long): Action[AnyContent] = Action { implicit request =>
    val product = Awaits.get(5, service.findById(id)).get
    Ok(views.html.product_details(Some(id), productForm.fill(product)))
  }

  def insert: Action[AnyContent] = Action { implicit request =>
    productForm
      .bindFromRequest()
      .fold(
        form => BadRequest(views.html.product_details(None, form)),
        product => {
          service.insert(product)
          Redirect(routes.ProductController.index())
            .flashing("success" -> Messages("success.insert", "New product created!"))
        }
      )
  }

  def update(id: Long): Action[AnyContent] = Action { implicit request =>
    productForm
      .bindFromRequest()
      .fold(
        form =>
          Ok(views.html.product_details(Some(id), form)).flashing("error" -> "Fix the errors!"),
        product => {
          service.update(id, product)
          Redirect(routes.ProductController.index())
            .flashing("success" -> Messages("success.update", product.name))
        }
      )
  }

  def remove(id: Long): Action[AnyContent] = Action { implicit request =>
    val result = Awaits.get(5, service.findById(id))

    result
      .map { product =>
        service.remove(id)
        Redirect(routes.ProductController.index())
          .flashing("success" -> Messages("success.delete", product.name))
      }
      .getOrElse(NotFound)
  }

  def report(): Action[AnyContent] = Action { implicit request =>
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    Ok.chunked(Enumerator.fromStream(ReportBuilder.toPdf("Products.jrxml")))
      .withHeaders(CONTENT_TYPE -> "application/octet-stream")
      .withHeaders(CONTENT_DISPOSITION -> "attachment; filename=report-products.pdf")
  }
}
