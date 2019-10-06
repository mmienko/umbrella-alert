package com.weather.alerts.service

final case class UmbrellaAlertConfig(
  dailyPrecipitationProbability: Double,
  hourlyPrecipitationProbability: Double,
  lookAheadHours: Int
)
