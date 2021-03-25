package com.mirae.next

import com.mirae.next.database.QuerySupport
import com.mirae.next.database.Tables.Daily
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}

trait ServiceRoute extends ScalatraBase with JacksonJsonSupport with FutureSupport with QuerySupport with FileUploadSupport {
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3*1024*1024)))
  /** Sets up automatic case class to JSON output serialization, required by the JValueResult trait. */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database
  get("/") {
    views.html.hello()
  }

  /**
   *
   */
  post("/insert") {
    val logger = LoggerFactory.getLogger(getClass)

    new AsyncResult { override val is =
      Future {
        contentType = ""
        val date = params.getOrElse("date", "")
        val open = params.getOrElse("open", "").toDouble
        val high = params.getOrElse("high", "").toDouble
        val low = params.getOrElse("low", "").toDouble
        val close = params.getOrElse("close", "").toDouble
        val volume = params.getOrElse("volume", "").toInt

        insertDaily(Daily(date, open, high, low, close, volume))
      }
    }
  }

  error {
//    case e: NoSuchElementException => e.printStackTrace()
//    case e: NumberFormatException => e.printStackTrace()
    case e: Exception => <p>error! in service route</p>
  }
}

/** 서비스를 제공하는 routing 클래스
 *
 * @param db config 정보
 */
class ServiceRouteServlet (val db: Database) extends ScalatraServlet with ServiceRoute {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}