package com.weather.alerts.models

import java.time.ZoneId

import com.weather.alerts.BaseSpec

class TodaysForecastSpec extends BaseSpec {

  private val current = DataPoint(1555772047, Some("Overcast"), Some("cloudy"), Some(6.0), Some(57.0), Some(0.0), None, Some(0.0), None, None, Some(65.06), Some(65.58))

  private val day = DataPoint(1555732800, Some("Rain in the morning."), Some("rain"), None, None, Some(0.0407), None, Some(1.0), None, Some("rain"), None, None)

  private val hourly = List(
    DataPoint(1555783200, Some("Overcast"), Some("cloudy"), None, None, Some(0.0165), None, Some(0.32), None, Some("rain"), Some(63.59), Some(63.94)),
//    DataPoint(1555786800, Some("Overcast"), Some("cloudy"), None, None, Some(0.0068), None, Some(0.11), None, Some("rain"), Some(62.75), Some(62.99)),
//    DataPoint(1555790400, Some("Overcast"), Some("cloudy"), None, None, Some(0.0042), None, Some(0.07), None, Some("rain"), Some(62.28), Some(62.38)),
//    DataPoint(1555794000, Some("Overcast"), Some("cloudy"), None, None, Some(0.0026), None, Some(0.04), None, Some("rain"), Some(60.82), Some(60.82)),
//    DataPoint(1555797600, Some("Overcast"), Some("cloudy"), None, None, Some(0.0023), None, Some(0.04), None, Some("rain"), Some(59.65), Some(59.65)),
//    DataPoint(1555801200, Some("Overcast"), Some("cloudy"), None, None, Some(0.0025), None, Some(0.03), None, Some("rain"), Some(58.24), Some(58.24)),
    DataPoint(1555808400, Some("Mostly Cloudy"), Some("partly - cloudy - night"), None, None, Some(5.0E-4), None, Some(0.02), None, Some("rain"), Some(56.83), Some(56.83)),
//    DataPoint(1555815600, Some("Overcast"), Some("cloudy"), None, None, Some(0.0014), None, Some(0.02), None, Some("rain"), Some(55.78), Some(55.78)),
//    DataPoint(1555819200, Some("Mostly Cloudy"), Some("partly - cloudy - night"), None, None, Some(0.0015), None, Some(0.02), None, Some("rain"), Some(55.25), Some(55.25))
  )

  private val forecast = TodaysForecast(current, Some(day), hourly, ZoneId.of("America/New_York"))

  describe("Forecast") {
    it("should give current summary") {
      forecast.currentSummary shouldEqual
        """
          |Overcast
          |65.06˚F feels like 65.58˚F
          |No precipitation
        """.stripMargin.trim
    }

    it("should give daily summary") {
      forecast.todaysSummary shouldEqual
        """
          |Rain in the morning.
          |100% chance of rain
        """.stripMargin.trim
    }

    it("should give hourly summary") {
      forecast.hourlySummary shouldEqual List(
        " - 02:00 pm Overcast     , 63.59˚F feels like 63.94˚F, 32% chance of rain",
        " - 09:00 pm Mostly Cloudy, 56.83˚F feels like 56.83˚F, 2% chance of rain"
      ).mkString("\n")
    }
  }

}
