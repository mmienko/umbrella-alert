package com.weather.alerts.service

import scala.util.Try

import com.weather.alerts.BaseSpec
import com.weather.alerts.service.client.ForecastClient

class UmbrellaAlertSpec extends BaseSpec {

  describe("UmbrellaAlert") {
    describe("error paths") {
      it("returns error if forecast client returns error") {
        Given("a forecast client that throws an error")
        val forecastClient = mock[ForecastClient]
        val error = ForecastError("err")
        (forecastClient.forecastForToday _).expects().returns(Left(error))

        And("a publisher")
        val publisher = mock[ForecastPublisher]

        And("an umbrella alert service")
        val umbrellaAlert = new UmbrellaAlert(
          forecastClient,
          publisher,
          Fixtures.umbrellaAlertConfig
        )

        When("alert might be sent")
        val res = umbrellaAlert.maybeSendAlert

        Then("result is an error")
        res.left.value shouldEqual UmbrellaAlertError.fromForecastError(error)
      }

      it("does nothing if no chance of rain") {
        Given("a forecast client that returns no chance of rain")
        val forecastClient = mock[ForecastClient]
        (forecastClient.forecastForToday _)
          .expects()
          .returns(Right(Fixtures.clearForecast))

        And("a publisher")
        val publisher = mock[ForecastPublisher]

        And("an umbrella alert service")
        val umbrellaAlert = new UmbrellaAlert(
          forecastClient,
          publisher,
          Fixtures.umbrellaAlertConfig
        )

        When("alert might be sent")
        val res = umbrellaAlert.maybeSendAlert

        Then("the result is no alert sent")
        res.right.value shouldBe AlertNotSent
      }

      it("returns error if publisher returns failure") {
        Given("a forecast client that returns a chance of rain")
        val forecastClient = mock[ForecastClient]
        (forecastClient.forecastForToday _)
          .expects()
          .returns(Right(Fixtures.rainyForecast))

        And("a publisher that throws an error")
        val error = new Exception("err")
        val publisher = mock[ForecastPublisher]
        (publisher.publish _).expects(*).returns(Try { throw error })

        And("an umbrella alert service")
        val umbrellaAlert = new UmbrellaAlert(
          forecastClient,
          publisher,
          Fixtures.umbrellaAlertConfig
        )

        When("alert might be sent")
        val res = umbrellaAlert.maybeSendAlert

        Then("result is an error")
        res.left.value shouldEqual UmbrellaAlertError.fromThrowable(error)

      }

    }

    describe("happy path") {
      it("publishes forecast if there is a chance of rain") {
        Given("a forecast client that returns a chance of rain")
        val forecastClient = mock[ForecastClient]
        (forecastClient.forecastForToday _)
          .expects()
          .returns(Right(Fixtures.rainyForecast))

        And("a publisher")
        val publisher = mock[ForecastPublisher]
        (publisher.publish _).expects(*).returns(Try(Unit))

        And("an umbrella alert service")
        val umbrellaAlert = new UmbrellaAlert(
          forecastClient,
          publisher,
          Fixtures.umbrellaAlertConfig
        )

        When("alert might be sent")
        val res = umbrellaAlert.maybeSendAlert

        Then("the result is an alert sent")
        res.right.value shouldBe AlertSent
      }
    }
  }
}
