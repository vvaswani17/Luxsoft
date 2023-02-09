/*
* Author :  Vijay Vaswani
* Date   :  14-09-2022
* Project:  developement
* */

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import java.io.File
import java.nio.file.{Files, Paths}
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Statistics extends App {


  val measure = Measurements(0, 0, 0)

  measureCalc(args(0),measure).onComplete({
      case Success(value) =>
        println(s"Num of processed files: ${measure.processedFile}")
        println(s"Num of processed measurements: ${measure.processedMeasurements}")
        println(s"Num of failed measurements: ${measure.failedMeasurements}")
        println()
        println("Sensors with highest avg humidity:")
        println()
        println("sensor-id,min,avg,max")
        value.last.toSeq.sortWith((m, n) => (m._2._1.sum / m._2._2) > n._2._1.sum / n._2._2).foreach(m => {
          if (m._2._1.nonEmpty && (List(0) diff m._2._1).nonEmpty) {
            println(s"${m._1},${m._2._1.min},${m._2._1.sum / m._2._2},${m._2._1.max}")
          } else println(s"${m._1},NaN,NaN,NaN")
        })
      case Failure(exception) => exception.printStackTrace()
    })

  def measureCalc(folderLocation:String,measure:Measurements):Future[Seq[Map[String,(List[Int],Int)]]]={
    implicit val actorSystem: ActorSystem = ActorSystem("system")

    val map = scala.collection.mutable.Map[String, (List[Int], Int)]()

    val file = new File(folderLocation)
    val listOfFiles = file.listFiles.filter(_.isFile).map(_.getPath).toList
    val source = Source(listOfFiles)

    val mapper = Flow[String].map(new File(_))

    val existsFilter = Flow[File].filter(_.exists())

    val lengthZeroFilter = Flow[File].filter(_.length() != 0)

    val contents = Flow[File].map(m => {
      measure.processedFile = measure.processedFile + 1
      Files.lines(Paths.get(m.getPath)).skip(1).toArray().map(_.toString)
        .map(_.split(" ").mkString)
        .map(m => m.split(","))
        .map(m => {
          measure.processedMeasurements = measure.processedMeasurements + 1
          if (!map.contains(m(0))) {
            if (m(1) != "NaN") map += (m(0) -> (List(m(1).toInt), 1)) else {
              measure.failedMeasurements = measure.failedMeasurements + 1
              map += (m(0) -> (List(0), 1))
            }
          } else {
            if (m(1) != "NaN") map += (m(0) -> (map(m(0))._1 :+ m(1).toInt, map(m(0))._2 + 1))
            else measure.failedMeasurements = measure.failedMeasurements + 1
          }
        })

      map.toMap
    })

    source
      .via(mapper)
      .via(existsFilter)
      .via(lengthZeroFilter)
      .via(contents)
      .runWith(Sink.seq)
  }
}
