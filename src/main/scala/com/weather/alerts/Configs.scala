package com.weather.alerts

import com.amazonaws.regions.Regions

import scala.util.Try

object Configs extends ForecastServiceConfig {

  private val KEY_SNS_ARN = "SNS_ARN"
  private val KEY_SNS_REGION = "SNS_REGION"

  lazy val SNS_ARN: String = getOrThrow(KEY_SNS_ARN)
  lazy val SNS_REGION: Regions = {
    val tried = Try(Regions.fromName(getOrThrow(KEY_SNS_REGION)))
    if (tried.isFailure)
      throw new IllegalArgumentException(s"$KEY_SNS_REGION must be a valid region, i.e. ${Regions.US_EAST_1.getName}")
    tried.get
  }
}
