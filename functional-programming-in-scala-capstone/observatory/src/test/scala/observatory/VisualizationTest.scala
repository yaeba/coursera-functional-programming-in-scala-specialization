package observatory

import org.junit.Assert._
import org.junit.Test

trait VisualizationTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("raw data display", 2) _

  // Implement tests for the methods of the `Visualization` object
  @Test def predictTemperature(): Unit = {
    val temperatures = Seq((Location(1, 1), 3.0), (Location(10, 10), 5.0))

    assertEquals(3.0, Visualization.predictTemperature(temperatures, Location(1, 1)), 0)

    assertEquals(5.0, Visualization.predictTemperature(temperatures, Location(9.999, 9.999)), 0)

    assert(Visualization.predictTemperature(temperatures, Location(9, 9)) < 5)
  }

  @Test def interpolateColor(): Unit = {
    assert(Visualization.interpolateColor(Seq((0, Color(0, 0, 0)), (100, Color(255, 255, 255))), 50) == Color(128, 128, 128))
    assert(Visualization.interpolateColor(Seq((0, Color(0, 0, 0)), (80, Color(255, 255, 255))), 10) == Color(32, 32, 32))
  }
}
