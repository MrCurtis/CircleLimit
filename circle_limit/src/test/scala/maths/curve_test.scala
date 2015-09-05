package circle_limit

import utest._
import breeze.math._

object LineTestSuite extends TestSuite {
  val tests = TestSuite{

    "test Line can be intantiated"-{
      val line = Line(1.0+2.0*i, 3.0+4.0*i)
    }

    "values are accesible"-{
      val line = Line(1.0+2.0*i, 3.0+4.0*i)
      assert (line.start == 1.0+2.0*i)
      assert (line.finish == 3.0+4.0*i)
    }

    "two lines are equal if and only if the two end points are equal"-{
      val line1 = Line(1.0+2.0*i, 3.0+4.0*i)
      val line2 = Line(1.0+2.0*i, 3.0+4.0*i)
      val line3 = Line(3.0+4.0*i, 1.0+2.0*i)
      val line4 = Line(3.7+4.3*i, 8.0-2.0*i)
      val line5 = Line(1.0+2.0*i, 8.0-2.0*i)
      val line6 = Line(3.7+4.3*i, 3.0+4.0*i)

      assert (line1 == line1)
      assert (line1 == line2)
      assert (line2 == line1)
      assert (line1 == line3)
      assert (line3 == line1)
      assert (line1 != line4)
      assert (line4 != line1)
      assert (line1 != line5)
      assert (line5 != line1)
      assert (line1 != line6)
      assert (line6 != line1)
    }

  }
}

object ArcTestSuite extends TestSuite{
  val tests = TestSuite{

    "test Arc can be intantiated"-{
      val arc = Arc(1.0+2.0*i, 3.0+4.0*i, 5.0+6.0*i)
    }

    "values are accesible"-{
      val arc = Arc(1.0+2.0*i, 3.0+4.0*i, 5.0+6.0*i)
      assert (arc.start == 1.0+2.0*i)
      assert (arc.finish == 3.0+4.0*i)
      assert (arc.centre == 5.0+6.0*i)
    }

    "are equal only if centre and end points match"-{
      val arc1 = Arc(-1.0+0.0*i, 0.0+1.0*i, 0.0+0.0*i)
      val arc2 = Arc(-1.0+0.0*i, 0.0+1.0*i, 0.0+0.0*i)
      val arc3 = Arc(1.0+0.0*i, 0.0+1.0*i, 0.0+0.0*i)
      val arc4 = Arc(-1.0+0.0*i, 1.0+0.0*i, 0.0+0.0*i)
      val arc5 = Arc(-1.0+0.0*i, 0.0+1.0*i, -1.0+1.0*i)

      assert (arc1 == arc1)
      assert (arc1 == arc2)
      assert (arc2 == arc1)
      assert (arc1 != arc3)
      assert (arc3 != arc1)
      assert (arc1 != arc4)
      assert (arc4 != arc1)
      assert (arc1 != arc5)
      assert (arc5 != arc1)
    }

  }
}
