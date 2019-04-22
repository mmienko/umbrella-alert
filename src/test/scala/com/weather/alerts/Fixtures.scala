package com.weather.alerts

import java.time.{Instant, LocalDate}
import java.util.TimeZone

import com.weather.alerts.models.Forecast
import io.circe.generic.auto._

import scala.io.Source

object Fixtures {

  lazy val forecastResponse: String = Source.fromResource("sample_response.json")
    .getLines()
    .mkString("")

  lazy val forecast: Forecast = io.circe.parser.decode[Forecast](forecastResponse).right.get

  lazy val timeZone: TimeZone = TimeZone.getTimeZone(forecast.timezone)

  lazy val forecastDate: LocalDate = timeToLocalDate(forecast.currently.time)

  def timeToLocalDate(time: Long): LocalDate = LocalDate.from(
    Instant
      .ofEpochSecond(time)
      .atZone(timeZone.toZoneId)
  )
}
