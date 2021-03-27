package com.mirae.next.database

import com.mirae.next.database.Tables._
import org.scalatra.{ActionResult, Ok}
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
  def selectTicker(ticker: String) =
    db.run(tickers.filter(_.ticker === ticker).result)

  def selectDaily(ticker: String) =
    db.run(dailies.filter(_.ticker === ticker).result)

  def selectTickerAll() =
    db.run(tickers.result)

  def selectDailyAll() =
    db.run(dailies.result)

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
