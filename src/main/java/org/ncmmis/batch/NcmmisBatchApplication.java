package org.ncmmis.batch;

import org.springframework.batch.core.launch.support.CommandLineJobOperator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NcmmisBatchApplication {

	public static void main(String[] args) {
		
		// runApplication(args);
		
		// runAndCloseApplication(args);

		runApplicationFromCommandLine(args);
			
	}
	
	static void runApplication(String[] args) {
		System.out.println("This application just keeps running until someone stops it.");
		ConfigurableApplicationContext context = SpringApplication.run(NcmmisBatchApplication.class, args);		
		
		System.out.println("You can also stop it programmatically.");
		context.close();
	}
	
	static void runAndCloseApplication(String[] args) {
		System.out.println("This application runs and then automatically stops when processing is done.");
		SpringApplication.run(NcmmisBatchApplication.class, args).close();
	}

	static void runApplicationFromCommandLine(String[] args) {
		System.out.println("This application passes command line args to the CommandLineJobOperator.");
		CommandLineJobOperator.main(args);
		

		// To run this from a Terminal
		// C:\_workspace\ncmmis-batch\target>java -jar ncmmis-batch-1.0.jar org.ncmmis.batch.provider.job.ProviderJob1 start job1 name=Jeff,java.lang.String
	}
	
//    @Bean
//    public JobParametersConverter jobParametersConverter() {
//        // Implement this interface for adding CommandLineJobOperator params dynamically
//        return new MyCustomJobParametersConverter(); 
//    }
	
//	@Bean
//	public ExitCodeMapper exitCodeMapper() {
//		// Implement this interface for adding CommandLineJobOperator exit codes > 2
//		return new MyCustomExitCodeMapper();
//	}
	
}
