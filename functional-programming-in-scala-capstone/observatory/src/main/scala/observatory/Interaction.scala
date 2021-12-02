package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction extends InteractionInterface {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    import scala.math._

    val n = math.pow(2.0, tile.zoom)
    val lat = atan(sinh(Pi - tile.y / n * 2 * Pi)) * 180 / Pi
    val long = (tile.x / n * 360.0) - 180.0
    Location(lat, long)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @param tile         Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val power = 8
    val width = 256
    val height = 256
    val alpha = 127

    System.gc()

    def colorToPixel(color: Color): Pixel = Pixel(color.red, color.green, color.blue, alpha)

    val pixels =
      for {
        y <- 0 until height
        x <- 0 until width
      } yield {
        val t = Tile(x + (tile.x * math.pow(2, power)).toInt, y + (tile.y * math.pow(2, power)).toInt, power + tile.zoom)

        colorToPixel(Visualization.interpolateColor(colors, Visualization.predictTemperature(temperatures, tileLocation(t))))
      }

    Image(width, height, pixels.toArray)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    *
    * @param yearlyData    Sequence of (year, data), where `data` is some data associated with
    *                      `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
                           yearlyData: Iterable[(Year, Data)],
                           generateImage: (Year, Tile, Data) => Unit
                         ): Unit = {
    for {
      zoom <- 0 until 4
      x <- 0 until math.pow(2, zoom).toInt
      y <- 0 until math.pow(2, zoom).toInt
      (year, data) <- yearlyData
    } yield generateImage(year, Tile(x, y, zoom), data)
  }

}
