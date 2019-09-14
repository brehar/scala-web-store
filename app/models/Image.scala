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
