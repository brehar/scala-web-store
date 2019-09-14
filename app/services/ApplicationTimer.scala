package services

import java.time.{ Clock, Instant }

import com.google.inject.{ Inject, Singleton }
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class ApplicationTimer @Inject()(clock: Clock, appLifecycle: ApplicationLifecycle) {
  private val start: Instant = clock.instant()
  Logger.info(s"Starting application at: $start.")

  appLifecycle.addStopHook { () =>
    val stop: Instant = clock.instant()
    val runningTime: Long = stop.getEpochSecond - start.getEpochSecond
    Logger.info(s"Stopping application at $stop after ${runningTime}s.")

    Future.successful(())
  }
}
