package com.weather.alerts

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
)

case class DataBlock(
  summary: String,
  icon: String,
  data: List[DataPoint]
)

case class ForecastResponse(
  latitude: Double,
  longitude: Double,
  timezone: String,
  currently: DataPoint,
  hourly: DataBlock,
  daily: DataBlock
)