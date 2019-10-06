package com.weather.alerts.service.client

import java.time.{ZonedDateTime, Instant, ZoneId}

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
  apparentTemperature: Option[Double]
) {

  def dateTime(zoneId: ZoneId): ZonedDateTime =
    Instant.ofEpochSecond(time).atZone(zoneId)
}

case class DataBlock(summary: String, icon: String, data: List[DataPoint])

case class DarkSkyForecast(
  latitude: Double,
  longitude: Double,
  timezone: String,
  currently: DataPoint,
  hourly: DataBlock,
  daily: DataBlock
)
