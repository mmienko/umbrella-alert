package com.weather.alerts.core

import com.amazonaws.services.lambda.runtime.Context
import com.weather.alerts.{AwsUtil, BaseSpec}
import com.weather.alerts.models.{Forecast, LoggerStub}

class ForecastServiceIntegrationSpec extends BaseSpec {

  describe("ForecastService") {
    it("should get forecast and publish") {
      val service = new TestService()
      val context = mock[Context]
      (context.getLogger _).expects().anyNumberOfTimes().returning(LoggerStub())

      service.handleRequest(AwsUtil.makeScheduledEvent(), context)
      println(service.testResult)
    }
  }

  private class TestService(
    var testResult: Either[Exception, Forecast] = Left(new Exception("test exception"))
  ) extends ForecastService {
    override def handleForecast(testResult: Either[Exception, Forecast], isTest: Boolean, c: Context): Unit = {
      this.testResult = testResult
    }

    override protected def configs: ForecastServiceConfig = new ForecastServiceConfig {}
  }

}
