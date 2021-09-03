#!/usr/bin/env bash

aws emr create-cluster \
--applications Name=Spark \
--ec2-attributes file://../ec2_attributes.json \
--release-label emr-6.2.0 \
--log-uri 's3n://stp-prod-services/emr/' \
--steps file://data_engineering.json \
--instance-groups file://../instance_groups.json \
--configurations file://../configurations.json \
--service-role EMR_DefaultRole \
--enable-debugging \
--name 'STP DATA PROD: data-engineering' \
--auto-terminate \
--scale-down-behavior TERMINATE_AT_TASK_COMPLETION \
--region eu-west-1 \
--tags env="prod" project="stp"