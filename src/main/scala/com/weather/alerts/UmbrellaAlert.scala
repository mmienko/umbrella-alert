package com.weather.alerts

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishRequest

class UmbrellaAlert extends ForecastService {
  private val ARN = Configs.SNS_ARN
  private val SUBJECT = "Rain Alert"
  private val client = AmazonSNSClientBuilder.standard()
    .withRegion(Configs.SNS_REGION.getName)
    .build()

  override protected def configs: ForecastServiceConfig = Configs

  override def publishMessage(msg: String, isTest: Boolean, c: Context): Unit = {
    client.publish(new PublishRequest(ARN, msg, if (isTest) "Weather Alert Test" else SUBJECT))
  }

}