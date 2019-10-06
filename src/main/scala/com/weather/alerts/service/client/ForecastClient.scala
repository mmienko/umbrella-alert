package com.weather.alerts.service.client

import java.time.LocalDate
import java.util.TimeZone

import com.softwaremill.sttp.{sttp, _}
import com.weather.alerts.service.{ForecastError, ForecastUnit, Forecast}
import io.circe.generic.auto._
import io.circe.parser.decode

class ForecastClient(
  todaysDate: LocalDate,
  forecastClientConfig: ForecastClientConfig,
  httpClient: EitherBackend[Nothing]
) {

  private lazy val URI =
    uri"https://api.darksky.net/forecast/${forecastClientConfig.darkSkyApiKey.value}/${forecastClientConfig.latLong.show}?exclude=minutely,flags"

  def forecastForToday(): Either[ForecastError, Forecast] = {
    getDarkSkyForecast
      .flatMap { darkSkyForecast =>
        ForecastClient
          .getTodaysForecast(todaysDate, darkSkyForecast)
          .toRight(
            ForecastError(
              "DarkSky did not return the hourly forecast for today"
            )
          )
      }
  }

  private def getDarkSkyForecast: Either[ForecastError, DarkSkyForecast] = {
    val request = sttp.get(URI)
    httpClient
      .send(request)
      .left
      .map(ForecastError.apply)
      .flatMap { resp =>
        //in this HTTP client, errors are encoded in the body.
        resp.body.left
          .map(body => handleResponseError(resp, body))
          .flatMap(decode[DarkSkyForecast](_).left.map(ForecastError.apply))
      }
  }

  private def handleResponseError(resp: Response[String], body: String) = {
    ForecastError(s"DarkSky returned ${resp.code.toString}: $body")
  }
}

object ForecastClient {

  def apply(forecastClientConfig: ForecastClientConfig): ForecastClient =
    new ForecastClient(
      LocalDate.now,
      forecastClientConfig,
      new EitherBackend[Nothing](HttpURLConnectionBackend())
    )

  def getTodaysForecast(
    todaysDate: LocalDate,
    darkSkyForecast: DarkSkyForecast
  ): Option[Forecast] = {
    val tz = TimeZone.getTimeZone(darkSkyForecast.timezone)
    val midnight = todaysDate
      .plusDays(1)
      .atStartOfDay(tz.toZoneId)

    darkSkyForecast.hourly.data
      .filter(_.dateTime(tz.toZoneId).isBefore(midnight))
      .sortBy(_.time) match {
      case Nil =>
        None
      case hours =>
        val day = darkSkyForecast.daily.data
          .find(_.dateTime(tz.toZoneId).isBefore(midnight))
        Some(
          Forecast(
            current = forecastUnit(darkSkyForecast.currently),
            today = day.map(forecastUnit),
            hourly = hours.map(forecastUnit),
            zonedId = tz.toZoneId
          )
        )
    }
  }

  private def forecastUnit(dataPoint: DataPoint): ForecastUnit = {
    ForecastUnit(
      time = dataPoint.time,
      summary = dataPoint.summary,
      precipProbability = dataPoint.precipProbability,
      precipType = dataPoint.precipType,
      temperature = dataPoint.temperature,
      apparentTemperature = dataPoint.apparentTemperature
    )
  }

}
