package com.dummy

import org.rogach.scallop.{ScallopConf, ScallopOption}

/**
  * Handles application's parameters
  * https://github.com/scallop/scallop
  */
object Arguments {
  def apply(arguments: Array[String]) = new ScallopConf(arguments) {
    banner(
      """
         Runs data-engineering pipeline
      """
    )

    val piArgument = opt[Boolean](
      "pi",
      noshort = true,
      default = Some(false),
      descr = "Runs a simple PI calculation in case you just want to see if your platform works"
    )

    val verboseArgument = opt[Boolean](
      "verbose",
      default = Some(false),
      descr = "Turns on all the logs up to INFO"
    )

    val dummyArgument = opt[Boolean](
      "dummy",
      default = Some(false),
      descr = "Runs dummy aggregations"
    )

    val e2eTestsArgument = opt[Boolean](
      "e2e-tests",
      noshort = true,
      default = Some(false),
      descr = "Runs end-to-end tests on generated data (only nominal cases, use unit-tests to test for edge-cases)"
    )
    verify()
  }
}
