package com.mirae.next

import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.temporal.TemporalAdjusters
import java.time.{LocalDate, Year}
import java.util.{Calendar, Date}

/** 각종 기능의 함수들을 모아놓은 object
 *
 */
object Util {
  /** 각 달의 첫 일과 마지막 일을 dayOfYear 기준으로 반환한다.
   *
   * @param month 특정 달(1~12)
   * @return List.head == 각 달의 첫 일의 dayOfYear, last == 각 달의 마지막 일의 dayOfYear
   */
  def getDaysWithMonth(month:Int) = {
    val firstAdjuster = TemporalAdjusters.firstDayOfMonth
    val lastAdjuster = TemporalAdjusters.lastDayOfMonth

    // using adjuster for local date-time
    val localDate = LocalDate.of(Year.now.getValue, month, 1)
    val firstDayOfMonth = localDate.`with`(firstAdjuster)
    val lastDayOfMonth = localDate.`with`(lastAdjuster)

    List(convertLocalDateToDate(firstDayOfMonth), convertLocalDateToDate(lastDayOfMonth))
  }

  /** 현재 시스템 시간을 기준으로 현재 달의 첫 일과 마지막 일을 dayOfYear 기준으로 반환한다.
   *
   * @return List.head == 현재 달의 첫 일의 dayOfYear, last == 현재 달의 마지막 일의 dayOfYear
   */
  def getDaysWithNow() = {
    val firstAdjuster = TemporalAdjusters.firstDayOfMonth
    val lastAdjuster = TemporalAdjusters.lastDayOfMonth

    // using adjuster for local date-time
    val localDate = LocalDate.now()
    val firstDayOfMonth = localDate.`with`(firstAdjuster)
    val lastDayOfMonth = localDate.`with`(lastAdjuster)

    List(convertLocalDateToDate(firstDayOfMonth), convertLocalDateToDate(lastDayOfMonth))
  }

  /** LocalDate 타입을 Date 타입으로 변경해주는 함수
   *
   * @param localDate 변경하려는 LocalDate
   * @return 변환된 Date
   */
  def convertLocalDateToDate(localDate: LocalDate) = {
    import java.time.ZoneId

    val defaultZoneId = ZoneId.systemDefault
    val cal = Calendar.getInstance()
    cal.setTime(Date.from(localDate.atStartOfDay(defaultZoneId).toInstant()))
    cal.get(Calendar.DAY_OF_YEAR)
  }

  def convertBytesArrayToString(bytesArray : Array[Byte]) = {
    var s : String = null
    if (bytesArray.length > 0) {
      val logger = LoggerFactory.getLogger(getClass)
      logger.info("byte Array photo length : " + bytesArray)
      s = new String(bytesArray, StandardCharsets.UTF_8)
      logger.info("sPhoto length : " + s.length)
    }
    s
  }

  def convertPythonDateToMySqlDate(date : String) = {
    val dt = new Date();
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    sdf.format(dt);
  }
}
