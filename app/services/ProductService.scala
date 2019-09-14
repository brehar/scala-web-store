package services

import com.google.inject.{ Inject, Singleton }
import dao.ProductDao
import models.Product
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.Awaits

import scala.concurrent.Future

trait ProductService extends BaseService[Product] {
  def insert(product: Product): Future[Unit]
  def update(id: Long, product: Product): Future[Unit]
  def remove(id: Long): Future[Int]
  def findById(id: Long): Future[Option[Product]]
  def findAll(): Future[Option[Seq[Product]]]
  def findAllProducts(): Seq[(String, String)]
}

@Singleton
class ProductServiceImpl @Inject()(dao: ProductDao) extends ProductService {
  def insert(product: Product): Future[Unit] = dao.insert(product)

  def update(id: Long, product: Product): Future[Unit] = {
    product.id = Option(id.toInt)
    dao.update(product)
  }

  def remove(id: Long): Future[Int] = dao.remove(id)

  def findById(id: Long): Future[Option[Product]] = dao.findById(id)

  def findAll(): Future[Option[Seq[Product]]] = dao.findAll().map(Option(_))

  def findAllProducts(): Seq[(String, String)] = {
    val future = findAll()
    val result = Awaits.get(5, future)
    result
      .getOrElse(Seq(Product(Some(0), "", "", 0)))
      .map(product => (product.id.get.toString, product.name))
  }
}
