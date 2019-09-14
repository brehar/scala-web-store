package controllers

import play.api.mvc._

class HomeController extends Controller {
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def reports: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.reports_index())
  }
}
