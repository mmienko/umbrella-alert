package com.weather.alerts

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import com.weather.alerts.core.{ForecastService, ForecastServiceConfig}
import com.weather.alerts.models.{Forecast, TodaysForecast}

class UmbrellaAlert extends ForecastService {

  private val ARN = Configs.SNS_ARN

  private val client = AmazonSNSClientBuilder.standard()
    .withRegion(Configs.SNS_REGION.getName)
    .build()

  override protected def configs: ForecastServiceConfig = Configs

  override def handleForecast(result: Either[Exception, Forecast], isTest: Boolean, c: Context): Unit = {
    result match {
      case Left(e) =>
        c.getLogger.log(s"ERROR: ${e.getMessage}")
      case Right(forecast) =>
        val today = forecast.getTodays()
        if (willLikelyRain(today)) {
          val subject = if (isTest) "Weather Alert Test" else "Rain Alert"
          client.publish(new PublishRequest(ARN, createMessage(today), subject))
        }
    }
  }

  private def willLikelyRain(forecast: TodaysForecast): Boolean = forecast
    .hasChanceOfPrecipitation(Configs.DAILY_PRECIPITATION_PROB, Configs.HOURLY_PRECIPITATION_PROB, Configs.LOOK_AHEAD_HOURS)

  private def createMessage(forecast: TodaysForecast): String = {
    s"""
       |Currently:
       |${forecast.currentSummary}
       |
       |Day:
       |${forecast.todaysSummary}
       |
       |Next ${forecast.hourly.size} Hours:
       |${forecast.hourlySummary}
        """.stripMargin
  }


}
