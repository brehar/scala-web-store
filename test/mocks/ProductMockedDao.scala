package mocks

import dao.ProductDao
import models.Product
import slick.lifted.TableQuery

import scala.concurrent.Future

class ProductMockedDao extends ProductDao {
  val dao: GenericMockedDao[Product] = new GenericMockedDao[Product]()

  def findAll(): Future[Seq[Product]] = dao.findAll()

  def findById(id: Long): Future[Option[Product]] = dao.findById(id)

  def remove(id: Long): Future[Int] = dao.remove(id)

  def insert(p: Product): Future[Unit] = dao.insert(p)

  def update(p2: Product): Future[Unit] = dao.update(p2)

  def toTable: TableQuery[_] = null
}
