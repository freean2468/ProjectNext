package com.mirae.next.database

import slick.jdbc.MySQLProfile.api._

/** Slick에서 제공하는 Functional Relational Mapping(FRM)을 담은 object
 * 이 안에서 서비스에 필요한 테이블 쿼리를 정의한다.
 *
 */
object Tables {

  /** daily_table의 한 레코드를 모방한 case class
   *
   * @param date 날짜 String 타입인데 이전에 mysql date 타입에 맞게 변환이 되어야 한다.
   * @param open 시가 not null
   * @param high 고가 not null
   * @param low 저가 not null
   * @param close 종가 not null
   * @param volume 거래량 not null
   */
  case class Daily(date: String, open: Double, high: Double, low: Double, close: Double, volume: Int)

  /** db의 daily_table을 모방한 클래스
   *
   * @param tag 테이블 이름
   */
  class Dailies(tag: Tag) extends Table[Daily](tag, "daily_table") {
    /** Columns */
    def date = column[String]("date", O.PrimaryKey)
    def open = column[Double]("open")
    def high = column[Double]("high")
    def low = column[Double]("low")
    def close = column[Double]("close")
    def volume = column[Int]("volume")

    /** Every table needs a * projection with the same type as the table's type parameter */
    def * =
      (date, open, high, low, close, volume) <> (Daily.tupled, Daily.unapply)
  }

  /** dailies_table과의 쿼리를 담당할 변수
   *
   */
  val dailies = TableQuery[Dailies]
}