package com.weather.alerts.service

import com.weather.alerts.BaseSpec

class ForecastPresenterSpec extends BaseSpec {

  describe("ForecastPresenter") {
    it(
      "should present forecast with no current precipitation, but chance of precipitation during the day"
    ) {
      ForecastPresenter.present(Fixtures.currentlyClearButRainInTheDay) shouldEqual
      """
          |Currently:
          |Clear
          |65.06˚F feels like 65.58˚F
          |No precipitation
          |
          |Day:
          |Clear throughout the day.
          |4% chance of rain
          |""".stripMargin.trim
    }

    it(
      "should present forecast with current precipitation, but no chance of precipitation during the day"
    ) {
      ForecastPresenter.present(Fixtures.currentlyRainButClearInTheDay) shouldEqual
      """
          |Currently:
          |Rainy
          |65.06˚F feels like 65.58˚F
          |100% chance of rain
          |
          |Day:
          |Clear
          |No precipitation
          |""".stripMargin.trim
    }

    it("should present forecast rain all day") {
      ForecastPresenter.present(Fixtures.rainyForecast) shouldEqual
      """
          |Currently:
          |Rainy
          |65.06˚F feels like 65.58˚F
          |100% chance of rain
          |
          |Day:
          |Rainy
          |100% chance of rain
          |""".stripMargin.trim
    }

    it("should present current, with no daily") {
      ForecastPresenter.present(Fixtures.rainyWithoutDaysForecast) shouldEqual
      """
          |Currently:
          |Rainy
          |65.06˚F feels like 65.58˚F
          |100% chance of rain
          |
          |Day:
          |N/A
          |N/A
          |""".stripMargin.trim
    }
  }
}
