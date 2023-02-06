package us.dot.its.jpo.ode.messagesender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class TestMessageSenderApplication extends SpringBootServletInitializer {

	final static Logger logger = LoggerFactory.getLogger(TestMessageSenderApplication.class);


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TestMessageSenderApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(TestMessageSenderApplication.class, args);
		
	}

	
	

}
