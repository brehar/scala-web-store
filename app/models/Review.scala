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
