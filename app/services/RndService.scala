package services

import com.google.inject.{ Inject, Singleton }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.util.{ Failure, Success }

trait RndService {
  def next(): Double
  def call(): Future[String]
  def rxScalaCall(): Observable[Double]
  def rxScalaCallBatch(): Observable[Double]
}

@Singleton
class RndServiceImpl @Inject()(ws: WSClient) extends RndService {
  def next(): Double = {
    val future = ws.url("http://localhost:9090/double").get().map(_.body.toDouble)

    Await.result(future, 5.seconds)
  }

  def call(): Future[String] = ws.url("http://localhost:9090/double").get().map(_.body)

  def rxScalaCall(): Observable[Double] = {
    val doubleFuture: Future[Double] =
      ws.url("http://localhost:9090/double").get().map(_.body.toDouble)

    Observable.from(doubleFuture)
  }

  def rxScalaCallBatch(): Observable[Double] = {
    val doubleInfiniteStreamSubject = PublishSubject.apply[Double]()
    val future =
      ws.url("http://localhost:9090/doubles/10").get().map(x => Json.parse(x.body).as[List[Double]])
    future.onComplete {
      case Success(l: List[Double]) =>
        l.foreach { e =>
          doubleInfiniteStreamSubject.onNext(e)
        }
      case Failure(e: Exception) => doubleInfiniteStreamSubject.onError(e)
    }

    val observableEven = Observable
      .create(doubleInfiniteStreamSubject.subscribe)
      .onErrorReturn(_ => 2.0)
      .flatMap { x =>
        Observable.from(Iterable.fill(1)(x + 10))
      }
      .filter(_.toInt % 2 == 0)
      .flatMap(x => Observable.just(x))

    val observableOdd = Observable
      .create(doubleInfiniteStreamSubject.subscribe)
      .onErrorReturn(_ => 1.0)
      .flatMap { x =>
        Observable.from(Iterable.fill(1)(x + 10))
      }
      .filter(_.toInt % 2 != 0)
      .flatMap(x => Observable.just(x))

    Observable.empty
      .merge(observableEven)
      .merge(observableOdd)
      .take(10)
      .foldLeft(0.0)(_ + _)
      .flatMap { x =>
        Observable.just(x - (x * 0.9))
      }
  }
}
