package mocks

import dao.ImageDao
import models.Image
import slick.lifted.TableQuery

import scala.concurrent.Future

class ImageMockedDao extends ImageDao {
  val dao: GenericMockedDao[Image] = new GenericMockedDao[Image]()

  def findAll(): Future[Seq[Image]] = dao.findAll()

  def findById(id: Long): Future[Option[Image]] = dao.findById(id)

  def remove(id: Long): Future[Int] = dao.remove(id)

  def insert(i: Image): Future[Unit] = dao.insert(i)

  def update(i2: Image): Future[Unit] = dao.update(i2)

  def toTable: TableQuery[_] = null
}
