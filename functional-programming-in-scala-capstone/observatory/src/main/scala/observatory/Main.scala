package observatory

import com.sksamuel.scrimage.writer

import java.io.File

object Main extends App {
//  val temperatureColors =
//    Seq(
//      (60d, Color(255, 255, 255)),
//      (32d, Color(255, 0, 0)),
//      (12d, Color(255, 255, 0)),
//      (0d, Color(0, 255, 255)),
//      (-15d, Color(0, 0, 255)),
//      (-27d, Color(255, 0, 255)),
//      (-50d, Color(33, 0, 107)),
//      (-60d, Color(0, 0, 0))
//    )
//
//  val deviationColors =
//    Seq(
//      (7d, Color(0, 0, 0)),
//      (4d, Color(255, 0, 0)),
//      (2d, Color(255, 255, 0)),
//      (0d, Color(255, 255, 255)),
//      (-2d, Color(0, 255, 255)),
//      (-7d, Color(0, 0, 255))
//    )
//
//  val range = 1975 to 1975
//
//  println("reading")
//
//  val averageTemperatures = range.map(year => {
//    val temperatures = Extraction.locateTemperatures(year, "/stations.csv", s"/$year.csv")
//    val averagedTemperatures = Extraction.locationYearlyAverageRecords(temperatures)
//    (year, averagedTemperatures)
//  })
//
//  println("finished reading")
//
//  def generateTemperatureImages(year: Int, tile: Tile, temperatures: Iterable[(Location, Double)]): Unit = {
//    val imageFile: File = new File(s"target/temperatures/$year/${tile.zoom}/${tile.x}-${tile.y}.png")
//    imageFile.getParentFile.mkdirs()
//
//    val img = Interaction.tile(temperatures, temperatureColors, tile)
//    img.output(imageFile)
//  }
//
//  Interaction.generateTiles(averageTemperatures, generateTemperatureImages)

}
