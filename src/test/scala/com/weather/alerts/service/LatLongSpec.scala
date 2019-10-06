package com.weather.alerts.service

import com.weather.alerts.BaseSpec
import org.scalatest.EitherValues

class LatLongSpec extends BaseSpec with EitherValues {

  describe("LatLong") {
    describe("parse") {
      it("should return error if not parsable") {
        LatLong
          .parse("foo")
          .left
          .value shouldEqual "Latitude & Longitude, \"foo\", must be comma separated doubles, i.e. 40.741,-74.005"

        LatLong
          .parse("abc,23.005")
          .left
          .value shouldEqual "Latitude & Longitude, \"abc,23.005\", must be comma separated doubles, i.e. 40.741,-74.005"

        LatLong
          .parse("23.005,abc")
          .left
          .value shouldEqual "Latitude & Longitude, \"23.005,abc\", must be comma separated doubles, i.e. 40.741,-74.005"
      }

      it("should return LatLong if parsable") {
        LatLong.parse("23.005,4.04").right.value shouldEqual LatLong(
          "23.005",
          "4.04"
        )

        LatLong.parse("40.7410429,-74.0051566").right.value shouldEqual LatLong(
          "40.7410429",
          "-74.0051566"
        )

        LatLong.parse("23.005,  4.04").right.value shouldEqual LatLong(
          "23.005",
          "4.04"
        )

        LatLong.parse("23.005  ,4.04").right.value shouldEqual LatLong(
          "23.005",
          "4.04"
        )

        LatLong.parse("  23.005  ,  4.04  ").right.value shouldEqual LatLong(
          "23.005",
          "4.04"
        )
      }
    }
  }
}
