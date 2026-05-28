## How to build

Bash:

`./mvnw clean package`

Windows:

`.\mvnw.cmd clean package`

When using Eclipse IDE, the built jar is created here:

`${project_loc:ncmmis-batch}/target/ncmmis-batch-1.0.jar`


## How to run

Note that this is not a normal long-running Spring Boot app.
This is a command-line Spring Batch application that runs discrete batch jobs

The runtime entry point is Spring Batch’s CommandLineJobOperator.

The Maven Spring Boot plugin (configured in `pom.xml`) explicitly sets the executable main class to `org.springframework.batch.core.launch.support.CommandLineJobOperator`.

The intended command shape is as follows:

`java -jar target/ncmmis-batch-1.0.jar <job-config-class> start <job-name> [job-parameters]`

For example:

`java -jar ncmmis-batch-1.0.jar org.ncmmis.batch.provider.job.job1.ProviderJob1 start job1 name=Jeff,java.lang.String`


