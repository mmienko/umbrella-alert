package com.weather.alerts.service.client

import scala.io.Source

import com.weather.alerts.service.{DarkSkyApiKey, LatLong, Fixtures}
import io.circe.generic.auto._

object ForecastClientFixtures {

  val forecastResponse: String = Source
    .fromResource("dark_sky_response.json")
    .getLines()
    .mkString("")

  val forecast: DarkSkyForecast =
    io.circe.parser.decode[DarkSkyForecast](forecastResponse).right.get

  val noHourlyForecastResponse: String = Source
    .fromResource("dark_sky_no_hourly_response.json")
    .getLines()
    .mkString("")

  val forecastClientConfig =
    ForecastClientConfig(DarkSkyApiKey("dark-sky"), LatLong("21.001", "22.001"))

  private val clearDataPoint = DataPoint(
    time = 1555772047,
    summary = Some("Clear"),
    None,
    None,
    None,
    None,
    None,
    precipProbability = Some(0.0), // 0%
    None,
    precipType = None,
    temperature = Some(65.06),
    apparentTemperature = Some(65.58)
  )

  private val clearDataBlock = DataBlock("clear", "clear", List(clearDataPoint))

  val clearForecast: DarkSkyForecast = DarkSkyForecast(
    latitude = 21.0,
    longitude = 22.0,
    timezone = "America/New_York",
    currently = clearDataPoint,
    hourly = clearDataBlock,
    daily = clearDataBlock
  )

  private val dataPoint = DataPoint(
    time = 1550329968,
    summary = Some("Clear"),
    None,
    None,
    None,
    None,
    None,
    precipProbability = Some(0.0), // 0%
    None,
    precipType = None,
    temperature = Some(65.06),
    apparentTemperature = Some(65.58)
  )

  private val hourlyDataBlock = DataBlock("clear", "clear", (0 to 24).map {
    hours =>
      dataPoint.copy(
        time = Fixtures.todaysDate
          .atStartOfDay(Fixtures.zoneId)
          .plusHours(hours)
          .toEpochSecond
      )
  }.toList)

  private val dailyDataBlock = DataBlock("clear", "clear", (0 to 4).map {
    days =>
      dataPoint.copy(
        time = Fixtures.todaysDate
          .plusDays(days)
          .atStartOfDay(Fixtures.zoneId)
          .toEpochSecond
      )
  }.toList)

  val darkSkyForecast: DarkSkyForecast = DarkSkyForecast(
    latitude = 21.0,
    longitude = 22.0,
    timezone = "America/New_York",
    currently = dataPoint,
    hourly = hourlyDataBlock,
    daily = dailyDataBlock
  )

  private val rainDataPoint = DataPoint(
    time = 1555772047,
    summary = Some("Rainy"),
    None,
    None,
    None,
    None,
    None,
    precipProbability = Some(1.0), // 100%
    None,
    precipType = Some("rain"),
    temperature = Some(65.06),
    apparentTemperature = Some(65.58)
  )

  private val rainDataBlock = DataBlock("rain", "rain", List(rainDataPoint))

  val rainyForecast: DarkSkyForecast = DarkSkyForecast(
    latitude = 21.0,
    longitude = 22.0,
    timezone = "America/New_York",
    currently = rainDataPoint,
    hourly = rainDataBlock,
    daily = rainDataBlock
  )

}
