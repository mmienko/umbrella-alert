package com.weather.alerts.service

import java.time.ZoneId

final case class Forecast(
  current: ForecastUnit,
  today: Option[ForecastUnit],
  hourly: Iterable[ForecastUnit],
  zonedId: ZoneId
) {

  def hasChanceOfPrecipitation(
    dailyPrecipitationProd: Double,
    hourlyPrecipitationProd: Double,
    nextHours: Int
  ): Boolean = {
    hasChanceOfRainOverTheDay(dailyPrecipitationProd) ||
    hasChanceOfRainInAnHour(hourlyPrecipitationProd, nextHours)
  }

  private def hasChanceOfRainOverTheDay(precipitationProd: Double): Boolean =
    today
      .filter(_.precipType.exists(Forecast.PrecipitationTypes.contains))
      .flatMap(_.precipProbability)
      .exists(_ >= precipitationProd)

  private def hasChanceOfRainInAnHour(
    precipitationProd: Double,
    nextHours: Int
  ) =
    hourly
      .take(nextHours)
      .filter(_.precipType.exists(Forecast.PrecipitationTypes.contains))
      .flatMap(_.precipProbability)
      .exists(_ >= precipitationProd)

}

object Forecast {
  private val PrecipitationTypes = List("rain", "sleet")
}

final case class ForecastUnit(
  time: Long,
  summary: Option[String],
  precipProbability: Option[Double],
  precipType: Option[String],
  temperature: Option[Double],
  apparentTemperature: Option[Double]
)
