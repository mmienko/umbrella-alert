package com.weather.alerts

import com.amazonaws.services.lambda.runtime.LambdaLogger

case class LoggerStub() extends LambdaLogger {
  override def log(message: String): Unit = {}

  override def log(message: Array[Byte]): Unit = {}
}
