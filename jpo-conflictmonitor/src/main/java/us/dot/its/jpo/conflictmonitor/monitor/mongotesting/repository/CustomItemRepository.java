package us.dot.its.jpo.conflictmonitor.monitor.mongotesting.repository;

public interface CustomItemRepository {
	
	void updateItemQuantity(String itemName, float newQuantity);

}