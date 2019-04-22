package com.weather.alerts.core

import com.amazonaws.services.lambda.runtime.Context
import com.weather.alerts.{AwsUtil, BaseSpec, Fixtures}
import com.weather.alerts.models.{Forecast, LoggerStub}
import io.circe.ParsingFailure
import io.circe.generic.auto._

class ForecastServiceSpec extends BaseSpec {

  describe("ForecastService") {
    it("should give left exception if fetchForecast fails") {
      val ex = new Exception("fetch failed")

      runService(new TestService {
        override def handleForecast(result: Either[Exception, Forecast], isTest: Boolean, c: Context): Unit = {
          calls += 1
          result shouldBe Left(ex)
          calls shouldBe 1
        }

        override protected def fetchForecast(): Either[Exception, String] = Left(ex)
      })
    }

    it("should give exception if json parse fails") {
      runService(new TestService {
        override def handleForecast(result: Either[Exception, Forecast], isTest: Boolean, c: Context): Unit = {
          calls += 1
          result.isLeft shouldBe true
          result.left.get.getCause.isInstanceOf[ParsingFailure] shouldBe true
          calls shouldBe 1
        }

        override protected def fetchForecast(): Either[Exception, String] = Right("a")
      })
    }

    it("should give string if good response") {

      runService(new TestService {
        override def handleForecast(result: Either[Exception, Forecast], isTest: Boolean, c: Context): Unit = {
          calls += 1
          result shouldBe Right(Fixtures.forecast)
          calls shouldBe 1
        }

        override protected def fetchForecast(): Either[Exception, String] = Right(Fixtures.forecastResponse)
      })

    }
  }

  private def runService(testService: TestService): Unit = {
    val context = mock[Context]
    (context.getLogger _).expects().anyNumberOfTimes().returning(LoggerStub())
    testService.handleRequest(AwsUtil.makeScheduledEvent(), context)
  }

  private trait TestService extends ForecastService {
    var calls = 0

    override protected def configs: ForecastServiceConfig = new ForecastServiceConfig {}
  }

}
