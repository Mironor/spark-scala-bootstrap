package com.dummy

import com.dummy.utils.Spark
import com.typesafe.config.ConfigFactory

import scala.math.random

object Main extends App {

  private val arguments = Arguments(args)

  lazy val config = ConfigFactory.load()

  if (arguments.piArgument()) runPi()
  if (arguments.verboseArgument()) Spark.session.sparkContext.setLogLevel("INFO") else Spark.session.sparkContext.setLogLevel("WARN")
  if (arguments.dummyArgument()) runExtractIndividuals()

  // IMPORTANT: this line should always remain the last "if" statement
  if (arguments.e2eTestsArgument()) runE2eTests()


  /**
    * This is useful when you need to just test whether your cluster can handle your jar without doing any aggregations
    */
  def runPi(): Unit = {
    val slices = 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val count = Spark.session.sparkContext.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x * x + y * y < 1) 1 else 0
    }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / n)
  }

  def runExtractIndividuals(): Unit = {
  }


  def runE2eTests(): Unit = E2ETests.runE2eTests()
}
