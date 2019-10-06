package com.weather.alerts.service.client

import com.weather.alerts.service.{DarkSkyApiKey, LatLong}

final case class ForecastClientConfig(
  darkSkyApiKey: DarkSkyApiKey,
  latLong: LatLong
)
