package com.weather.alerts.service.client

import com.weather.alerts.BaseSpec
import com.weather.alerts.service.IntegrationConfigs
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.EitherValues

class ForecastClientIntegrationSpec extends BaseSpec with EitherValues {

  describe("ForecastClient") {
    it("should get forecast") {
      val client = ForecastClient(IntegrationConfigs.forecastClientConfig)
      val forecast = client.forecastForToday().right.value
      println(forecast.asJson)
      println(forecast.hasChanceOfPrecipitation(0.6, 0.6, 12))
    }
  }
}
