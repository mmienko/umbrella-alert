package com.weather.alerts.service

import scala.util.Try

final case class DarkSkyApiKey(value: String) extends AnyVal

final case class LatLong(latitude: String, longitude: String) {
  def show: String = s"$latitude,$longitude"
}

final case class AwsTopicArn(value: String) extends AnyVal

object LatLong {

  def parse(string: String): Either[String, LatLong] = {
    val parts = string.split(",")
    if (parts.length != 2) {
      Left(errorMsg(string))
    } else {
      if (Try(parts.head.toDouble)
            .flatMap(_ => Try(parts.last.toDouble))
            .isFailure)
        Left(errorMsg(string))
      else
        Right(LatLong(parts.head.trim, parts(1).trim))
    }
  }

  private[this] def errorMsg(string: String) =
    "Latitude & Longitude, \"" + string + "\", must be comma separated doubles, i.e. 40.741,-74.005"
}

final case class ForecastError(value: String) extends AnyVal

object ForecastError {

  def apply(throwable: Throwable): ForecastError =
    new ForecastError(
      s"${throwable.getClass.getSimpleName}: ${throwable.getMessage}"
    )
}

final case class UmbrellaAlertError(value: String) extends AnyVal

object UmbrellaAlertError {

  def fromThrowable(throwable: Throwable): UmbrellaAlertError =
    new UmbrellaAlertError(
      s"${throwable.getClass.getSimpleName}: ${throwable.getMessage}"
    )

  def fromForecastError(forecastError: ForecastError): UmbrellaAlertError =
    new UmbrellaAlertError(
      s"${ForecastError.getClass.getSimpleName}: ${forecastError.value}"
    )
}

sealed trait AlertResult

case object AlertSent extends AlertResult
case object AlertNotSent extends AlertResult
