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

// @EnableMongoRepositories
// @Controller
public class MongoServiceTesting {
 
    private static final Logger logger = LoggerFactory.getLogger(MongoServiceTesting.class);


    @Autowired
	ItemRepository groceryItemRepo;
	
	@Autowired
	CustomItemRepository customRepo;
	
	List<GroceryItem> itemList = new ArrayList<GroceryItem>();

	// @Autowired	
	public MongoServiceTesting(ConflictMonitorProperties conflictMonitorProps) {
		
		// Clean up any previous data
		groceryItemRepo.deleteAll(); // Doesn't delete the collection
		
		logger.info("-------------CREATE GROCERY ITEMS-------------------------------\n");
		
		createGroceryItems();
		
		logger.info("\n----------------SHOW ALL GROCERY ITEMS---------------------------\n");
		
		showAllGroceryItems();
		
		logger.info("\n--------------GET ITEM BY NAME-----------------------------------\n");
		
		getGroceryItemByName("Whole Wheat Biscuit");
		
		logger.info("\n-----------GET ITEMS BY CATEGORY---------------------------------\n");
		
		getItemsByCategory("millets");
		
		logger.info("\n-----------UPDATE CATEGORY NAME OF ALL GROCERY ITEMS----------------\n");
		
		updateCategoryName("snacks");
		
		logger.info("\n-----------UPDATE QUANTITY OF A GROCERY ITEM------------------------\n");
		
		updateItemQuantity("Bonny Cheese Crackers Plain", 10);
		
		logger.info("\n----------DELETE A GROCERY ITEM----------------------------------\n");
		
		deleteGroceryItem("Kodo Millet");
		
		logger.info("\n------------FINAL COUNT OF GROCERY ITEMS-------------------------\n");
		
		findCountOfGroceryItems();
		
		logger.info("\n-------------------THANK YOU---------------------------");
						
	}
	
	// CRUD operations

	//CREATE
	void createGroceryItems() {
		logger.info("Data creation started...");

		groceryItemRepo.save(new GroceryItem("Whole Wheat Biscuit", "Whole Wheat Biscuit", 5, "snacks"));
		groceryItemRepo.save(new GroceryItem("Kodo Millet", "XYZ Kodo Millet healthy", 2, "millets"));
		groceryItemRepo.save(new GroceryItem("Dried Red Chilli", "Dried Whole Red Chilli", 2, "spices"));
		groceryItemRepo.save(new GroceryItem("Pearl Millet", "Healthy Pearl Millet", 1, "millets"));
		groceryItemRepo.save(new GroceryItem("Cheese Crackers", "Bonny Cheese Crackers Plain", 6, "snacks"));
		
		logger.info("Data creation complete...");
	}
	
	// READ
	// 1. Show all the data
	 public void showAllGroceryItems() {
		 
		 itemList = groceryItemRepo.findAll();
		 
		 itemList.forEach(item -> logger.info(getItemDetails(item)));
	 }
	 
	 // 2. Get item by name
	 public void getGroceryItemByName(String name) {
		 logger.info("Getting item by name: " + name);
		 GroceryItem item = groceryItemRepo.findItemByName(name);
		 logger.info(getItemDetails(item));
	 }
	 
	 // 3. Get name and items of a all items of a particular category
	 public void getItemsByCategory(String category) {
		 logger.info("Getting items for the category " + category);
		 List<GroceryItem> list = groceryItemRepo.findAll(category);
		 
		 list.forEach(item -> logger.info("Name: " + item.getName() + ", Quantity: " + item.getItemQuantity()));
	 }
	 
	 // 4. Get count of documents in the collection
	 public void findCountOfGroceryItems() {
		 long count = groceryItemRepo.count();
		 logger.info("Number of documents in the collection = " + count);
	 }
	 
	 // UPDATE APPROACH 1: Using MongoRepository
	 public void updateCategoryName(String category) {
		 
		 // Change to this new value
		 String newCategory = "munchies";
		 
		 // Find all the items with the category 
		 List<GroceryItem> list = groceryItemRepo.findAll(category);
		 
		 list.forEach(item -> {
			 // Update the category in each document
			 item.setCategory(newCategory);
		 });
		 
		 // Save all the items in database
		 List<GroceryItem> itemsUpdated = groceryItemRepo.saveAll(list);
		 
		 if(itemsUpdated != null)
			 logger.info("Successfully updated " + itemsUpdated.size() + " items.");		 
	 }
	 
	 
	 // UPDATE APPROACH 2: Using MongoTemplate
	 public void updateItemQuantity(String name, float newQuantity) {
		 logger.info("Updating quantity for " + name);
		 customRepo.updateItemQuantity(name, newQuantity);
	 }
	 
	 // DELETE
	 public void deleteGroceryItem(String id) {
		 groceryItemRepo.deleteById(id);
		 logger.info("Item with id " + id + " deleted...");
	 }
	 // Print details in readable form
	 
	 public String getItemDetails(GroceryItem item) {

		 logger.info(
		 "Item Name: " + item.getName() + 
		 ", \nItem Quantity: " + item.getItemQuantity() + 
		 ", \nItem Category: " + item.getCategory()
		 );
		 
		 return "";
	 }
    

}