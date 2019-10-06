package com.weather.alerts.service

import com.weather.alerts.BaseSpec

class ForecastSpec extends BaseSpec {

  describe("Forecast") {
    describe("no hourly chance of rain, just daily") {
      it(
        "has chance of precipitation if probability of rain for today is greater than threshold"
      ) {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today =
            Some(Fixtures.rainForecastUnit.copy(precipProbability = Some(0.7))),
          hourly = List.empty,
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(threshold, 0, 0) shouldEqual true
      }

      it(
        "has NO chance of precipitation if probability of rain for today is less than threshold"
      ) {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today =
            Some(Fixtures.rainForecastUnit.copy(precipProbability = Some(0.5))),
          hourly = List.empty,
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(threshold, 0, 0) shouldEqual false
      }

      it(
        "has NO chance of precipitation if probability of rain for today doesn't exist"
      ) {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today = None,
          hourly = List.empty,
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(threshold, 0, 0) shouldEqual false
      }
    }

    describe("hourly chance of rain, but no daily") {
      it(
        "has chance of precipitation if probability of rain for at least 1 hour is greater than threshold"
      ) {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today = None,
          hourly = List(
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.3)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.7))
          ),
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(0, threshold, 2) shouldEqual true
      }

      it(
        "has chance of precipitation if probability of rain for at least 1 hour is greater than threshold and range is large"
      ) {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today = None,
          hourly = List(
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.3)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.7))
          ),
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(0, threshold, 4) shouldEqual true
      }

      it(
        "has NO chance of precipitation if probability of rain for all hours is greater than threshold"
      ) {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today = None,
          hourly = List(
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.3)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.4))
          ),
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(0, threshold, 2) shouldEqual false
      }

      it(
        "has NO chance of precipitation if range is smaller than the hourly rain"
      ) {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today = None,
          hourly = List(
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.3)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.3)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.7))
          ),
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(0, threshold, 2) shouldEqual false
      }
    }

    describe("hourly and daily chance of rain") {
      it("has chance of precipitation if above threshold") {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today =
            Some(Fixtures.rainForecastUnit.copy(precipProbability = Some(0.7))),
          hourly = List(
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.8)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.7))
          ),
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(threshold, threshold, 2) shouldEqual true

      }

      it("has NO chance of precipitation if above threshold") {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today =
            Some(Fixtures.rainForecastUnit.copy(precipProbability = Some(0.2))),
          hourly = List(
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.0)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.5))
          ),
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(threshold, threshold, 2) shouldEqual false

      }
    }

    describe("no hourly or daily rain") {
      it("has NO chance of precipitation") {
        val threshold = 0.6
        val forecast = Forecast(
          current = Fixtures.clearForecastUnit,
          today =
            Some(Fixtures.rainForecastUnit.copy(precipProbability = Some(0.0))),
          hourly = List(
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.0)),
            Fixtures.rainForecastUnit.copy(precipProbability = Some(0.0))
          ),
          zonedId = Fixtures.zoneId
        )
        forecast.hasChanceOfPrecipitation(threshold, threshold, 2) shouldEqual false
      }
    }
  }
}
