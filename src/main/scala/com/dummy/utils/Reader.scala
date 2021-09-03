package com.dummy.utils

import org.apache.spark.sql.{AnalysisException, Dataset, Encoder}

import scala.reflect.runtime.universe.TypeTag

object Reader {

  private def readParquetAs[T: Encoder : TypeTag](path: String): Dataset[T] =
    Spark.session.read.parquet(path).as[T]

  /**
    * This safe reading takes 1.5 minutes by it alone,
    * but it's safe and won't throw an exception if the dataset doesn't exist.
    * Useful when you want to update a dataset by merging the current one with the new one
    *
    * @param path path to the parquet file
    * @return DataFrame from the provided path, empty dataframe if there is nothing there
    */
  private def readParquetEmptySafe[T: Encoder : TypeTag](path: String): Dataset[T] =
    try readParquetAs[T](path)
    catch {
      case exception: AnalysisException =>
        println(s"Empty DataFrame read at $path")
        Spark.session.emptyDataFrame.as[T]
    }
}
