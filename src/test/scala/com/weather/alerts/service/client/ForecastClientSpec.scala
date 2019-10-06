package com.weather.alerts.service.client

import com.softwaremill.sttp.testing.SttpBackendStub
import com.softwaremill.sttp.{Request, Id, StatusCodes, MonadError, EitherBackend, Response, SttpBackend}
import com.weather.alerts.BaseSpec
import com.weather.alerts.service.client.ForecastClientFixtures.forecastClientConfig
import com.weather.alerts.service.{ForecastError, Fixtures}
import io.circe.generic.auto._
import org.scalatest.{OptionValues, EitherValues}

class ForecastClientSpec extends BaseSpec with EitherValues with OptionValues {

  describe("ForecastService") {
    describe("error path") {
      it("should return ForecastError if exception in http client") {
        val ex = new Exception("fetch failed")
        val client = new ForecastClient(
          Fixtures.todaysDate,
          forecastClientConfig,
          httpClient = new EitherBackend[Nothing](new ErrorThrowingBackend(ex))
        )

        client.forecastForToday().left.value shouldEqual ForecastError(
          "Exception: fetch failed"
        )
      }

      it("should return ForecastError if json parsing fails") {
        val client = new ForecastClient(
          Fixtures.todaysDate,
          forecastClientConfig,
          httpClient = new EitherBackend[Nothing](
            SttpBackendStub.synchronous.whenAnyRequest.thenRespond("a")
          )
        )
        client
          .forecastForToday()
          .left
          .value shouldBe ForecastError(
          "ParsingFailure: expected json value got a (line 1, column 1)"
        )
      }

      it("should return ForecastError if server responds with 500") {
        val client = new ForecastClient(
          Fixtures.todaysDate,
          forecastClientConfig,
          httpClient = new EitherBackend[Nothing](
            SttpBackendStub.synchronous.whenAnyRequest.thenRespondServerError()
          )
        )
        client.forecastForToday().left.value shouldEqual ForecastError(
          "DarkSky returned 500: Internal server error"
        )
      }

      it("should return ForecastError if server responds with 400") {
        val client = new ForecastClient(
          Fixtures.todaysDate,
          forecastClientConfig,
          httpClient = new EitherBackend[Nothing](
            SttpBackendStub.synchronous.whenAnyRequest.thenRespond(
              Response(
                Left("client error"),
                StatusCodes.BadRequest,
                StatusCodes.BadRequest.toString
              )
            )
          )
        )
        client.forecastForToday().left.value shouldEqual ForecastError(
          "DarkSky returned 400: client error"
        )
      }

      it(
        "should return ForecastError if server responds with 200, but no hourly forecast for today"
      ) {
        val client = new ForecastClient(
          Fixtures.todaysDate,
          forecastClientConfig,
          httpClient = new EitherBackend[Nothing](
            SttpBackendStub.synchronous.whenAnyRequest
              .thenRespond(ForecastClientFixtures.noHourlyForecastResponse)
          )
        )

        client.forecastForToday().left.value shouldEqual ForecastError(
          "DarkSky did not return the hourly forecast for today"
        )
      }
    }

    describe("happy path") {
      it(
        "should return Forecast if server responds with 200 and forecast for today"
      ) {
        val client = new ForecastClient(
          Fixtures.todaysDate,
          forecastClientConfig,
          httpClient = new EitherBackend[Nothing](
            SttpBackendStub.synchronous.whenAnyRequest
              .thenRespond(ForecastClientFixtures.forecastResponse)
          )
        )

        client.forecastForToday().right.value shouldEqual ForecastClient
          .getTodaysForecast(
            Fixtures.todaysDate,
            ForecastClientFixtures.forecast
          )
          .value
      }
    }

    describe("`getTodaysForecast`") {
      it("should get forecast for the passed date") {
        val forecast = ForecastClient
          .getTodaysForecast(
            Fixtures.forecastDate,
            ForecastClientFixtures.darkSkyForecast
          )
          .value

        Fixtures.timeToLocalDate(forecast.current.time) shouldEqual Fixtures.forecastDate
        Fixtures.timeToLocalDate(forecast.today.value.time) shouldEqual Fixtures.forecastDate
        forecast.hourly.size should be > 0
        forecast.hourly.foreach(hour => {
          Fixtures.timeToLocalDate(hour.time) shouldEqual Fixtures.forecastDate
        })
      }
    }
  }

  private class ErrorThrowingBackend(exception: Exception)
      extends SttpBackend[Id, Nothing] {
    override def send[T](request: Request[T, Nothing]): Id[Response[T]] =
      throw exception

    override def close(): Unit = ???

    override def responseMonad: MonadError[Id] = ???
  }

}
