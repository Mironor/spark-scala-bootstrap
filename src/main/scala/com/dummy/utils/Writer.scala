package com.dummy.utils

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.DataFrame

object Writer {

  def writeParquet(dataFrame: DataFrame, path: String): Unit = dataFrame
    .write
    .format("parquet")
    .mode("overwrite") // this will only overwrite specific dataframe partitions thanks to partitionOverwriteMode=dynamic in the config
    .save(path)

  /**
    * Hard caches a dataframe to the path location, this is sometimes better than a "persist" or "cache" as instead of
    * saving data on the executor, it saves it in a parquet format somewhere else splitting the physical plan
    * into two parts
    *
    * @param dataFrame    data to be cached
    * @param path         cache's path
    * @param randomString this will be appended to the path as we need to have a fresh cache without some partition being obsolete
    * @return
    */
  def cache(dataFrame: DataFrame, path: String, randomString: String = ""): DataFrame = {
    writeParquet(dataFrame, path + randomString)
    Spark.session.read.parquet(path + randomString)
  }
}
