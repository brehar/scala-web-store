package models

case class Image(var id: Option[Long], var productId: Option[Long], var url: String)
    extends BaseModel {
  def getId: Option[Long] = id

  def setId(id: Option[Long]): Unit = this.id = id

  override def toString: String =
    s"Image { id: ${id.getOrElse(0)}, productId: ${productId.getOrElse(0)}, url: $url }"
}

object ImageDef {
  final val toTable: String = "Image"
}

object ImagesJson {
  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val imageWrites: Writes[Image] =
    ((JsPath \ "id").write[Option[Long]] and (JsPath \ "productId")
      .write[Option[Long]] and (JsPath \ "url").write[String])(unlift(Image.unapply))

  implicit val imageReads: Reads[Image] =
    ((JsPath \ "id").readNullable[Long] and (JsPath \ "productId")
      .readNullable[Long] and (JsPath \ "url").read[String])(Image.apply _)

  def toJson(images: Option[Seq[Image]]): JsValue = Json.toJson(images)
}
