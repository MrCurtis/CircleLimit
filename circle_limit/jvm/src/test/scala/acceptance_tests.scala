package circle_limit

import scala.math.{pow, sqrt, round}

import spire.math.Complex
import spire.implicits._
import utest._
import org.openqa.selenium.{By, WebElement, Dimension}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions

import circle_limit.maths.{Arc, Geodesic, SpaceType, Line}
import circle_limit.graphics.{Converter, Box, Vector}
import circle_limit.maths.CircleImplicits._


abstract class AcceptanceTestSuite extends TestSuite {

  val fileUrl = "file:///vagrant/circle_limit/circle_limit.html"

  def loadPage(driver: ChromeDriver) = {
    val displayWidth = 924
    val displayHeight = 924
    driver.get(fileUrl)
    PageObject(driver).resizeViewport(displayWidth, displayHeight)
  }

}


object InitialPageLayoutTests extends AcceptanceTestSuite {

  val tests = TestSuite {

    "title should be \"Circle Limit\""-{
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .assertTitle("Circle Limit")
      } finally {
        driver.quit()
      }
    }
    "boundary circle should be centred in browser window" - {
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .assertBoundaryCircleCentredInViewPort()
      } finally {
        driver.quit()
      }
    }
    "boundary circle diameter should be approximately 95% of viewport height" - {
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .assertBoundaryCircleToHeightRatioApproximately(0.95)
      } finally {
        driver.quit()
      }
    }
    "no geodesics should be displayed" - {
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .assertNumberOfGeodesicsPlotted(0)
      } finally {
        driver.quit()
      }
    }
  }

}


object MovableGeodisicTests extends AcceptanceTestSuite {

  val tests = TestSuite {

    "double clicking on two points should create a geodesic with handle points" - {
      val point1 = Complex(0.4, -0.1)
      val point2 = Complex(0.2, 0.5)
      val driver = new ChromeDriver()
      try {
        loadPage(driver)
          .doubleClickAtMathematicalPoint(point1)
          .doubleClickAtMathematicalPoint(point2)
          .assertHandlePointLocatedAtMathematicalPoint(point1)
          .assertHandlePointLocatedAtMathematicalPoint(point2)
          .assertGeodesicPlottedWithMathematicalEndpoints(point1, point2)
      } finally {
        driver.quit()
      }
    }
    "should be able to create multiple geodesics by double clicking" - {
      val pointA1 = Complex(0.4, -0.1)
      val pointA2 = Complex(0.2, 0.5)
      val pointB1 = Complex(0.7, 0.1)
      val pointB2 = Complex(0.1, -0.5)
      val pointC1 = Complex(-0.2, -0.5)
      val pointC2 = Complex(0.3, 0.6)
      val driver = new ChromeDriver()
      try {
        loadPage(driver)
          .doubleClickAtMathematicalPoint(pointA1)
          .doubleClickAtMathematicalPoint(pointA2)
          .assertHandlePointLocatedAtMathematicalPoint(pointA1)
          .assertHandlePointLocatedAtMathematicalPoint(pointA2)
          .assertGeodesicPlottedWithMathematicalEndpoints(pointA1, pointA2)
          .doubleClickAtMathematicalPoint(pointB1)
          .doubleClickAtMathematicalPoint(pointB2)
          .assertHandlePointLocatedAtMathematicalPoint(pointB1)
          .assertHandlePointLocatedAtMathematicalPoint(pointB2)
          .assertGeodesicPlottedWithMathematicalEndpoints(pointB1, pointB2)
          .doubleClickAtMathematicalPoint(pointC1)
          .doubleClickAtMathematicalPoint(pointC2)
          .assertHandlePointLocatedAtMathematicalPoint(pointC1)
          .assertHandlePointLocatedAtMathematicalPoint(pointC2)
          .assertGeodesicPlottedWithMathematicalEndpoints(pointC1, pointC2)
          .assertNumberOfGeodesicsPlotted(3)
      } finally {
        driver.quit()
      }
    }
    "double-clicking then single clicking should allow the creation of piece-wise geodesics" - {
      val point1 = Complex(0.4, -0.1)
      val point2 = Complex(0.2, 0.5)
      val point3 = Complex(0.3, -0.1)
      val point4 = Complex(-0.2, 0.5)
      val driver = new ChromeDriver()

      try {
        loadPage(driver)
          .doubleClickAtMathematicalPoint(point1)
          .assertHandlePointLocatedAtMathematicalPoint(point1)
          .singleClickAtMathematicalPoint(point2)
          .assertHandlePointLocatedAtMathematicalPoint(point2)
          .assertGeodesicPlottedWithMathematicalEndpoints(point1, point2)
          .singleClickAtMathematicalPoint(point3)
          .assertHandlePointLocatedAtMathematicalPoint(point3)
          .assertGeodesicPlottedWithMathematicalEndpoints(point2, point3)
          .doubleClickAtMathematicalPoint(point4)
          .assertHandlePointLocatedAtMathematicalPoint(point4)
          .assertGeodesicPlottedWithMathematicalEndpoints(point3, point4)
          .assertNumberOfGeodesicsPlotted(3)
      } finally {
        driver.quit()
      }
    }
    "piece-wise geodesics should be movable using handles" - {
      val point1 = Complex(0.4, -0.1)
      val initialPoint2 = Complex(0.2, 0.5)
      val destinationPoint2 = Complex(0.5, 0.2)
      val point3 = Complex(0.3, -0.1)
      val driver = new ChromeDriver()

      try {
        loadPage(driver)
          .createMultiGeodesicWithHandlesAtMathematicalPoints(List(point1, initialPoint2, point3))
          .dragHandleFromMathematicalPointToPoint(initialPoint2, destinationPoint2)
          .assertHandlePointLocatedAtMathematicalPoint(point1)
          .assertHandlePointLocatedAtMathematicalPoint(destinationPoint2)
          .assertHandlePointLocatedAtMathematicalPoint(point3)
          .assertGeodesicPlottedWithMathematicalEndpoints(point1, destinationPoint2)
          .assertGeodesicPlottedWithMathematicalEndpoints(destinationPoint2, point3)
          .assertNumberOfGeodesicsPlotted(2)
      } finally {
        driver.quit()
      }
    }
    "geodesic with both endpoint in top left quadrant should plot a circular arc"-{
      val point1 = Complex(0.5, -0.5)
      val point2 = Complex(-0.5, -0.5)
      val driver = new ChromeDriver()
      try {
        loadPage(driver)
          .createGeodesicWithHandlesAtMathematicalPoints(point1, point2)
          .assertGeodesicPlottedWithMathematicalEndpoints(point1, point2)
        } finally {
          driver.quit()
        }
    }
    "geodesic with both points on line through the origin should plot a line segment"-{
      // NOTE - We have chosen end points so that we don't get any floating point errors. Thus we can use exact
      // equality here. This is important as small errors would result in an arc rather than a line being plotted.
      val point1 = Complex(-0.6, 0.0)
      val point2 = Complex(0.6, 0.0)
      val driver = new ChromeDriver()
      try {
        loadPage(driver)
          .createGeodesicWithHandlesAtMathematicalPoints(point1, point2)
          .assertGeodesicPlottedWithMathematicalEndpoints(point1, point2, exact=true)
      } finally {
        driver.quit()
      }
    }
    "geodesic should be moveable using handles" - {
      val initialPoint1 = Complex(-0.2, 0.2)
      val destinationPoint1= Complex(0.5, -0.5)
      val initialPoint2 = Complex(0.2, 0.2)
      val destinationPoint2 = Complex(-0.5, -0.5)
      val driver = new ChromeDriver()
      try {
        loadPage(driver)
          .createGeodesicWithHandlesAtMathematicalPoints(initialPoint1, initialPoint2)
          .dragHandleFromMathematicalPointToPoint(initialPoint1, destinationPoint1)
          .dragHandleFromMathematicalPointToPoint(initialPoint2, destinationPoint2)
          .assertHandlePointLocatedAtMathematicalPoint(destinationPoint1)
          .assertHandlePointLocatedAtMathematicalPoint(destinationPoint2)
          .assertGeodesicPlottedWithMathematicalEndpoints(destinationPoint1, destinationPoint2)
          .assertNumberOfGeodesicsPlotted(1)
      } finally {
        driver.quit()
      }
    }
    "triple-clicking on handle points should delete them" - {
      val point1 = Complex(-0.1, 0.1)
      val point2 = Complex(0.2, 0.2)
      val point3 = Complex(0.3, -0.3)
      val point4 = Complex(-0.4, -0.4)
      val driver = new ChromeDriver()
      try {
        loadPage(driver)
          .createMultiGeodesicWithHandlesAtMathematicalPoints(List(point1, point2, point3, point4))
          .tripleClickAtMathematicalPoint(point2)
          .assertNumberOfHandlesPlotted(3)
          .assertGeodesicPlottedWithMathematicalEndpoints(point1, point3)
          .assertGeodesicPlottedWithMathematicalEndpoints(point3, point4)
      } finally {
        driver.quit()
      }
    }
    "if only two handles exist for a piece-wise geodesic then triple clicking one should delete whole geodesic" - {
      val point1 = Complex(-0.1, 0.1)
      val point2 = Complex(0.2, 0.2)
      val driver = new ChromeDriver()
      try {
        loadPage(driver)
          .createGeodesicWithHandlesAtMathematicalPoints(point1, point2)
          .tripleClickAtMathematicalPoint(point2)
          .assertNumberOfHandlesPlotted(0)
          .assertNumberOfGeodesicsPlotted(0)
      } finally {
        driver.quit()
      }
    }
  }
}


object ResizingTests extends AcceptanceTestSuite {

  val tests = TestSuite {

    "boundary circle re-sizes on viewport resize - landscape" - {
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .resizeViewport(400, 300)
          .assertBoundaryCircleCentredInViewPort()
          .assertBoundaryCircleToHeightRatioApproximately(0.95)
      } finally {
        driver.quit()
      }
    }
    "boundary circle re-sizes on viewport resize - portrait" - {
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .resizeViewport(400, 500)
          .assertBoundaryCircleCentredInViewPort()
          .assertBoundaryCircleToWidthRatioApproximately(0.95)
      } finally {
        driver.quit()
      }
    }
    "handles and geodesics should be correctly placed after resize - portrait" - {
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .resizeViewport(400, 300)
          .doubleClickAtGraphicalPoint(150, 150)
          .doubleClickAtGraphicalPoint(250, 150)
          .resizeViewport(800, 600)
          .assertHandleAtGraphicalPoint(300, 300)
          .assertHandleAtGraphicalPoint(500, 300)
          .assertGeodesicPlottedWithGraphicalEndpoints(
            300, 300,
            500, 300)
      } finally {
        driver.quit()
      }
    }
    "handles and geodesics should be correctly placed after resize - landscape" - {
      val driver = new ChromeDriver()
      try{
        loadPage(driver)
          .resizeViewport(400, 500)
          .doubleClickAtGraphicalPoint(150, 150)
          .doubleClickAtGraphicalPoint(250, 150)
          .resizeViewport(800, 1000)
          .assertHandleAtGraphicalPoint(300, 300)
          .assertHandleAtGraphicalPoint(500, 300)
          .assertGeodesicPlottedWithGraphicalEndpoints(
            300, 300,
            500, 300)
      } finally {
        driver.quit()
      }
    }
  }

}
