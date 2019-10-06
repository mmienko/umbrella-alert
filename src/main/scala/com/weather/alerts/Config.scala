package com.weather.alerts

import scala.util.{Properties, Try}

import com.amazonaws.regions.Regions
import com.weather.alerts.service._
import com.weather.alerts.service.client.ForecastClientConfig

final case class Config(
  snsConfig: SnsConfig,
  forecastClientConfig: ForecastClientConfig,
  publisherConfig: PublisherConfig,
  umbrellaAlertConfig: UmbrellaAlertConfig
)

object Config {

  def loadFromEnv(): Either[String, Config] = {
    for {
      snsRegion <- readEnv("SNS_REGION").flatMap { region =>
        tryAsEither(Regions.fromName(region))
      }

      darkSkyApi <- readEnv("DARK_SKY_API_KEY").map(DarkSkyApiKey)
      latLong <- readEnv("LAT_LONG").flatMap(LatLong.parse)

      snsArn <- readEnv("SNS_ARN").map(AwsTopicArn)

      dpp <- readEnv("DAILY_PRECIPITATION_PROBABILITY").flatMap { str =>
        tryAsEither(str.toDouble)
      }
      hpp <- readEnv("HOURLY_PRECIPITATION_PROBABILITY").flatMap { str =>
        tryAsEither(str.toDouble)
      }
      lah <- readEnv("LOOK_AHEAD_HOURS").flatMap { str =>
        tryAsEither(str.toInt)
      }

    } yield {
      Config(
        snsConfig = SnsConfig(snsRegion),
        forecastClientConfig = ForecastClientConfig(
          darkSkyApi,
          latLong
        ),
        publisherConfig = PublisherConfig(snsArn),
        umbrellaAlertConfig = UmbrellaAlertConfig(
          dailyPrecipitationProbability = dpp,
          hourlyPrecipitationProbability = hpp,
          lookAheadHours = lah
        )
      )
    }
  }

  private[this] def readEnv(name: String): Either[String, String] = {
    Properties.envOrNone(name).toRight(s"Env variable $name not found")
  }

  private[this] def tryAsEither[A](r: => A): Either[String, A] = {
    Try(r).toEither.left
      .map(error => s"${error.getClass.getName} ${error.getMessage}")
  }

}

final case class SnsConfig(region: Regions)
