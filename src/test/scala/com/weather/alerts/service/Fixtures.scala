package com.weather.alerts.service

import java.time.{Instant, ZoneId, LocalDate}

import io.circe.generic.auto._

object Fixtures {

  val umbrellaAlertConfig: UmbrellaAlertConfig = UmbrellaAlertConfig(
    dailyPrecipitationProbability = 0.1,
    hourlyPrecipitationProbability = 0.2,
    lookAheadHours = 10
  )

  val zoneId: ZoneId = ZoneId.of("America/New_York")

  val todaysDate: LocalDate = LocalDate.of(2019, 2, 16)

  val forecastDate: LocalDate = timeToLocalDate(1550329968) // TODO: reconcile with 1555772047 which is used everywhere

  val rainForecastUnit = ForecastUnit(
    time = 1555772047,
    summary = Some("Rainy"),
    precipProbability = Some(1.0), // 100%
    precipType = Some("rain"),
    temperature = Some(65.06),
    apparentTemperature = Some(65.58)
  )

  val clearWithSmallChanceOfRainForecastUnit = ForecastUnit(
    time = 1555772047,
    summary = Some("Clear throughout the day."),
    precipProbability = Some(0.04), // 0.04%
    precipType = Some("rain"),
    temperature = Some(65.06),
    apparentTemperature = Some(65.58)
  )

  val clearForecastUnit = ForecastUnit(
    time = 1555772047,
    summary = Some("Clear"),
    precipProbability = Some(0.0), // 0%
    precipType = None,
    temperature = Some(65.06),
    apparentTemperature = Some(65.58)
  )

  val rainyForecast: Forecast = Forecast(
    current = rainForecastUnit,
    hourly = List(rainForecastUnit),
    today = Some(rainForecastUnit),
    zonedId = zoneId
  )

  val clearForecast: Forecast = Forecast(
    current = clearForecastUnit,
    hourly = List(clearForecastUnit),
    today = Some(clearForecastUnit),
    zonedId = zoneId
  )

  val rainyWithoutDaysForecast = Forecast(
    current = rainForecastUnit,
    hourly = List(clearForecastUnit),
    today = None,
    zonedId = zoneId
  )

  val currentlyClearButSmallChanceOfPrecipitationInTheDay: Forecast = Forecast(
    current = clearForecastUnit,
    today = Some(rainForecastUnit),
    hourly = List(rainForecastUnit),
    zonedId = zoneId
  )

  val currentlyClearButRainInTheDay: Forecast = Forecast(
    current = clearForecastUnit,
    today = Some(clearWithSmallChanceOfRainForecastUnit),
    hourly = List.empty,
    zonedId = zoneId
  )

  val currentlyRainButClearInTheDay: Forecast = Forecast(
    current = rainForecastUnit,
    today = Some(clearForecastUnit),
    hourly = List.empty,
    zonedId = zoneId
  )

  def timeToLocalDate(time: Long): LocalDate = LocalDate.from(
    Instant
      .ofEpochSecond(time)
      .atZone(zoneId)
  )
}
