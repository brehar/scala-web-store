package mocks

import dao.ReviewDao
import models.Review
import slick.lifted.TableQuery

import scala.concurrent.Future

class ReviewMockedDao extends ReviewDao {
  val dao: GenericMockedDao[Review] = new GenericMockedDao[Review]()

  def findAll(): Future[Seq[Review]] = dao.findAll()

  def findById(id: Long): Future[Option[Review]] = dao.findById(id)

  def remove(id: Long): Future[Int] = dao.remove(id)

  def insert(r: Review): Future[Unit] = dao.insert(r)

  def update(r2: Review): Future[Unit] = dao.update(r2)

  def toTable: TableQuery[_] = null
}
