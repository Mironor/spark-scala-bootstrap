package com.dummy.utils

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._

/**
  * Contains re-usable UDFs that are not provided by Spark
  */
object SqlTools {
  val arrayRemoveNulls: UserDefinedFunction = udf((levers: Seq[String]) => levers.filter(_ != null))
  val sumValuesInArray: UserDefinedFunction = udf((values: Seq[Double]) => values.sum)
}
