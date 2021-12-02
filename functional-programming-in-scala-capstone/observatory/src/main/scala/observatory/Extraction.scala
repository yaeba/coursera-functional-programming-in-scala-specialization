package observatory

import java.time.LocalDate

import scala.io.Source
import scala.util.Try

/**
  * 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    val stations = parseStations(stationsFile)
    val temperatures = parseTemperatues(temperaturesFile)

    locateTemperatures(year, stations, temperatures)
  }

  def locateTemperatures(year: Year, stations: Map[StationKey, Location], temperatures: Iterable[(StationKey, Int, Int, Temperature)]): Iterable[(LocalDate, Location, Temperature)] =
    temperatures.flatMap {
      case (stationKey, month, day, temperature) if stationKey.stn.isDefined || stationKey.wban.isDefined =>
        Try(LocalDate.of(year, month, day), stations(stationKey), fahrenheitToCelsius(temperature)).toOption
    }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    records
      .groupBy(_._2)
      .mapValues(xs => {
        val temperatures = xs.map(_._3)
        temperatures.sum / temperatures.size
      })
  }

  private def parseStations(stationFile: String): Map[StationKey, Location] = {
    Source.fromInputStream(getClass.getResourceAsStream(stationFile)).getLines
      .flatMap(_.split(",") match {
        case Array(stn, wban, lat, long) => Try(StationKey(tryInt(stn), tryInt(wban)), Location(lat.toDouble, long.toDouble)).toOption
        case _ => None
      })
      .toMap
  }

  private def parseTemperatues(temperatureFile: String): Iterable[(StationKey, Int, Int, Temperature)] = {
    Source.fromInputStream(getClass.getResourceAsStream(temperatureFile)).getLines
      .flatMap(_.split(",") match {
        case Array(stn, wban, month, day, temperature) if temperature != "9999.9" =>
          Try(StationKey(tryInt(stn), tryInt(wban)), month.toInt, day.toInt, temperature.toDouble).toOption
        case _ => None
      })
      .toIterable
  }

  private def tryInt = (x: String) => Try(x.toInt).toOption

  private def fahrenheitToCelsius: Temperature => Temperature = (f: Temperature) => (f - 32) / 1.8

}
