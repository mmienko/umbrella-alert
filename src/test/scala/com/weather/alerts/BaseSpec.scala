package com.weather.alerts

import org.scalamock.scalatest.MockFactory
import org.scalatest._

trait BaseSpec
    extends FunSpec
    with Matchers
    with MockFactory
    with GivenWhenThen
    with EitherValues
    with OptionValues
