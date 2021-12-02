package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization extends VisualizationInterface {

  val EARTH_RADIUS = 6372.8

  //////////
  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    val temperatureDistance = temperatures.map { case (loc, temperature) => (temperature, distance(loc, location)) }
    val min = temperatureDistance.minBy(_._2)
    if (min._2 < 1) {
      min._1
    } else {
      val power = 3
      val numerator = temperatureDistance.map { case (temperature, distance) => temperature / math.pow(distance, power) }.sum
      val denominator = temperatureDistance.map { case (_, distance) => 1 / math.pow(distance, power) }.sum
      numerator / denominator
    }
  }

  private def distance(x: Location, y: Location): Double = {
    import scala.math._
    if (x == y) {
      0d
    } else if ((x.lat == -y.lat) && (abs(x.lon - y.lon) == 180)) {
      EARTH_RADIUS * Pi
    } else {
      val deltaLong = toRadians(abs(x.lon - y.lon))
      val xLat = toRadians(x.lat)
      val yLat = toRadians(y.lat)
      val deltaLat = abs(xLat - yLat)
      val deltaSigma = 2 * asin(sqrt(pow(sin(deltaLat / 2), 2) + cos(xLat) * cos(yLat) * pow(sin(deltaLong / 2), 2)))
      EARTH_RADIUS * deltaSigma
    }
  }

  //////////

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    import scala.math.abs
    points.find { case (temperature, _) => temperature == value } match {
      case Some((_, color)) => color
      case _ =>
        val (smaller, bigger) = points.partition { case (temperature, _) => temperature < value }
        (smaller, bigger) match {
          case (Nil, _) => bigger.minBy { case (temperature, _) => temperature }._2
          case (_, Nil) => smaller.maxBy { case (temperature, _) => temperature }._2
          case _ =>
            val a = smaller.maxBy { case (temperature, _) => temperature }
            val b = bigger.minBy { case (temperature, _) => temperature }
            val colorA = a._2
            val colorB = b._2
            val ma = 1 / abs(a._1 - value)
            val mb = 1 / abs(b._1 - value)

            def interpolate(x: Int, y: Int) = ((ma * x + mb * y) / (ma + mb)).round.toInt

            Color(interpolate(colorA.red, colorB.red), interpolate(colorA.green, colorB.green), interpolate(colorA.blue, colorB.blue))
        }
    }
  }

  //////////

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    System.gc()

    val width = 360
    val height = 180

    def colorToPixel(color: Color): Pixel = Pixel(color.red, color.green, color.blue, 255)

    val pixels =
      for {
        y <- 0 until height
        x <- 0 until width
      } yield colorToPixel(interpolateColor(colors, predictTemperature(temperatures, Location(90 - y, x - 180))))

    Image(width, height, pixels.toArray)
  }

}

