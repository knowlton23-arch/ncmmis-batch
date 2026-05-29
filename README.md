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

`java -Dspring.profiles.active=<env> -jar target/ncmmis-batch-1.0.jar <job-config-class> start <job-name> [job-parameters]`

For example:

`java -Dspring.profiles.active=local -jar ncmmis-batch-1.0.jar org.ncmmis.batch.provider.job.job1.ProviderJob1 start job1 name=Jeff,java.lang.String`


## ProviderJob3 restart demo

`ProviderJob3` is an instructional Spring Batch restartability job.

It reads the provider CSV, writes to `ncmmis_provider`, intentionally fails while processing provider id `350` during the first execution, and can then be restarted from the last committed chunk.

Start the job:

`java -Dspring.profiles.active=local -jar target/ncmmis-batch-1.0.jar org.ncmmis.batch.provider.job.job3.ProviderJob3 start job3 demoRun=restart-demo-1,java.lang.String`

After the intentional failure, restart the failed execution by execution id:

`java -Dspring.profiles.active=local -jar target/ncmmis-batch-1.0.jar org.ncmmis.batch.provider.job.job3.ProviderJob3 restart <job-execution-id>`

With the current chunk size of `100`, the first failed run should commit `300` provider rows before failing. The restart should continue from the last committed chunk and complete the remaining rows.

Use a new `demoRun` value for each fresh demonstration, or reset the Spring Batch metadata before reusing the same value.


