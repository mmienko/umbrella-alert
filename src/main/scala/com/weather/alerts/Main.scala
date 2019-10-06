package com.weather.alerts

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.sns.{AmazonSNSClientBuilder, AmazonSNS}
import com.weather.alerts.service.client.ForecastClient
import com.weather.alerts.service._

class Main extends RequestHandler[ScheduledEvent, String] {

  import com.weather.alerts.Main._

  override def handleRequest(event: ScheduledEvent, ctx: Context): String = {
    umbrellaAlert.maybeSendAlert.fold(_.value, _.toString)
  }
}

object Main {

  private val umbrellaAlert: UmbrellaAlert =
    wireUpApp

  private[this] def wireUpApp: UmbrellaAlert = {
    Config
      .loadFromEnv() match {
      case Left(configReadFailures) =>
        throw new IllegalArgumentException(configReadFailures)
      case Right(config) =>
        val snsClient: AmazonSNS = AmazonSNSClientBuilder
          .standard()
          .withRegion(config.snsConfig.region)
          .build()
        new UmbrellaAlert(
          ForecastClient(config.forecastClientConfig),
          ForecastPublisher(snsClient, config.publisherConfig),
          config.umbrellaAlertConfig
        )
    }
  }
}
