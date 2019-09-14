package models

case class Product(
    var id: Option[Long],
    var name: String,
    var details: String,
    var price: BigDecimal)
    extends BaseModel {
  def getId: Option[Long] = id

  def setId(id: Option[Long]): Unit = this.id = id

  override def toString: String =
    s"Product { id: ${id.getOrElse(0)}, name: $name, details: $details, price: $price }"
}

object ProductDef {
  final val toTable: String = "Product"
}
