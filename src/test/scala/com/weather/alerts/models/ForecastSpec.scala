package com.weather.alerts.models

import com.weather.alerts.{BaseSpec, Fixtures}
import io.circe.generic.auto._

import scala.io.Source

class ForecastSpec extends BaseSpec {

  describe("Forecast") {
    it("should get todays forecast") {
      val sampleResponse = Source.fromResource("sample_response.json").getLines().mkString("")
      val forecast = io.circe.parser.decode[Forecast](sampleResponse).right.get
      val today = forecast.getTodays(Fixtures.forecastDate)

      Fixtures.timeToLocalDate(today.current.time) shouldEqual Fixtures.forecastDate
      Fixtures.timeToLocalDate(today.today.time) shouldEqual Fixtures.forecastDate
      today.hourly.size should be > 0
      today.hourly.foreach(hour => {
        Fixtures.timeToLocalDate(hour.time) shouldEqual Fixtures.forecastDate
      })
    }
  }

}
