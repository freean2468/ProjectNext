package com.mirae.next.database

import com.mirae.next.database.Tables._
import org.scalatra.{ActionResult, Ok}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}

/** Tables의 모방 테이블을 활용한 쿼리문 지원하는 trait, Route class에 mixing in!
 *
 * @param db 첫 servlet 생성 시 전달된 db config
 */
trait QuerySupport {
  import scala.concurrent.ExecutionContext.Implicits.global
  def db: Database

  /** Create
   *
   */
  def insert(daily: Daily) = db.run(dailies += daily)
  def insert(ticker: Ticker) = db.run(tickers += ticker)

  def insertDailyList(dailyList: List[Daily]) = {
    val action = DBIO.seq(
      dailies ++= dailyList
    )
    db.run(action)
  }

  def insertDaily(daily: Daily) = {
    val prom=Promise[ActionResult]()
    insert(daily) onComplete {
      case Success(s) => prom.complete(Try(Ok(s)))
      case Failure(e) => prom.failure(e)
    }
    prom.future
  }

  def insertTicker(ticker: Ticker) = {
    val prom = Promise[ActionResult]()
    insert(ticker) onComplete {
      case Success(s) => prom.complete(Try(Ok(s)))
      case Failure(e) => prom.failure(e)
    }
    prom.future
  }

  /** Read
   *
   */
  def selectTicker(ticker: String, year: Int) =
    db.run(tickers.filter(_.ticker === ticker).filter(_.year === year).result.headOption)

  def selectDaily(ticker: String) =
    db.run(dailies.filter(_.ticker === ticker).result)

  def selectTickerAll() =
    db.run(tickers.result)

  def selectOnlyTickers() = {
    val prom = Promise[ActionResult]()
    val logger = LoggerFactory.getLogger(getClass)
    db.run(tickers.result) onComplete {
      case Failure(e) => prom.failure(e)
      case Success(s) => {
        val list = (for (t <- s) yield ("ticker" -> t.ticker))
//        logger.info(list.toSet.toString())
        prom.complete(Try(Ok(list.toSet)))
      }
    }
    prom.future
  }

  def selectDailyAll() =
    db.run(dailies.result)

  def isTickerDates(ticker: String, year: Int) = {
    val prom = Promise[ActionResult]()

    /** db.run(sql"""select count(*) from daily_table where date <= "2019-12-30" and ticker=${ticker}""".as[Int])
     *  slick에서 date 연산이 정상 작동하지 않는다.
     */
    selectTicker(ticker, year) onComplete {
      case Failure(e) => prom.failure(e)
      case Success(s) => {
        s match {
          case Some(t) => prom.complete(Try(Ok(1)))
          case None => prom.complete(Try(Ok(0)))
        }
      }
    }
    prom.future
  }

  // def find(no: String) = db.run((for (account <- accounts if account.no === no) yield account).result.headOption) // imperative way
  def findDaily(date: String) =
    db.run(dailies.filter(_.date === date).result.headOption)

  /** Update
   *
   */


  /** Delete
   *
   */
  def delete(date: String): Unit = {
    val deleteAction = (dailies filter { _.date like "%"+date+"%" }).delete
    db.run(deleteAction)
  }
}
