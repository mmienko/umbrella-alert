package com.weather.alerts.core

import java.lang

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.softwaremill.sttp.{sttp, HttpURLConnectionBackend, Id, SttpBackend, _}
import com.weather.alerts.models.Forecast
import io.circe.generic.auto._
import io.circe.parser.decode

trait ForecastService extends RequestHandler[ScheduledEvent, Void] {

  private val URI = uri"https://api.darksky.net/forecast/${configs.DARK_SKY_API_KEY}/${configs.LAT_LONG}?exclude=minutely,flags"

  implicit private val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  protected def configs: ForecastServiceConfig

  def handleForecast(result: Either[Exception, Forecast], isTest: Boolean, c: Context): Unit

  override def handleRequest(event: ScheduledEvent, c: Context): Void = {
    val res = fetchForecast()
      .flatMap(response => decode[Forecast](response) match {
        case Left(error) => Left(new Exception(error))
        case r => r
      })

    handleForecast(res, isTestEvent(event), c)
    null
  }

  protected def fetchForecast(): Either[Exception, String] = {
    val request = sttp.get(URI)
    val response = backend.send(request)
    response.body
      .left
      .map(errMsg => new Exception(s"Code: ${response.code}, Body: $errMsg"))
  }

  private def isTestEvent(event: ScheduledEvent) = {
    event.getDetail.getOrDefault("test", java.lang.Boolean.FALSE).asInstanceOf[lang.Boolean]
  }

}
