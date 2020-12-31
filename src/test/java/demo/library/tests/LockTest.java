package demo.library.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import demo.library.TaskWithAuthorSelect;
import demo.library.TaskWithAuthorUpdate;

public class LockTest {
	
	private static EntityManagerFactory 	entityManagerFactory;
	
	@BeforeClass
	public static void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("demo");
	}
	private void simpleThreadPool(Runnable ...runnables) {
		List<Thread> threads = new ArrayList<>();
		for (Runnable r : runnables) {
			threads.add(new Thread(r));
		}
		for (Thread t : threads) {
			t.start();
		}
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Creates two threads with select tasks. 
	 * Task with OPTIMISTIC_FORCE_INCREMENT lock will modify Version field.
	 * All transactions have to be with status 'true'.
	 */
	@Test
	public void optimisticForceSelectTasks(){
    	TaskWithAuthorSelect selectTransactionForce = new TaskWithAuthorSelect(entityManagerFactory, 3L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    	TaskWithAuthorSelect selectTransaction = new TaskWithAuthorSelect(entityManagerFactory, 3L, LockModeType.OPTIMISTIC);
    	
    	simpleThreadPool(
    			selectTransactionForce, 
    			selectTransaction
    			);
    	
    	assertTrue(
    			"Force select transaction will increase version of entity. Both true.",
    			selectTransactionForce.getTaskStatus() && 
    			selectTransaction.getTaskStatus()
    			);
	}
	/**
	 * Creates two threads with update tasks. 
	 * Both transactions have OPTIMISTIC lock so only one of them will commit changes
	 * and another will rollback with exception.
	 * One transaction has 'false'.
	 */
	@Test
	public void optimisticExceptionUpdateTasks() throws InterruptedException {
		TaskWithAuthorUpdate up1 = new TaskWithAuthorUpdate(entityManagerFactory, 1L, LockModeType.OPTIMISTIC);
		TaskWithAuthorUpdate up2 = new TaskWithAuthorUpdate(entityManagerFactory, 1L, LockModeType.OPTIMISTIC);
    	
    	simpleThreadPool(
    			up1, 
    			up2
    			);
 
    	assertTrue(
    			"One transaction will wait try modify entity while another update. One transaction will be true another - false.",
    			up1.getTaskStatus() || 
    			up2.getTaskStatus()
    			);
	}
	/**
	 * Creates two threads with update tasks. 
	 * Both transactions have PESSIMISTIC_WRITE lock so one will wait 
	 * until the first transaction ends.
	 * All transactions have to be with status 'true'.
	 */
	@Test
	public void pesimisticUpdateTasks() {
		TaskWithAuthorUpdate up1 = new TaskWithAuthorUpdate(entityManagerFactory, 1L, LockModeType.PESSIMISTIC_WRITE);
		TaskWithAuthorUpdate up2 = new TaskWithAuthorUpdate(entityManagerFactory, 1L, LockModeType.PESSIMISTIC_WRITE);
    	
    	simpleThreadPool(
    			up1, 
    			up2
    			);
    	
    	assertTrue(
    			"One transaction will wait until another ends the update. Both statuses true.",
    			up1.getTaskStatus() && 
    			up2.getTaskStatus()
    			);
	}
	
	@AfterClass
	public static void close() {
		entityManagerFactory.close();
	}
}



//Date date = new Date();  
//Timestamp ts=new Timestamp(date.getTime());  
//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  


///* Create collector that peaks first value for stream -> collect method */
//private static <T> Collector<T, ?, T> getFirstValue() {
//  return Collectors.collectingAndThen(
//         Collectors.toList(),
//         tempList -> {
//              if (tempList.isEmpty()) {
//              	throw new IndexOutOfBoundsException();
//              } else {
//              	T firstValue = tempList.get(0);
//              	return firstValue;
//              }
//              
//         }
//  	);
//}