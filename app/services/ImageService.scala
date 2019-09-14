package services

import com.google.inject.{ Inject, Singleton }
import dao.ImageDao
import models.Image
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

trait ImageService extends BaseService[Image] {
  def insert(image: Image): Future[Unit]
  def update(id: Long, image: Image): Future[Unit]
  def remove(id: Long): Future[Int]
  def findById(id: Long): Future[Option[Image]]
  def findAll(): Future[Option[Seq[Image]]]
}

@Singleton
class ImageServiceImpl @Inject()(dao: ImageDao) extends ImageService {
  def insert(image: Image): Future[Unit] = dao.insert(image)

  def update(id: Long, image: Image): Future[Unit] = {
    image.id = Option(id.toInt)
    dao.update(image)
  }

  def remove(id: Long): Future[Int] = dao.remove(id)

  def findById(id: Long): Future[Option[Image]] = dao.findById(id)

  def findAll(): Future[Option[Seq[Image]]] = dao.findAll().map(Option(_))
}
