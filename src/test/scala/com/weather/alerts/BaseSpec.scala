package com.weather.alerts

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, GivenWhenThen, Matchers}

trait BaseSpec extends FunSpec with Matchers with MockFactory with GivenWhenThen
