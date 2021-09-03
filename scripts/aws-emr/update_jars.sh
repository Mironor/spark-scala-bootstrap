#!/usr/bin/env bash

cd ../../../st_oap_aws/ || exit

sbt clean assembly

aws s3 cp data-engineering/target/scala-2.12/data-engineering-0.0-RELEASE.jar s3://some-path
aws s3 cp ingestion/target/scala-2.12/stp-ingestion-0.0-RELEASE.jar s3://some-path
