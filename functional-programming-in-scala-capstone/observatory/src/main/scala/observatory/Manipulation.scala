package observatory

/**
  * 4th milestone: value-added information
  */
object Manipulation extends ManipulationInterface {

  private class Grid {
    private val cache = scala.collection.mutable.HashMap[GridLocation, Temperature]()

    def set(lat: Int, long: Int, temperature: Temperature): Unit = cache(GridLocation(lat, long)) = temperature

    def get(gridLoc: GridLocation): Temperature = cache(gridLoc)

    def get(lat: Int, long: Int): Temperature = get(GridLocation(lat, long))


    //      def add(grid: Grid): Grid = {
    //        val newGrid = new Grid()
    //        (0 until width * height).foreach(idx => newGrid.cache(idx) = this.cache(idx) + grid.cache(idx))
    //        newGrid
    //      }
    //
    //      def subtract(grid: Grid): Grid = {
    //        val newGrid = new Grid()
    //        (0 until width * height).foreach(idx => newGrid.cache(idx) = this.cache(idx) - grid.cache(idx))
    //        newGrid
    //      }
    //
    //      def multiply(factor: Double): Grid = {
    //        val newGrid = new Grid()
    //        (0 until width * height).foreach(idx => newGrid.cache(idx) = this.cache(idx) * factor)
    //        newGrid
    //      }
    //
    //      def map(func: Temperature => Temperature): Grid = {
    //        val newGrid = new Grid()
    //        (0 until width * height).foreach(idx => newGrid.cache(idx) = func(this.cache(idx)))
    //        newGrid
    //      }
  }

  private object Grid {
    def apply(temperatures: Iterable[(Location, Temperature)]): Grid = {
      System.gc()
      val grid = new Grid()
      for {
        lat <- -89 to 90
        long <- -180 to 179
      } grid.set(lat, long, Visualization.predictTemperature(temperatures, Location(lat, long)))
      grid
    }
  }

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    val grid = Grid(temperatures)
    grid.get
  }

  /**
    * @param temperatures Sequence of known temperatures over the years (each element of the collection
    *                     is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperatures: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    val years = temperatures.map(makeGrid)

    gridLoc => {
      val yearlyTemperatures = years.map(_ (gridLoc))
      yearlyTemperatures.sum / yearlyTemperatures.size
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param normals      A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature): GridLocation => Temperature = {
    val grid = makeGrid(temperatures)
    gridLoc => grid(gridLoc) - normals(gridLoc)
  }
}

