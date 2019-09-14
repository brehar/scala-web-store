package models

case class Review(
    var id: Option[Long],
    var productId: Option[Long],
    var author: String,
    var comment: String)
    extends BaseModel {
  def getId: Option[Long] = id

  def setId(id: Option[Long]): Unit = this.id = id

  override def toString: String =
    s"Review { id: ${id.getOrElse(0)}, productId: ${productId.getOrElse(0)}, author: $author, comment: $comment }"
}

object ReviewDef {
  final val toTable: String = "Review"
}

object ReviewsJson {
  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val reviewWrites: Writes[Review] =
    ((JsPath \ "id").write[Option[Long]] and (JsPath \ "productId")
      .write[Option[Long]] and (JsPath \ "author").write[String] and (JsPath \ "comment")
      .write[String])(unlift(Review.unapply))

  implicit val reviewReads: Reads[Review] =
    ((JsPath \ "id").readNullable[Long] and (JsPath \ "productId")
      .readNullable[Long] and (JsPath \ "author").read[String] and (JsPath \ "comment")
      .read[String])(Review.apply _)

  def toJson(reviews: Option[Seq[Review]]): JsValue = Json.toJson(reviews)
}
