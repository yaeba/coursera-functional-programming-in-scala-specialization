package observatory

import org.junit.Assert._
import org.junit.Test

import java.time.LocalDate

trait ExtractionTest extends MilestoneSuite {

  private val milestoneTest = namedMilestoneTest("data extraction", 1) _

  // Implement tests for the methods of the `Extraction` object
  @Test def locateTemperaturesTestData(): Unit = {
    val stations = Map(StationKey(Some(1), None) -> Location(1, 1))
    val temperatures = Seq((StationKey(Some(1), None), 1, 1, 77.0))
    val actual = Extraction.locateTemperatures(1975, stations, temperatures)
    println(actual)
    assert(actual.size == 1)
    assert(actual.head == (LocalDate.of(1975, 1, 1), Location(1.0, 1.0), 25.0))
  }

  @Test def locateTemperaturesRealData(): Unit = {
    val actual = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")
    assertTrue(actual.nonEmpty)
  }

  @Test def yearlyAvgTestData(): Unit = {
    val records = Seq(
      (LocalDate.of(2020, 1, 1), Location(1, 1), 10.0),
      (LocalDate.of(2020, 1, 2), Location(1, 1), 20.0),
      (LocalDate.of(2020, 1, 1), Location(2, 2), 30.0),
      (LocalDate.of(2020, 1, 2), Location(2, 2), 40.0)
    )
    val actual = Extraction.locationYearlyAverageRecords(records)
    val expected = Set(
      (Location(1, 1), 15),
      (Location(2, 2), 35)
    )
    assert(actual.toSet == expected)
  }
}
