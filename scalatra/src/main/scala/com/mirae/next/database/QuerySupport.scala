package com.mirae.next.database

import com.mirae.next.database.Tables._
import org.scalatra.{ActionResult, Ok}
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import java.sql.Date
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
  def selectTicker(ticker: String) =
    db.run(tickers.filter(_.ticker === ticker).result)

  def selectDaily(ticker: String) =
    db.run(dailies.filter(_.ticker === ticker).result)

  def selectTickerAll() =
    db.run(tickers.result)

  def selectDailyAll() =
    db.run(dailies.result)

  def selectCountTickerDates(ticker: String, year: String) = {
    val prom = Promise[ActionResult]()
    val logger = LoggerFactory.getLogger(getClass)

//    db.run(sql"select * from account_table".as[(String, String)])
    db.run(sql"select count(*) from daily_table where date >= ${year}-01-01".as[String]) onComplete {
      case Failure(e) => prom.failure(e)
      case Success(s) => {
        if (s(0).toInt < 250) {
          prom.complete(Try(Ok(0)))
        } else {
          db.run(sql"select count(*) from daily_table where date <= ${year}-12-31".as[String]) onComplete {
            case Failure(e) => prom.failure(e)
            case Success(s2) => {
              val count = s2(0).toInt
              if (count < 250)
                prom.complete(Try(Ok(0)))
              else
                prom.complete(Try(Ok(count)))
            }
          }
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
