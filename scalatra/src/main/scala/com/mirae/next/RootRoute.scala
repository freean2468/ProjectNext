package com.mirae.next

import com.mirae.next.database.QuerySupport
import com.mirae.next.database.Tables.{Daily, Ticker}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database

import java.sql.SQLIntegrityConstraintViolationException
import scala.concurrent.{ExecutionContext, Future}

trait RootRoute extends ScalatraBase with JacksonJsonSupport with FutureSupport with QuerySupport with FileUploadSupport {
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3*1024*1024)))
  /** Sets up automatic case class to JSON output serialization, required by the JValueResult trait. */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database

  /** get
   *
   */

  get("/") {
    <p>project next 서버에 오신 것을 환영합니다.</p>
    <p>다음과 같은 경로에 서비스가 제공됩니다.</p>
    <p> get : /daily : daily정보를 서버로부터 불러옵니다.</p>
    <p>post : /daily : daily정보를 db에 저장합니다.</p>
  }



  /** post
   *
   */
  post("/ticker/:none") {
    new AsyncResult { override val is =
      Future {
        contentType = ""
        val ticker = params.getOrElse("ticker", "")
        insertTicker(Ticker(ticker))
      }
    }
  }

  post("/daily/:none") {
    val logger = LoggerFactory.getLogger(getClass)

    new AsyncResult { override val is =
      Future {
        contentType = ""
        val ticker = params.getOrElse("ticker", "")
        val date = params.getOrElse("date", "")
        val open = params.getOrElse("open", "").toDouble
        val high = params.getOrElse("high", "").toDouble
        val low = params.getOrElse("low", "").toDouble
        val close = params.getOrElse("close", "").toDouble
        val volume = params.getOrElse("volume", "").toInt

        insertDaily(Daily(ticker, date, open, high, low, close, volume))
      }
    }
  }

  error {
//    case e: NoSuchElementException => e.printStackTrace()
//    case e: NumberFormatException => e.printStackTrace()
    case e: SQLIntegrityConstraintViolationException => {
      e.printStackTrace()
      <p>constraint violation!</p>
    }
    case e: Exception => {
      e.printStackTrace()
      <p>error! in service route</p>
    }
  }
}

/** 서비스를 제공하는 routing 클래스
 *
 * @param db config 정보
 */
class RootRouteServlet (val db: Database) extends ScalatraServlet with RootRoute {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}