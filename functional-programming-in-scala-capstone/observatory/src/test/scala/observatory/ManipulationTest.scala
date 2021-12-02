package observatory

import org.junit.Test

trait ManipulationTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("data manipulation", 4) _

  // Implement tests for methods of the `Manipulation` object
  @Test def makeGrid(): Unit = {
    val t = Seq(
      (Location(1.0, 1.0), 1.0),
      (Location(2.0, 2.0), 2.0)
    )
    val f = Manipulation.makeGrid(t)

    assert(f(GridLocation(1, 1)) == 1.0)
    assert(f(GridLocation(2, 2)) == 2.0)
    assert(f(GridLocation(3, 3)) < 2.0)
  }

  @Test def average(): Unit = {
    val s = Seq(
      Seq(
        (Location(1.0, 1.0), 1.0),
        (Location(2.0, 2.0), 2.0)
      ),
      Seq(
        (Location(1.0, 1.0), 3.0),
        (Location(2.0, 2.0), 4.0)
      )
    )
    val f = Manipulation.average(s)

    assert(f(GridLocation(1, 1)) == 2.0)
    assert(f(GridLocation(2, 2)) == 3.0)
  }

  @Test def deviation(): Unit = {
    val t = Seq(
      (Location(1.0, 1.0), 1.0),
      (Location(2.0, 2.0), 2.0)
    )
    val normals = (gridLoc: GridLocation) => 1.0
    val f = Manipulation.deviation(t, normals)

    assert(f(GridLocation(1, 1)) == 0.0)
    assert(f(GridLocation(2, 2)) == 1.0)
  }
}
