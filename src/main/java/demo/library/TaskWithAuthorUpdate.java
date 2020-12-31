package demo.library;

import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

import org.apache.log4j.Logger;

import demo.library.author.AuthorRepository;
import demo.library.author.Authors;
import demo.library.model.BaseEntity;
import demo.library.model.BaseRepository;
/**
 * Creates task to update specified entities (by id or name).
 * Creates its own EntityManager for transactions.
 * Lock mode type also have to be specified.
 * 
 * @author OlexiySergiyovich
 *
 */
public class TaskWithAuthorUpdate implements Runnable{
	
	private EntityManagerFactory entityManagerFactory;
	private static final Logger LOGGER = Logger.getLogger(TaskWithAuthorUpdate.class.getName());
	
	private Long	 		id;
	private String 			name;
	private LockModeType 	lockModeType;
	
	private boolean 		taskStatus;
	
	public TaskWithAuthorUpdate(EntityManagerFactory entityManagerFactory, Long id, LockModeType lockModeType) {
		this.entityManagerFactory = entityManagerFactory;
		this.lockModeType = lockModeType;
		this.id = id;
		taskStatus = false;
	}
	
	public TaskWithAuthorUpdate(EntityManagerFactory entityManagerFactory, String name, LockModeType lockModeType) {
		this.entityManagerFactory = entityManagerFactory;
		this.lockModeType = lockModeType;
		this.id = -1L;
		this.name = name;
		taskStatus = false;
	}
	
	private synchronized void print(Authors authors) {
		LOGGER.info("[ " + authors.getName() + " was updated]" );
	}
	
	@Override
	public void run() {
		try(BaseRepository repository = new AuthorRepository(entityManagerFactory)){
			Optional<BaseEntity> entity = null;
			
			repository.setLockModeType(lockModeType);
			
			if (name != null) {
				entity = repository.findByName( name );
			} else if (id >= 0) {
				entity = repository.findById( id );
			} else {
				entity = repository.findById(1L);
			}
			
			if (entity.isPresent()) {
				Authors authors = (Authors) entity.get();
				if (repository.update(authors, null).isPresent()) {
					taskStatus = true;
					print(authors);
				}
			}
		} catch (Exception e) {
//			LOGGER.error("[ update task failed while using author repository ]", e);
			LOGGER.error("[ update task failed while using author repository ]");
		}
	}
	
	public boolean getTaskStatus() {
		return taskStatus;
	}
}
