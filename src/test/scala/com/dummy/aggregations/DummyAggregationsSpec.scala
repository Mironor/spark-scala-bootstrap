package com.dummy.aggregations

import com.typesafe.config.{Config, ConfigFactory}
import com.dummy.E2ETests._
import com.dummy.models.DummyModel.{Dummy, DummyAggregated}
import com.dummy.utils.Spark
import org.scalatest.matchers.should._
import org.scalatest.wordspec._

class DummyAggregationsSpec extends AnyWordSpec with Matchers {

  import Spark.session.implicits._

  lazy val config: Config = ConfigFactory.load()

  "DummyAggregations" should {
    val pathToData = "src/test/resources/csv/dummy/aggregate_dummy"

    "aggregate dummies" in {
      // Given
      val dummies = readTestCsv(s"$pathToData/input_dummies.csv").as[Dummy]
      val dummiesAggregatedExpected = readTestCsv(s"$pathToData/output_dummies_aggregated.csv").as[DummyAggregated].collect().sortBy(_.toString)

      // When
      val result = DummyAggregations.aggregateDummy(dummies).as[DummyAggregated].collect().sortBy(_.toString)

      // Then
      result shouldBe dummiesAggregatedExpected
    }

  }
}
