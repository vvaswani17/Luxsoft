/*
* Author :  Vijay Vaswani
* Date   :  14-09-2022
* Project:  developement
* */

import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class MeasureTest extends AnyFlatSpec {

  "Sensers" should "validate average" in {
    Statistics.measureCalc("src/test/resources", Measurements(0, 0, 0)).onComplete({
      case Success(value) =>
        val m = value.last.toSeq.sortWith((m, n) => (m._2._1.sum / m._2._2) > n._2._1.sum / n._2._2)
        assert((m(0)._2._1.sum / m(0)._2._2) == 82)
        assert((m(1)._2._1.sum / m(1)._2._2) == 82)
      case Failure(exception) => exception.printStackTrace()
    })
  }
}
