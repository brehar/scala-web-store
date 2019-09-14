package dao

import com.google.inject.{ Inject, Singleton }
import models.{ Image, ImageDef }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

trait ImageDao extends BaseDao[Image] {
  def findAll(): Future[Seq[Image]]
  def findById(id: Long): Future[Option[Image]]
  def remove(id: Long): Future[Int]
  def insert(i: Image): Future[Unit]
  def update(i2: Image): Future[Unit]
}

@Singleton
class ImageDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile]
    with ImageDao {
  import driver.api._

  class ImageTable(tag: Tag) extends Table[Image](tag, ImageDef.toTable) {
    def id: Rep[Option[Long]] = column[Option[Long]]("ID", O.PrimaryKey)
    def productId: Rep[Option[Long]] = column[Option[Long]]("PRODUCT_ID")
    def url: Rep[String] = column[String]("URL")

    def * : ProvenShape[Image] = (id, productId, url) <> (Image.tupled, Image.unapply)
  }

  def toTable: TableQuery[ImageTable] = TableQuery[ImageTable]

  private val images = toTable

  def findAll(): Future[Seq[Image]] = db.run(images.result)

  def findById(id: Long): Future[Option[Image]] =
    db.run(images.filter(_.id === id).result.headOption)

  def remove(id: Long): Future[Int] = db.run(images.filter(_.id === id).delete)

  def insert(i: Image): Future[Unit] = db.run(images += i).map(_ => ())

  def update(i2: Image): Future[Unit] = Future[Unit] {
    db.run(
      images.filter(_.id === i2.id).map(i => (i.productId, i.url)).update((i2.productId, i2.url)))
  }
}
