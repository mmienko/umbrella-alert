package com.weather.alerts

import java.util

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger}
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers}

class ForecastServiceIntegrationSpec extends FunSpec with Matchers with MockFactory {

  describe("ForecastService") {
    it("should get forecast and publish") {
      val dt = DateTime.now()
      val service = new TestService()
      val event = new ScheduledEvent()
      event.setTime(dt)
      event.setDetail(new util.HashMap[String, AnyRef]())
      event.setRegion("us-east-1")
      val context = mock[Context]
      (context.getLogger _).expects().anyNumberOfTimes().returning(new StubLogger)

      service.handleRequest(event, context) shouldEqual dt
      println(service.msg)
    }
  }

  private class TestService(var msg: String = "") extends ForecastService {
    override def publishMessage(msg: String, isTest: Boolean, c: Context): Unit = {
      this.msg = msg
    }

    override protected def configs: ForecastServiceConfig = new ForecastServiceConfig {}
  }

  private class StubLogger extends LambdaLogger {
    override def log(message: String): Unit = {}

    override def log(message: Array[Byte]): Unit = {}
  }

}
