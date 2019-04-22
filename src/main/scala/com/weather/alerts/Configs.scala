package com.weather.alerts

import com.amazonaws.regions.Regions
import com.weather.alerts.core.ForecastServiceConfig

import scala.util.Try

object Configs extends ForecastServiceConfig {

  private val KEY_SNS_ARN = "SNS_ARN"
  private val KEY_SNS_REGION = "SNS_REGION"
  private val KEY_LOOK_AHEAD_HOURS = "LOOK_AHEAD_HOURS"
  private val KEY_HOURLY_PRECIPITATION_PROB = "HOURLY_PRECIPITATION_PROB"
  private val KEY_DAILY_PRECIPITATION_PROB = "DAILY_PRECIPITATION_PROB"

  lazy val LOOK_AHEAD_HOURS: Int = {
    val x = getOrThrow(KEY_LOOK_AHEAD_HOURS)
    val isInvalid = Try(x.toInt)
      .map(i => i < 1 && i > 24)
      .recover {
        case _ => true
      }
      .get
    if (isInvalid)
      throw new IllegalArgumentException(s"$KEY_LOOK_AHEAD_HOURS should be an int between 1 and 24")
    x.toInt
  }

  lazy val HOURLY_PRECIPITATION_PROB: Double = {
    val x = getOrThrow(KEY_HOURLY_PRECIPITATION_PROB)
    val isInvalid = Try(x.toDouble)
      .map(d => d < 0.0 && d > 1.0)
      .recover {
        case _ => true
      }
      .get
    if (isInvalid)
      throw new IllegalArgumentException(s"$KEY_HOURLY_PRECIPITATION_PROB should be an double between 0 and 1")
    x.toDouble
  }

  lazy val DAILY_PRECIPITATION_PROB: Double = {
    val x = getOrThrow(KEY_DAILY_PRECIPITATION_PROB)
    val isInvalid = Try(x.toDouble)
      .map(d => d < 0.0 && d > 1.0)
      .recover {
        case _ => true
      }
      .get
    if (isInvalid)
      throw new IllegalArgumentException(s"$KEY_DAILY_PRECIPITATION_PROB should be an double between 0 and 1")
    x.toDouble
  }

  lazy val SNS_ARN: String = getOrThrow(KEY_SNS_ARN)

  lazy val SNS_REGION: Regions = {
    val tried = Try(Regions.fromName(getOrThrow(KEY_SNS_REGION)))
    if (tried.isFailure)
      throw new IllegalArgumentException(s"$KEY_SNS_REGION must be a valid region, i.e. ${Regions.US_EAST_1.getName}")
    tried.get
  }
}
