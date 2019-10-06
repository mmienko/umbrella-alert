package com.weather.alerts.service

import scala.util.Try

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest

class ForecastPublisher(
  snsClient: AmazonSNS,
  publisherConfig: PublisherConfig
) {
  private val Subject = "Rain Alert"

  def publish(today: Forecast): Try[Unit] = {
    Try {
      snsClient.publish(
        new PublishRequest()
          .withTopicArn(publisherConfig.topicArn.value)
          .withSubject(Subject)
          .withMessage(ForecastPresenter.present(today))
      )
    }
  }
}

object ForecastPublisher {

  def apply(
    snsClient: AmazonSNS,
    publisherConfig: PublisherConfig
  ): ForecastPublisher =
    new ForecastPublisher(snsClient, publisherConfig)
}
