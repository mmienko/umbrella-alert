package com.weather.alerts

import java.util.TimeZone

import scala.util.Try

trait ForecastServiceConfig {

  private val KEY_LAT_LONG = "LAT_LONG"
  private val KEY_DARK_SKY_API_KEY = "DARK_SKY_API_KEY"
  private val KEY_LOOK_AHEAD_HOURS = "LOOK_AHEAD_HOURS"
  private val KEY_HOURLY_PRECIPITATION_PROB = "HOURLY_PRECIPITATION_PROB"
  private val KEY_DAILY_PRECIPITATION_PROB = "DAILY_PRECIPITATION_PROB"
  private val KEY_TIME_ZONE = "TIME_ZONE"

  lazy val LAT_LONG: String = {
    val failMsg = s"$KEY_LAT_LONG should have a be a comma separated lat & long, i.e. 40.7410429,-74.0051566"
    val ll = getOrThrow(KEY_LAT_LONG)
    val parts = ll.split(",")
    if (parts.size != 2)
      throw new IllegalArgumentException(failMsg)
    if (Try(parts.head.toDouble).flatMap(_ => Try(parts.last.toDouble)).isFailure)
      throw new IllegalArgumentException(failMsg)
    ll
  }

  lazy val DARK_SKY_API_KEY: String = getOrThrow(KEY_DARK_SKY_API_KEY)

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

  lazy val TIME_ZONE: TimeZone = TimeZone.getTimeZone(getOrThrow(KEY_TIME_ZONE))

  protected def getOrThrow(key: String): String = {
    scala.util.Properties.envOrNone(key).getOrElse(throw new IllegalArgumentException(s"Missing $key env var"))
  }
}
