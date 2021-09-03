package com.dummy.aggregations

import com.dummy.models.DummyModel.{Dummy, DummyAggregated}
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.{col, sum}
import org.apache.spark.sql.types.IntegerType

object DummyAggregations {

  import com.dummy.utils.Spark.session.implicits._

  def aggregateDummy(dummies: Dataset[Dummy]): Dataset[DummyAggregated] = dummies
    .groupBy(col("dummy_string"))
    .agg(sum(col("dummy_int")).cast(IntegerType).as("dummy_sum"))
    .as[DummyAggregated]
}
