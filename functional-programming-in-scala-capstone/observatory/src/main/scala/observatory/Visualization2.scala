package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 extends Visualization2Interface {

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00   Top-left value
    * @param d01   Bottom-left value
    * @param d10   Top-right value
    * @param d11   Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
                             point: CellPoint,
                             d00: Temperature,
                             d01: Temperature,
                             d10: Temperature,
                             d11: Temperature
                           ): Temperature = {
    point match {
      case CellPoint(x, y) =>
        (d00 * (1 - x) * (1 - y)) +
          (d01 * (1 - x) * y) +
          (d10 * x * (1 - y)) +
          (d11 * x * y)
    }
  }

  /**
    * @param grid   Grid to visualize
    * @param colors Color scale to use
    * @param tile   Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
                     grid: GridLocation => Temperature,
                     colors: Iterable[(Temperature, Color)],
                     tile: Tile
                   ): Image = {
    System.gc()

    val width = 256
    val height = 256
    val subtileZoom = 8
    val x0 = tile.x * width
    val y0 = tile.y * height

    def colorToPixel(color: Color): Pixel = Pixel(color.red, color.green, color.blue, 127)

    val pixels = new Array[Pixel](width * height)

    for (y <- 0 until height) {
      for (x <- 0 until width) {
        val t = Tile(x + x0, y + y0, tile.zoom + subtileZoom)
        val loc = Interaction.tileLocation(t)
        val temperature = interpolate(grid, loc)
        pixels(y * width + x) = colorToPixel(Visualization.interpolateColor(colors, temperature))
      }
    }

    Image(width, height, pixels)
  }

  private def interpolate(grid: GridLocation => Temperature,
                          loc: Location): Temperature = {
    val Location(lat, long) = loc
    val d00 = grid(GridLocation(lat.floor.toInt, long.floor.toInt))
    val d01 = grid(GridLocation(lat.ceil.toInt, long.floor.toInt))
    val d10 = grid(GridLocation(lat.floor.toInt, long.ceil.toInt))
    val d11 = grid(GridLocation(lat.ceil.toInt, long.ceil.toInt))
    val point = CellPoint(loc.lon - long.floor.toInt, loc.lat - lat.floor.toInt)
    bilinearInterpolation(point, d00, d01, d10, d11)
  }
}
