package com.weather.alerts.service

object ForecastPresenter {

  def present(forecast: Forecast): String = {
    s"""
       |Currently:
       |${summary(forecast.current)}
       |${temperature(forecast.current)}
       |${precipitationProb(forecast.current)}
       |
       |Day:
       |${forecast.today.map(summary).show}
       |${forecast.today.map(precipitationProb).show}
        """.stripMargin.trim
  }

  private def summary(fu: ForecastUnit) = fu.summary.show

  private def temperature(fu: ForecastUnit) =
    s"${fu.temperature.show}˚F feels like ${fu.apparentTemperature.show}˚F"

  private def precipitationProb(fu: ForecastUnit) = fu.precipType match {
    case None => "No precipitation"
    case Some(pType) =>
      fu.precipProbability
        .map(_ * 100)
        .map(math.round)
        .show + "% chance of " + pType
  }

  private implicit def optToOpt[T](option: Option[T]): CleanOption[T] =
    new CleanOption[T](option)

  private final class CleanOption[T](opt: Option[T]) {

    def show: String = {
      opt.map(_.toString).getOrElse("N/A")
    }
  }

}
