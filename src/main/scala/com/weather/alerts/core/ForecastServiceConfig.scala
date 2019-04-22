package com.weather.alerts.core

import scala.util.Try

trait ForecastServiceConfig {

  private val KEY_LAT_LONG = "LAT_LONG"
  private val KEY_DARK_SKY_API_KEY = "DARK_SKY_API_KEY"

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

  protected def getOrThrow(key: String): String = {
    scala.util.Properties.envOrNone(key).getOrElse(throw new IllegalArgumentException(s"Missing $key env var"))
  }
}
