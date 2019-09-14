package services

import com.google.inject.Singleton
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler
import rx.lang.scala.subjects.PublishSubject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random.nextDouble

trait PriceService {
  def generatePrices: Observable[Double]
}

@Singleton
class PriceServiceImpl extends PriceService {
  var doubleInfiniteStreamSubject = PublishSubject.apply[Double]()

  Future {
    Stream.continually(nextDouble * 1000.0).foreach { x =>
      Thread.sleep(1000)
      doubleInfiniteStreamSubject.onNext(x)
    }
  }

  def generatePrices: Observable[Double] = {
    val observableEven = Observable
      .create(doubleInfiniteStreamSubject.subscribe)
      .subscribeOn(IOScheduler())
      .flatMap { x =>
        Observable.from(Iterable.fill(1)(x + 10))
      }
      .filter(_.toInt % 2 == 0)

    val observableOdd = Observable
      .create(doubleInfiniteStreamSubject.subscribe)
      .subscribeOn(IOScheduler())
      .flatMap { x =>
        Observable.from(Iterable.fill(1)(x + 10))
      }
      .filter(_.toInt % 2 != 0)

    Observable.empty
      .subscribeOn(IOScheduler())
      .merge(observableEven)
      .merge(observableOdd)
      .take(10)
      .foldLeft(0.0)(_ + _)
      .flatMap { x =>
        Observable.just(x - (x * 0.9))
      }
  }
}
