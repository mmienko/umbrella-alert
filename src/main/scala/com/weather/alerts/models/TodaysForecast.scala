package com.weather.alerts.models

import java.time.{ZonedDateTime, ZoneId}

final case class TodaysForecast(
  current: DataPoint,
  today: Option[DataPoint],
  hourly: Iterable[DataPoint],
  zonedId: ZoneId
) {

  private val PRECIPITATION_TYPES = List("rain", "sleet")

  lazy val currentSummary: String =
    s"""
       |${sum(current)}
       |${temp(current)}
       |${precipitationProb(current)}
    """.stripMargin.trim

  lazy val todaysSummary: String =
    s"""
       |${today.map(sum(_)).show}
       |${today.map(precipitationProb).show}
    """.stripMargin.trim

  lazy val hourlySummary: String = {
    val estimatedSumWidth = hourly.map(sum(_)).map(_.length).max
    hourly
      .map(h => s" - ${hourOf(h.dateTime(zonedId))} ${sum(h, Some(estimatedSumWidth))}, ${temp(h)}, ${precipitationProb(h)}")
      .mkString("\n")
  }

  private def sum(dp: DataPoint, widthOpt: Option[Int] = None): String = {
    val res = dp.summary.show
    widthOpt match {
      case Some(width) =>
        res.padTo(width, " ").mkString("")
      case None =>
        res
    }
  }

  private def temp(dp: DataPoint) = s"${dp.temperature.show}˚F feels like ${dp.apparentTemperature.show}˚F"

  private def precipitationProb(dp: DataPoint) = dp.precipType match {
    case None => "No precipitation"
    case Some(pType) => dp
      .precipProbability
      .map(_ * 100)
      .map(math.round)
      .show + "% chance of " + pType
  }

  private def hourOf(zonedDateTime: ZonedDateTime) = {
    val hour = (zonedDateTime.getHour % 12).toString
    val amOrPm = if (zonedDateTime.getHour / 12 == 0) "am" else "pm"
    val padded = if (hour.length == 2) hour else s"0$hour"
    s"$padded:00 $amOrPm"
  }

  private implicit def optToOpt[T](option: Option[T]): CleanOption[T] = new CleanOption[T](option)

  private final class CleanOption[T](opt: Option[T]) {
    def show: String = {
      opt.map(_.toString).getOrElse("N/A")
    }
  }

  /**
    * Checks if chance of precipitation for the day or a given hour is above the given probability.
    *
    * @param dailyPrecipitationProd  - probability during whole day
    * @param hourlyPrecipitationProd - probability during any hour
    * @param nextHours               - number of hours to look ahead for the day
    * @return
    */
  def hasChanceOfPrecipitation(dailyPrecipitationProd: Double, hourlyPrecipitationProd: Double, nextHours: Int): Boolean = {
    hasChanceOfRainOverTheDay(dailyPrecipitationProd) ||
      hasChanceOfRainInAnHour(hourlyPrecipitationProd, nextHours)
  }

  private def hasChanceOfRainOverTheDay(precipitationProd: Double): Boolean = {
    today.isDefined &&
      (today.flatMap(_.precipType).exists(PRECIPITATION_TYPES.contains) ||
        today.flatMap(_.precipProbability).exists(_ >= precipitationProd))
  }

  private def hasChanceOfRainInAnHour(precipitationProd: Double, nextHours: Int) = {
    val hours = hourly.take(nextHours)
      .filter(_.precipType.exists(PRECIPITATION_TYPES.contains))
    hours.nonEmpty &&
      hours.flatMap(_.precipProbability).exists(_ >= precipitationProd)
  }

}
