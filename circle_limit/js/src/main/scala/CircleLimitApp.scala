package circle_limit

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.DynamicImplicits.number2dynamic
import org.scalajs.dom.html
import spire.math.Complex
import spire.implicits._
import circle_limit.maths.CircleImplicits._

import circle_limit.maths.{
  Arc,
  Geodesic,
  Group,
  Line,
  MoebiusTransformation,
  SpaceType
}
import circle_limit.maths.Imaginary.i
import circle_limit.graphics.{Converter, Box, Vector}

@JSExport
object CircleLimitApp {
  @JSExport
  def main(): Unit = {

    val displayWidth = 880
    val displayHeight = 880
    val converter = Converter(
      Box(-1.0, -1.0, 2.0, 2.0),
      Box(0.0, 0.0, displayWidth, displayHeight)
    )
    val transformArc = converter.convertArcToSvg _
    val transformLine = converter.convertLineToSvg _
    val toMathematical = converter.convertFromGraphicalToMathematicalSpace _

    val document = js.Dynamic.global.document

    println("Working")

    var s = js.Dynamic.global.Snap(displayWidth, displayHeight)
    var bigCircle = s.circle(displayWidth/2, displayHeight/2, 440).attr(
      js.Dictionary("stroke" -> "black", "fill" -> "none", "id" -> "boundary-circle"))
    var startPoint = s.circle(0.4*displayWidth, 0.4*displayHeight, 16).attr(
      js.Dictionary("fill" -> "red", "id" -> "end-point-1", "class" -> "handle"))
    var endPoint = s.circle(0.6*displayWidth, 0.4*displayHeight, 16).attr(
      js.Dictionary("fill" -> "red", "id" -> "end-point-2", "class" -> "handle"))
    var geodesic = s.path("M 100 100 A 100 100 0 0 0 150 150").attr(
      js.Dictionary("stroke" -> "black", "fill" -> "none", "id" -> "geodesic", "class" -> "geodesic"))
    var startPointX = "0.0"
    var startPointY = "0.0"
    var endPointX = "0.0"
    var endPointY = "0.0"
    startPoint.drag(
      ((el: js.Dynamic, dX: Int, dY: Int, posX: Int, posY: Int) => 
        {
          val cx = (startPointX.toInt+dX.toInt)
          val cy = (startPointY.toInt+dY.toInt)
          el.attr(js.Dictionary("cx" -> cx, "cy" -> cy))

          val startPointVector = Vector(startPoint.attr("cx").toString.toDouble, startPoint.attr("cy").toString.toDouble)
          val endPointVector = Vector(endPoint.attr("cx").toString.toDouble, endPoint.attr("cy").toString.toDouble)
          val curve = Geodesic(
            toMathematical(startPointVector),
            toMathematical(endPointVector),
            SpaceType.PoincareDisc
          ).asCurve
          val svg = curve match {
            case arc: Arc => transformArc(arc)
            case line: Line => transformLine(line)
          }
          geodesic.attr(js.Dictionary("d" -> svg))
        }): js.ThisFunction, 
      ((el: js.Dynamic, x: Int, y: Int) => 
        {
          startPointX = el.attr("cx").toString
          startPointY = el.attr("cy").toString
        }): js.ThisFunction, 
      null)
    endPoint.drag(
      ((el: js.Dynamic, dX: Int, dY: Int, posX: Int, posY: Int) => 
        {
          val cx = (endPointX.toInt+dX.toInt)
          val cy = (endPointY.toInt+dY.toInt)
          el.attr(js.Dictionary("cx" -> cx, "cy" -> cy))

          val startPointVector = Vector(startPoint.attr("cx").toString.toDouble, startPoint.attr("cy").toString.toDouble)
          val endPointVector = Vector(endPoint.attr("cx").toString.toDouble, endPoint.attr("cy").toString.toDouble)
          val curve = Geodesic(
            toMathematical(startPointVector),
            toMathematical(endPointVector),
            SpaceType.PoincareDisc
          ).asCurve
          val svg = curve match {
            case arc: Arc => transformArc(arc)
            case line: Line => transformLine(line)
          }
          geodesic.attr(js.Dictionary("d" -> svg))
        }): js.ThisFunction, 
      ((el: js.Dynamic, x: Int, y: Int) => 
        {
          endPointX = el.attr("cx").toString
          endPointY = el.attr("cy").toString
        }): js.ThisFunction, 
      null)
    var currentHandles = new js.Array[js.Dynamic]
    s.dblclick(
      (event: js.Dynamic) => {
        val handle = s.circle(event.clientX, event.clientY, 16).attr(
          js.Dictionary("fill" -> "red", "class" -> "handle"))
        currentHandles.push(handle)
        println("currentHandles.length: %s".format(currentHandles.length))
        if (currentHandles.length == 2){
          val vector1 = Vector(
            currentHandles(0).attr("cx").toString.toDouble,
            currentHandles(0).attr("cy").toString.toDouble)
          val vector2 = Vector(
            currentHandles(1).attr("cx").toString.toDouble,
            currentHandles(1).attr("cy").toString.toDouble)
          val curve = Geodesic(
            toMathematical(vector1),
            toMathematical(vector2),
            SpaceType.PoincareDisc
          ).asCurve
          val svg = curve match {
            case arc: Arc => transformArc(arc)
            case line: Line => transformLine(line)
          }
          println("svg: %s".format(svg))
          s.path(svg).attr(
            js.Dictionary("stroke" -> "black", "fill" -> "none", "id" -> "geodesic", "class" -> "geodesic"))
          currentHandles = new js.Array[js.Dynamic]
        }
      }
    )
  }
}
