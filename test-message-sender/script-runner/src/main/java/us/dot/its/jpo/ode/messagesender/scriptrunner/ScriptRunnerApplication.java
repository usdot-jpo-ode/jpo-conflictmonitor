package us.dot.its.jpo.ode.messagesender.scriptrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import us.dot.its.jpo.ode.messagesender.scriptrunner.hex.HexLogRunner;

@SpringBootApplication
@Profile("!messageSender")
public class ScriptRunnerApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ScriptRunnerApplication.class);

	public static void main(String[] args) {
		
		SpringApplication.run(ScriptRunnerApplication.class, args);
	}

	final String USAGE = 
		"\nCommand line script runner usage:\n" +
		"\n" +
		"Run Script:\n" +
		"  Executable jar:\n" +
		"    $ java -jar target/script-runner-cli.jar [filename]\n" +
		"  Maven:\n" +
		"    $ mvn spring-boot:run -Dspring-boot.run.arguments=[filename]\n" +
		"\n" +
		"Convert hex log to script:\n" +
		"  Executable jar:\n" +
		"   $ java -jar target/script-runner-cli.jar [docker host ip] [input filepath] [output filepath]\n";
		
	@Autowired
	ScriptRunner scriptRunner;

	@Autowired
	HexLogRunner hexLogRunner;

	@Autowired
	ThreadPoolTaskScheduler scheduler;

	@Override
	public void run(String... args) throws Exception {
		
		if (args.length != 1 && args.length != 3) {
			logger.info(USAGE);
			scheduler.shutdown();
			System.exit(0);
		}

		if (args.length == 1) {
			logger.info("\nRunning Script");
			String filePath = args[0];
			runScript(filePath);
			scheduler.shutdown();
			System.exit(0);
		}

		if (args.length == 3) {
			logger.info("\nConverting Hex Log to Script");
			String dockerHostIp = args[0];
			String inputFilePath = args[1];
			String outputFilePath = args[2];
			convertHexLogToScript(dockerHostIp, inputFilePath, outputFilePath);
		}
	}

	

	private void runScript(String filePath) throws FileNotFoundException {
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

	private void convertHexLogToScript(String dockerHostIp, String inputFilePath, String outputFilePath) throws IOException {
		logger.info("Docker Host IP: {}", dockerHostIp);
		logger.info("Input File Path: {}", inputFilePath);
		logger.info("Output File Path: {}", outputFilePath);
		var inputFile = new File(inputFilePath);
		if (!inputFile.exists()) {
			logger.warn("Input file {} does not exist.", inputFilePath);
			System.exit(1);
		}
		var outputFile = new File(outputFilePath);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		
		hexLogRunner.convertHexLogToScript(dockerHostIp, inputFile, outputFile);
		
		// scheduler.shutdown();
	}

}
