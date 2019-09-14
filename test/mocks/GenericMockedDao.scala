package mocks

import java.util.concurrent.atomic.AtomicLong

import models.BaseModel
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.collection.mutable
import scala.concurrent.Future

class GenericMockedDao[T <: BaseModel] {
  var inMemoryDb = new mutable.HashMap[Long, T]()
  var idCounter = new AtomicLong(0)

  def findAll(): Future[Seq[T]] = Future {
    if (inMemoryDb.isEmpty) Seq()
    else inMemoryDb.values.toSeq
  }

  def findById(id: Long): Future[Option[T]] = Future {
    inMemoryDb.get(id)
  }

  def remove(id: Long): Future[Int] = Future {
    validateId(id)
    inMemoryDb.remove(id)
    1
  }

  def insert(t: T): Future[Unit] = Future {
    val id = idCounter.incrementAndGet()
    t.setId(Some(id))
    inMemoryDb.put(id, t)
    ()
  }

  def update(t: T): Future[Unit] = Future {
    validateId(t.getId.get)
    inMemoryDb.put(t.getId.get, t)
    ()
  }

  private def validateId(id: Long): Unit = {
    val entry = inMemoryDb.get(id)

    if (entry == null || entry.isEmpty) throw new RuntimeException("Could not find product: " + id)
  }
}
