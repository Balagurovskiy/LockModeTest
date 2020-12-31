package demo.library;

import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

import org.apache.log4j.Logger;

import demo.library.author.AuthorRepository;
import demo.library.author.Authors;
import demo.library.book.Books;
import demo.library.model.BaseEntity;
import demo.library.model.BaseRepository;
import demo.library.tag.Tags;
/**
 * Creates task to select specified entities (by id or name).
 * Creates its own EntityManager for transactions.
 * Lock mode type also have to be specified.
 * 
 * @author OlexiySergiyovich
 *
 */
public class TaskWithAuthorSelect implements Runnable{
	
	private EntityManagerFactory entityManagerFactory;
	private static final Logger LOGGER = Logger.getLogger(TaskWithAuthorSelect.class.getName());
	private Long	 		id;
	private String 			name;
	private LockModeType 	lockModeType;

	private boolean 		taskStatus;
	
	public TaskWithAuthorSelect(EntityManagerFactory entityManagerFactory, Long id, LockModeType lockModeType) {
		this.entityManagerFactory = entityManagerFactory;
		this.lockModeType = lockModeType;
		this.id = id;
		taskStatus = false;
	}
	
	public TaskWithAuthorSelect(EntityManagerFactory entityManagerFactory, String name, LockModeType lockModeType) {
		this.entityManagerFactory = entityManagerFactory;
		this.lockModeType = lockModeType;
		this.id = -1L;
		this.name = name;
		taskStatus = false;
	}

	private synchronized void print(Authors authors) {
		for(Books book : authors.getBooks()) {
			StringBuilder str = new StringBuilder();
			str.append("[selected author ");
			str.append(authors.getName());
			
			str.append(" { book : ");
			str.append(book.getName());
			str.append(" | version : ");
			str.append(book.getVersion() + "}");
			
    		for(Tags tag : book.getTags()) {
    			str.append(" { tag : ");
    			str.append(tag.getName());
    			str.append(" | version : ");
    			str.append(tag.getVersion() + "}");
    		}
    		str.append("]");
    		LOGGER.info(str.toString());
		}
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
				print(authors);
				taskStatus = true;
			}
		} catch (Exception e) {
//			LOGGER.error("[ select task failed while using author repository ]", e);
			LOGGER.error("[ select task failed while using author repository ]");
		}
	}

	public boolean getTaskStatus() {
		return taskStatus;
	}
}
