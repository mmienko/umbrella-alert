package com.weather.alerts

import java.util

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import org.joda.time.DateTime

object AwsUtil {

  def makeScheduledEvent(dt: DateTime = DateTime.now()): ScheduledEvent = {
    val event = new ScheduledEvent()
    event.setTime(dt)
    event.setDetail(new util.HashMap[String, AnyRef]())
    event.setRegion("us-east-1")
    event
  }

}
