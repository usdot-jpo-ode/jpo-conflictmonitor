package us.dot.its.jpo.ode.messagesender.scriptrunner;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
public class ScriptRunnerApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ScriptRunnerApplication.class);

	public static void main(String[] args) {
		
		SpringApplication.run(ScriptRunnerApplication.class, args);
	}

	final String USAGE = 
		"\nCommand line script runner usage:\n" +
		"\n" +
		"Executable jar:\n" +
		"$ java -jar target/script-runner-cli.jar [filename]\n" +
		"\n" +
		"Maven:\n" +
		"$ mvn spring-boot:run -Dspring-boot.run.arguments=[filename]\n";
		
	@Autowired
	ScriptRunner scriptRunner;

	@Autowired
	ThreadPoolTaskScheduler scheduler;

	@Override
	public void run(String... args) throws Exception {
		
		if (args.length == 0) {
			logger.info(USAGE);
			return;
		}

		logger.info("\nRunning Script");

		String filePath = args[0];
		var file = new File(filePath);
		logger.info("File: {}", filePath);
		if (!file.exists()) {
			logger.warn("File does not exist.");
			System.exit(1);
		}
		scriptRunner.scheduleScript(file);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.shutdown();
	}

}
