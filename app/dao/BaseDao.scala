package dao

import slick.lifted.TableQuery

import scala.concurrent.Future

trait BaseDao[T] {
  def toTable: TableQuery[_]
  def findAll(): Future[Seq[T]]
  def remove(id: Long): Future[Int]
  def insert(o: T): Future[Unit]
  def update(o2: T): Future[Unit]
  def findById(id: Long): Future[Option[T]]
}
