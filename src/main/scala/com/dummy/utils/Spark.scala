package com.dummy.utils

import org.apache.spark.sql.SparkSession

/**
  * Singleton holding reference to the Spark Session and initialising its parameters
  */
object Spark {
  lazy val session: SparkSession = {

    println("HELLO!")

    SparkSession.builder
      // this is important, it will overwrite specific partitions in path when saving a dataframe,
      // making your aggregations idempotent
      .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .appName("Data engineering").getOrCreate()
  }
}
