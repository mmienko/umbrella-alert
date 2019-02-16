package com.weather.alerts

import java.time.{Instant, ZonedDateTime}
import java.util.Calendar

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.softwaremill.sttp._
import io.circe.generic.auto._
import io.circe.parser.decode
import org.joda.time.DateTime

trait ForecastService extends RequestHandler[ScheduledEvent, DateTime] {

  private val URI = uri"https://api.darksky.net/forecast/${configs.DARK_SKY_API_KEY}/${configs.LAT_LONG}?exclude=minutely,flags"
  private val PRECIPITATION_TYPES = List("rain", "sleet")

  implicit private val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  override def handleRequest(event: ScheduledEvent, c: Context): DateTime = {
    val request = sttp.get(URI)
    val response = backend.send(request)
    response.body match {
      case Left(errMsg) => throw new Exception(s"Code: ${response.code}, Body: $errMsg")
      case Right(jsonStr) =>
        decode[ForecastResponse](jsonStr) match {
          case Left(error) => throw new Exception(error)
          case Right(forecastResponse) =>
            checkForecast(forecastResponse, event, c)
            event.getTime
        }
    }
  }

  protected def configs: ForecastServiceConfig

  private def checkForecast(forecastResponse: ForecastResponse, event: ScheduledEvent, c: Context) = {
    if (!forecastResponse.timezone.equals(configs.TIME_ZONE.getID))
      throw new Exception(s"forecastResponse.timezone, ${forecastResponse.timezone}, should ${configs.TIME_ZONE.getID}")

    val timeLimit = getTimeLimit
    val hourlyRain = getHourlyRainForecast(forecastResponse, timeLimit)
    val `today's Rain` = getTodaysRainForecast(forecastResponse, timeLimit)
    val currentForecast = forecastResponse.currently

    val chanceOfRainInDay = `today's Rain`.flatMap(_.precipProbability).exists(_ >= configs.DAILY_PRECIPITATION_PROB)
    val chanceOfRainInAnHour = hourlyRain.flatMap(_.precipProbability).exists(_ >= configs.HOURLY_PRECIPITATION_PROB)

    val isTest = event.getDetail.getOrDefault("test", java.lang.Boolean.FALSE).asInstanceOf[java.lang.Boolean]

    if (chanceOfRainInDay || chanceOfRainInAnHour) {
      publishMessage(
        createMessage(hourlyRain, `today's Rain`, currentForecast),
        isTest,
        c
      )
    } else if (isTest) {
      publishMessage("No weather alert", isTest, c)
    }
  }

  private def createMessage(hourlyRain: List[DataPoint], `today's Rain`: Option[DataPoint], currentForecast: DataPoint): String = {
    s"""
       |Currently:
       |${currentForecast.summary.getOrElse("")}
       |
       |Day:
       |${`today's Rain`.map(_.summary).getOrElse("")}
       |
       |Next ${configs.LOOK_AHEAD_HOURS} Hours:
       |${hourlyRain.map(_.summary).mkString("\n")}
        """.stripMargin
  }

  private def getTodaysRainForecast(forecastResponse: ForecastResponse, timeLimit: ZonedDateTime) = {
    forecastResponse.daily
      .data
      .find(dp => Instant.ofEpochSecond(dp.time).atZone(configs.TIME_ZONE.toZoneId).isBefore(timeLimit))
      .filter(_.precipType.exists(PRECIPITATION_TYPES.contains))
  }

  private def getHourlyRainForecast(forecastResponse: ForecastResponse, timeLimit: ZonedDateTime) = {
    forecastResponse.hourly
      .data
      .filter(dp => Instant.ofEpochSecond(dp.time).atZone(configs.TIME_ZONE.toZoneId).isBefore(timeLimit))
      .filter(_.precipType.exists(PRECIPITATION_TYPES.contains))
  }

  private def getTimeLimit: ZonedDateTime = {
    val today = Calendar.getInstance(configs.TIME_ZONE)
    today.add(Calendar.HOUR_OF_DAY, configs.LOOK_AHEAD_HOURS)
    Instant.ofEpochMilli(today.getTime.getTime).atZone(configs.TIME_ZONE.toZoneId)
  }


  def publishMessage(msg: String, isTest: Boolean, c: Context): Unit
}
