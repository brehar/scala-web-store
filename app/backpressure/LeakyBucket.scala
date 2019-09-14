package backpressure

import java.util.Date

import scala.concurrent.duration._

class LeakyBucket(var rate: Int, var perDuration: FiniteDuration) {
  var numDropsInBucket: Int = 0
  var timeOfLastDropLeak: Date = _
  var msDropLeaks: Long = perDuration.toMillis

  def dropToBucket(): Boolean = synchronized {
    val now = new Date()

    if (timeOfLastDropLeak != null) {
      val deltaT = now.getTime - timeOfLastDropLeak.getTime
      val numberToLeak: Long = deltaT / msDropLeaks

      if (numberToLeak > 0) {
        if (numDropsInBucket <= numberToLeak) numDropsInBucket -= numberToLeak.toInt
        else numDropsInBucket = 0

        timeOfLastDropLeak = now
      }
    } else timeOfLastDropLeak = now

    if (numDropsInBucket < rate) {
      numDropsInBucket += 1
      return true
    }

    return false
  }
}
