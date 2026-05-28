# NCMMIS Batch

## Overview

This is a command-line Spring Batch application that runs discrete batch jobs against PostgreSQL.

The project is currently in the prototype (instructional, bootstrap) stage and will eventually evolve into a much larger application.

## Goals

1. Be able to run discrete batch jobs via a scheduler.

## Application Flow

Note that this is not a normal long-running Spring Boot app.

The runtime entry point is Spring Batch’s CommandLineJobOperator.

The Maven Spring Boot plugin explicitly sets the executable main class to `org.springframework.batch.core.launch.support.CommandLineJobOperator` in pom.xml.

The intended command shape is as follows:

`java -jar target/ncmmis-batch-1.0.jar <job-config-class> start <job-name> [job-parameters]`

For example:

`java -jar ncmmis-batch-1.0.jar org.ncmmis.batch.provider.job.ProviderJob1 start job1 name=Jeff,java.lang.String`

