package com.weather.alerts.service

import com.weather.alerts.service.client.ForecastClientConfig

object IntegrationConfigs {

  private lazy val RawDarkSkyApiKey: String = getOrThrow("DARK_SKY_API_KEY")
  private lazy val RawLatLong: String = getOrThrow("LAT_LONG")

  lazy val forecastClientConfig: ForecastClientConfig =
    client.ForecastClientConfig(
      DarkSkyApiKey(RawDarkSkyApiKey),
      LatLong.parse(RawLatLong) match {
        case Left(error) => throw new RuntimeException(error)
        case Right(value) => value
      }
    )

  protected def getOrThrow(key: String): String = {
    scala.util.Properties
      .envOrNone(key)
      .getOrElse(throw new IllegalArgumentException(s"Missing $key env var"))
  }
}
