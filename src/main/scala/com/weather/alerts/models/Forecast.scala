package com.weather.alerts.models

import java.time.{Instant, LocalDate, ZonedDateTime, ZoneId}
import java.util.TimeZone

case class DataPoint(
  time: Long,
  summary: Option[String],
  icon: Option[String],
  nearestStormDistance: Option[Double],
  nearestStormBearing: Option[Double],
  precipIntensity: Option[Double],
  precipIntensityError: Option[Double],
  precipProbability: Option[Double],
  precipAccumulation: Option[Double], //amount of snowfall accumulation expected to occur, in inches.
  precipType: Option[String],
  temperature: Option[Double],
  apparentTemperature: Option[Double],
) {
  def dateTime(zoneId: ZoneId): ZonedDateTime = Instant.ofEpochSecond(time).atZone(zoneId)
}

case class DataBlock(
  summary: String,
  icon: String,
  data: List[DataPoint]
)

case class Forecast(
  latitude: Double,
  longitude: Double,
  timezone: String,
  currently: DataPoint,
  hourly: DataBlock,
  daily: DataBlock
) {

  def getTodays(todaysDate: LocalDate = LocalDate.now): TodaysForecast = {
    val tz = TimeZone.getTimeZone(timezone)
    val midnight = todaysDate
      .plusDays(1)
      .atStartOfDay(tz.toZoneId)

    val day = daily.data.filter(_.dateTime(tz.toZoneId).isBefore(midnight)).head //todo: safe to assume?
    val hours = hourly.data.filter(_.dateTime(tz.toZoneId).isBefore(midnight)).sortBy(_.time)
    TodaysForecast(currently, day, hours, tz.toZoneId)
  }

}