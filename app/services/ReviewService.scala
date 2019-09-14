package services

import com.google.inject.{ Inject, Singleton }
import dao.ReviewDao
import models.Review
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

trait ReviewService extends BaseService[Review] {
  def insert(review: Review): Future[Unit]
  def update(id: Long, review: Review): Future[Unit]
  def remove(id: Long): Future[Int]
  def findById(id: Long): Future[Option[Review]]
  def findAll(): Future[Option[Seq[Review]]]
}

@Singleton
class ReviewServiceImpl @Inject()(dao: ReviewDao) extends ReviewService {
  def insert(review: Review): Future[Unit] = dao.insert(review)

  def update(id: Long, review: Review): Future[Unit] = {
    review.id = Option(id.toInt)
    dao.update(review)
  }

  def remove(id: Long): Future[Int] = dao.remove(id)

  def findById(id: Long): Future[Option[Review]] = dao.findById(id)

  def findAll(): Future[Option[Seq[Review]]] = dao.findAll().map(Option(_))
}
