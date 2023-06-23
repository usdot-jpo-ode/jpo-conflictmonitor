package us.dot.its.jpo.ode.messagesender.scriptrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import us.dot.its.jpo.ode.messagesender.scriptrunner.hex.HexLogRunner;

@SpringBootApplication
@Profile("!messageSender")
public class ScriptRunnerApplication implements ApplicationRunner  {

	private static final Logger logger = LoggerFactory.getLogger(ScriptRunnerApplication.class);

	public static void main(String[] args) {
		
		SpringApplication.run(ScriptRunnerApplication.class, args);
	}

	final String USAGE = 
		"\nUsage:\n\n" +
		"The tool can run in two modes: either run a script, or send a hex log to the ODE and create a script.\n\n" +
		"Run Script:\n\n" +
		"  Executable jar:\n" +
		"    $ java -jar script-runner-cli.jar <filename>\n\n" +
		"  Maven:\n" +
		"    $ mvn spring-boot:run -Dspring-boot.run.arguments=<filename>\n\n" +
		"  <filename> : (Required in script run mode) Input ODE JSON script file\n" +
		"               Formatted as line-delimited CSV/JSON like:\n" + 
		"               <BSM or MAP or SPAT>,<millisecond offset>,<Templated ODE JSON>\n\n" +
		"Send hex log to ODE and create script:\n\n" +
		"  Executable jar:\n" +
		"   $ java -jar script-runner-cli.jar --infile=<filename> --outfile=<filename> --ip=<docker host ip> ...\n\n" +
		"  Maven:\n" +
		"    $ mvn spring-boot:run -Dspring-boot.run.arguments=\"--infile=<filename> --outfile=<filename> ...\"\n\n" +
		"  Options: \n" +
		"    --hexfile=<filename> : (Required in hex log mode) Input hex log file\n" +
		"                           formatted as line-delimited JSON like\n" + 
		"                           { \"timeStamp\": <epoch milliseconds>, \"dir\": <\"S\" or \"R\">, \"hexMessage\": \"00142846F...\" }\n\n" +
		"    --outfile=<filename> : (Optional) Output file to save JSON received from the ODE as a script,\n" +
		"                           optionally substituting placeholders for timestamps\n\n" +
		"    --placeholders       : (Optional) If present substitute placeholders in the output script:\n\n" +
		"                           @ISO_DATE_TIME@ for 'odeReceivedAt'\n" +
		"                           @MINUTE_OF_YEAR@ for 'timeStamp' and 'intersection.moy' in SPATs\n" +
		"                           @MILLI_OF_MINUTE@ for 'intersection.timeStamp' in SPATs and 'secMark' in BSMs\n" +
		"						    @TEMP_ID@ for 'coreData.id' in BSMs\n\n" +
		"    --mapfile=<filename> : (Optional) Output file to save MAPs as line-delimited JSON\n\n" +
		"    --spatfile=<filename>: (Optional) Output file to save SPATs as line-delimited JSON\n\n" +
		"    --bsmfile=<filename> : (Optional) Output file to save BSMs as line-delimited JSON\n\n" +
		"    --ip=<docker host ip>: (Optional) IP address of docker host to send UDP packets to\n" +
		"                           Uses DOCKER_HOST_IP env variable if not specified.\n\n" +
		"    --delay=<milliseconds> : (Optional) Delay in milliseconds before starting to send messages.\n\n";

		
	@Autowired
	ScriptRunner scriptRunner;

	@Autowired
	HexLogRunner hexLogRunner;

	@Autowired
	ThreadPoolTaskScheduler scheduler;

	@Override
	public void run(ApplicationArguments appArgs) throws Exception {
		
		String[] args = appArgs.getSourceArgs();
		if (args.length == 0) exitUsage();

		/// If there is only one argument, run a script
		if (args.length == 1) {
			logger.info("\nRunning Script");
			String filePath = args[0];
			runScript(filePath);
			scheduler.shutdown();
			System.exit(0);
		}

		// Check for options needed to run hex log
		Set<String> optionNames = appArgs.getOptionNames();
		for (String optionName : optionNames) {
			logger.info("Option: {} = {}", optionName, appArgs.getOptionValues(optionName));
		}

		boolean missingOptions = false;
		if (!optionNames.contains("hexfile")) {
			logger.info("Missing option --hexfile=<filename>");
			missingOptions = true;
		}
		String infile = appArgs.getOptionValues("hexfile").get(0);
		String outfile = optionNames.contains("outfile") ? appArgs.getOptionValues("outfile").get(0) : null;		
		String ip = optionNames.contains("ip") ? appArgs.getOptionValues("ip").get(0) : null;
		int delay = optionNames.contains("delay") ? Integer.parseInt(appArgs.getOptionValues("delay").get(0)) : 0;
		boolean placeholders = optionNames.contains("placeholders");
		String mapfile = optionNames.contains("mapfile") ? appArgs.getOptionValues("mapfile").get(0) : null;
		String spatfile = optionNames.contains("spatfile") ? appArgs.getOptionValues("spatfile").get(0) : null;
		String bsmfile = optionNames.contains("bsmfile") ? appArgs.getOptionValues("bsmfile").get(0) : null;


		if (ip == null) {
			ip = System.getenv("DOCKER_HOST_IP");
			if (ip == null) {
				logger.info("Missing option --ip=<docker host ip> or DOCKER_HOST_IP environment variable");
				missingOptions = true;
			}
		}
		
		if (missingOptions) exitUsage();

		convertHexLogToScript(ip, infile, outfile, delay, placeholders, mapfile, spatfile, bsmfile);
		
	}

	private void exitUsage() {
		logger.info(USAGE);
		scheduler.shutdown();
		System.exit(0);
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

	private void convertHexLogToScript(String dockerHostIp, String inputFilePath, String outputFilePath, int delay,
			boolean placeholders, String mapFilePath, String spatFilePath, String bsmFilePath) throws IOException {
		logger.info("Docker Host IP: {}", dockerHostIp);
		logger.info("Input File Path: {}", inputFilePath);
		logger.info("Output File Path: {}", outputFilePath);
		logger.info("Delay: {}", delay);
		logger.info("Placeholders: {}", placeholders);
		logger.info("MAP File Path: {}", mapFilePath);
		logger.info("SPAT File Path: {}", spatFilePath);
		logger.info("BSM File Path: {}", bsmFilePath);

		var inputFile = new File(inputFilePath);
		if (!inputFile.exists()) {
			logger.warn("Input file {} does not exist.", inputFilePath);
			System.exit(1);
		}
		var outputFile = outputFilePath != null ? new File(outputFilePath) : null;
		var mapFile = mapFilePath != null ? new File(mapFilePath) : null;
		var spatFile = spatFilePath != null ? new File(spatFilePath) : null;
		var bsmFile = bsmFilePath != null ? new File(bsmFilePath) : null;
		
		
		hexLogRunner.convertHexLogToScript(dockerHostIp, inputFile, outputFile, delay, placeholders, mapFile, spatFile, bsmFile);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.shutdown();
	}

}
