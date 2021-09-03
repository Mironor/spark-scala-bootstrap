package com.dummy

import com.dummy.models.DummyModel.Dummy
import com.dummy.utils.Spark
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{split => spark_split, _}
import org.apache.spark.sql.types._

object E2ETests {

  import Spark.session.implicits._

  private lazy val config = ConfigFactory.load()

  def readTestCsv(path: String, hasHeader: String = "true", escapeCharacter: String = "\""): DataFrame = {
    val input = Spark.session
      .read
      .format("csv")
      .option("header", hasHeader)
      .option("inferSchema", "true")
      .option("escape", escapeCharacter)
      .load(path)

    if (input.columns.contains("comment")) convertArrayStringsToArrayColumnsWithComment(input)
    else convertArrayStringsToArrayColumns(input)
  }

  /**
    * Sometimes you need to have arrays in your csv files (often this is only the case for test files)
    * this will allow yo to convert a column to an array type
    */
  private def convertArrayStringsToArrayColumns(dataFrame: DataFrame): DataFrame = {

    val SuffixSeparator = "%"
    // for when there's only one ellement in the array and it's an empty string
    // (otherwise it will be converted to null by spark reader)
    val ArrayWithEmptyStringElement = "%empty_string_element%"

    val columnsToConvert = dataFrame.columns.map(_.split(SuffixSeparator)).collect {
      case Array(columnName, "array") => (s"$columnName${SuffixSeparator}array", columnName, StringType)
      case Array(columnName, "array_int") => (s"$columnName${SuffixSeparator}array_int", columnName, IntegerType)
      case Array(columnName, "array_double") => (s"$columnName${SuffixSeparator}array_double", columnName, DoubleType)
      case Array(columnName, "array_timestamp") => (s"$columnName${SuffixSeparator}array_timestamp", columnName, TimestampType)
    }

    // for each column this will apply a `withColumn()` to the dataframe
    columnsToConvert.foldLeft(dataFrame) { case (dataFrameAcc, (fromName, toName, arrayType)) =>
      dataFrameAcc.withColumn(toName,
        when(col(fromName).isNull, array())
          .otherwise(when(col(fromName) === lit(ArrayWithEmptyStringElement), array(lit("")))
            .otherwise(spark_split(col(fromName), ","))))
        .withColumn(toName, col(toName).cast(ArrayType(arrayType)))
    }
  }

  /**
    * For tests purposes we may include "comment" column to your dataframe explaining which case the csv line is testing.
    * Otherwise next person will have to guess what the line is doing
    */
  private def convertArrayStringsToArrayColumnsWithComment(dataFrame: DataFrame): DataFrame =
    convertArrayStringsToArrayColumns(dataFrame)
      .drop("comment")

  /**
    * In unit tests you have your IDE to help you with comparing actual to expected
    * When e2e test fails, it's sometimes difficult to see the difference between the expected and the actual.
    * This shows the exact rows that have failed making this task much easier
    *
    * @param actual   actual dataframe
    * @param expected expected dataframe
    */
  private def showDiff(actual: Set[String], expected: Set[String]) = {
    val diff1 = actual.filterNot(expected)
    val diff2 = expected.filterNot(actual)
    if (diff1.nonEmpty || diff2.nonEmpty) {
      println("diff (ACTUAL without expected):")
      diff1.foreach(println)
      println("diff (EXPECTED without actual):")
      diff2.foreach(println)
    }
  }

  def runE2eTests(): Unit = {
    testDummy()

    // this is useful to know whether the test was successful
    // by something (CI/DI for example) that looks into docker's logs
    println("SUCCESS")
  }

  private def testDummy(): Unit = {
    val dummyExpected = readTestCsv(s"file:///data/expected_individuals_omnichannel.csv").as[Dummy].collect().sortBy(_.toString)
    val individualsOmnichannelActual = Spark.session.read.parquet(config.getString(s"data_engineering.computed.dummy")).as[Dummy]

    println("Testing dummy: RUNNING")
    showDiff(individualsOmnichannelActual.collect().sortBy(_.toString).map(_.toString).toSet, dummyExpected.map(_.toString).toSet)

    assert(individualsOmnichannelActual.collect().sortBy(_.toString).sameElements(dummyExpected), "Testing dymmy: FAILED")
    println("Testing dummy: SUCCESS")
  }
}
