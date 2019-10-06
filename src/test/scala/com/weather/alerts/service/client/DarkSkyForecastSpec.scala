package com.weather.alerts.service.client

import scala.io.Source

import com.weather.alerts.BaseSpec
import io.circe.generic.auto._

class DarkSkyForecastSpec extends BaseSpec {

  describe("DarkSkyForecast") {
    it("sample dark sky can be decoded") {
      val sampleResponse =
        Source.fromResource("dark_sky_response.json").getLines().mkString
      io.circe.parser
        .decode[DarkSkyForecast](sampleResponse)
        .isRight shouldBe true
    }
  }

}
