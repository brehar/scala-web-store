package utils

import java.util.concurrent.{ ExecutorService, Executors }

import slick.dbio.DBIO
import slick.driver.MySQLDriver.api._
import slick.driver.MySQLDriver.backend.Database

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, ExecutionContextExecutorService }

object DBCleaner {
  val pool: ExecutorService = Executors.newCachedThreadPool()

  implicit val ec: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(pool)

  def cleanUp(): Unit = {
    Class.forName("com.mysql.cj.jdbc.Driver")

    val db = Database.forURL(
      "jdbc:mysql://127.0.0.1:3306/RWS_DB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=PST8PDT",
      "root",
      sys.env("MYSQL_ROOT_PASSWORD")
    )

    try Await.result(
      db.run(
        DBIO.seq(
          sqlu"DELETE FROM Product;",
          sqlu"DELETE FROM Image;",
          sqlu"DELETE FROM Review;",
          sqlu"ALTER TABLE Product AUTO_INCREMENT = 1",
          sqlu"ALTER TABLE Image AUTO_INCREMENT = 1",
          sqlu"ALTER TABLE Review AUTO_INCREMENT = 1"
        )),
      20 seconds
    )
    catch {
      case _: Exception => ()
    }
  }
}
