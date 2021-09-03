package com.dummy.models

object DummyModel {
  case class Dummy(dummy_string: String,
                   dummy_int: Int)

  case class DummyAggregated(dummy_string: String,
                             dummy_sum: Int)
}
