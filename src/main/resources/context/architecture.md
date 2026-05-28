# Architecture Context

This project is Maven-based, using Spring Boot 4.0.6, Java 25, Spring Batch, Spring Batch JDBC, PostgreSQL JDBC, and Spring Batch test support. 

Spring Batch is used because this is a batch-processing app with jobs, steps, chunking, job parameters, execution status, restart metadata, and listeners.

Spring Batch JDBC is used for storing job metadata in PostgreSQL via JobRepository.

Spring Boot is mostly used for dependency management, bootstrapping, configuration loading, and executable jar packaging. 

This project is not using the usual Spring Boot web-app model.
