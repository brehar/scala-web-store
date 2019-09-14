package dao

import com.google.inject.{ Inject, Singleton }
import models.{ Review, ReviewDef }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

trait ReviewDao extends BaseDao[Review] {
  def findAll(): Future[Seq[Review]]
  def findById(id: Long): Future[Option[Review]]
  def remove(id: Long): Future[Int]
  def insert(r: Review): Future[Unit]
  def update(r2: Review): Future[Unit]
}

@Singleton
class ReviewDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends HasDatabaseConfigProvider[JdbcProfile]
    with ReviewDao {
  import driver.api._

  class ReviewTable(tag: Tag) extends Table[Review](tag, ReviewDef.toTable) {
    def id: Rep[Option[Long]] = column[Option[Long]]("ID", O.PrimaryKey)
    def productId: Rep[Option[Long]] = column[Option[Long]]("PRODUCT_ID")
    def author: Rep[String] = column[String]("AUTHOR")
    def comment: Rep[String] = column[String]("COMMENT")

    def * : ProvenShape[Review] =
      (id, productId, author, comment) <> (Review.tupled, Review.unapply)
  }

  def toTable: TableQuery[ReviewTable] = TableQuery[ReviewTable]

  private val reviews = toTable

  def findAll(): Future[Seq[Review]] = db.run(reviews.result)

  def findById(id: Long): Future[Option[Review]] =
    db.run(reviews.filter(_.id === id).result.headOption)

  def remove(id: Long): Future[Int] = db.run(reviews.filter(_.id === id).delete)

  def insert(r: Review): Future[Unit] = db.run(reviews += r).map(_ => ())

  def update(r2: Review): Future[Unit] = Future[Unit] {
    db.run(
      reviews
        .filter(_.id === r2.id)
        .map(r => (r.productId, r.author, r.comment))
        .update((r2.productId, r2.author, r2.comment)))
  }
}
