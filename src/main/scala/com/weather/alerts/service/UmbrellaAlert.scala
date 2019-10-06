package com.weather.alerts.service

import com.weather.alerts.service.client.ForecastClient

class UmbrellaAlert(
  forecastClient: ForecastClient,
  publisher: ForecastPublisher,
  umberAlertConfig: UmbrellaAlertConfig
) {

  def maybeSendAlert: Either[UmbrellaAlertError, AlertResult] = {
    forecastClient
      .forecastForToday()
      .left
      .map(UmbrellaAlertError.fromForecastError)
      .flatMap { todaysForecast =>
        if (willLikelyRain(todaysForecast))
          publisher
            .publish(todaysForecast)
            .toEither
            .left
            .map(UmbrellaAlertError.fromThrowable)
            .map(_ => AlertSent)
        else
          Right(AlertNotSent)
      }
  }

  private def willLikelyRain(forecast: Forecast): Boolean =
    forecast
      .hasChanceOfPrecipitation(
        umberAlertConfig.dailyPrecipitationProbability,
        umberAlertConfig.hourlyPrecipitationProbability,
        umberAlertConfig.lookAheadHours
      )
}
