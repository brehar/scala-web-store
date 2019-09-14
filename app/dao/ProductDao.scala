package dao

import com.google.inject.{ Inject, Singleton }
import models.{ Product, ProductDef }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

trait ProductDao extends BaseDao[Product] {
  def findAll(): Future[Seq[Product]]
  def findById(id: Long): Future[Option[Product]]
  def remove(id: Long): Future[Int]
  def insert(p: Product): Future[Unit]
  def update(p2: Product): Future[Unit]
}

@Singleton
class ProductDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile]
    with ProductDao {
  import driver.api._

  class ProductTable(tag: Tag) extends Table[Product](tag, ProductDef.toTable) {
    def id: Rep[Option[Long]] = column[Option[Long]]("ID", O.PrimaryKey)
    def name: Rep[String] = column[String]("NAME")
    def details: Rep[String] = column[String]("DETAILS")
    def price: Rep[BigDecimal] = column[BigDecimal]("PRICE")

    def * : ProvenShape[Product] = (id, name, details, price) <> (Product.tupled, Product.unapply)
  }

  def toTable: TableQuery[ProductTable] = TableQuery[ProductTable]

  private val products = toTable

  def findAll(): Future[Seq[Product]] = db.run(products.result)

  def findById(id: Long): Future[Option[Product]] =
    db.run(products.filter(_.id === id).result.headOption)

  def remove(id: Long): Future[Int] = db.run(products.filter(_.id === id).delete)

  def insert(p: Product): Future[Unit] = db.run(products += p).map(_ => ())

  def update(p2: Product): Future[Unit] = Future[Unit] {
    db.run(
      products
        .filter(_.id === p2.id)
        .map(p => (p.name, p.details, p.price))
        .update((p2.name, p2.details, p2.price)))
  }
}
