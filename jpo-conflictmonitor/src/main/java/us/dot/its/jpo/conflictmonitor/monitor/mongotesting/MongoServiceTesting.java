package us.dot.its.jpo.conflictmonitor.monitor.mongotesting;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.mongotesting.model.GroceryItem;
import us.dot.its.jpo.conflictmonitor.monitor.mongotesting.repository.CustomItemRepository;
import us.dot.its.jpo.conflictmonitor.monitor.mongotesting.repository.ItemRepository;

import us.dot.its.jpo.conflictmonitor.monitor.mongotesting.CustomerRepository;

@EnableMongoRepositories
@Controller
public class MongoServiceTesting {
 
    private static final Logger logger = LoggerFactory.getLogger(MongoServiceTesting.class);


	@Autowired
	private CustomerRepository repository;

	@Autowired
	public void run() throws Exception {
  
	  repository.deleteAll();
  
	  // save a couple of customers
	  repository.save(new Customer("Alice", "Smith"));
	  repository.save(new Customer("Bob", "Smith"));
  
	  // fetch all customers
	  logger.info("Customers found with findAll():");
	  logger.info("-------------------------------");
	  for (Customer customer : repository.findAll()) {
		logger.info(customer.toString());
	  }
	  logger.info("");
  
	  // fetch an individual customer
	  logger.info("Customer found with findByFirstName('Alice'):");
	  logger.info("--------------------------------");
	  logger.info(repository.findByFirstName("Alice").toString());
  
	  logger.info("Customers found with findByLastName('Smith'):");
	  logger.info("--------------------------------");
	  for (Customer customer : repository.findByLastName("Smith")) {
		logger.info(customer.toString());
	  }

  
	}
    

}