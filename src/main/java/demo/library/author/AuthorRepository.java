package demo.library.author;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.apache.log4j.Logger;

import demo.library.model.BaseEntity;
import demo.library.model.BaseRepository;

public class AuthorRepository extends BaseRepository {
	
	private static final Logger LOGGER = Logger.getLogger(AuthorRepository.class.getName());
	
	public AuthorRepository(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}
/**
 * Creates Query to find entity.
 * Retrieve all records from authors table
 * @return list with query results
 */
	@Override
	public List<BaseEntity> findAll() {
		return entityManager.createQuery("SELECT a FROM Authors a").getResultList();
	}
/**
 * Creates Query to find entity. 
 * Find records where authors name contains some string.
 * @param String pattern to search in authors names.
 * @return list with query results.
 */
	@Override
	public List<BaseEntity> findLikeName(String pattern) {
		String queryJpql = "SELECT a FROM Authors a WHERE LOWER(a.name) LIKE LOWER(:pattern)";
	    Query query = entityManager.createQuery(queryJpql);
	    query.setParameter("pattern", pattern);
		return query.getResultList( );
	}
/**
 * Creates Query to find entity. 
 * Find record where author name is exactly as input string.
 * @param String name to search in authors such name.
 * @return base entity where name field has same string value as argument.
 */	
	@Override
	public Optional<BaseEntity> findByName(String name) {
		String queryJpql = "SELECT a FROM Authors a WHERE LOWER(a.name) = LOWER(:name)";
		Query query = entityManager.createQuery(queryJpql, Authors.class);
		Authors author = (Authors) query.setParameter("name", name).getSingleResult();
        if (author == null) {
    		LOGGER.info("[ nothing was found with such argument (String name) ]");
        	return Optional.empty();
        }
        return Optional.of(author);
	}
/**
 * Creates Transaction to find entity, specifies lock. 
 * Find record where author id is exactly as input long id.
 * @param LOng id to search in authors such id.
 * @return base entity where id field has same Long value as argument.
 */	
	@Override
	public Optional<BaseEntity> findById(Long id) {
		Authors author = null;
		EntityTransaction transaction = entityManager.getTransaction();
        try {
        	transaction.begin();
        	
        	author = entityManager.find(Authors.class, id, this.lockModeType);
        	if (transaction.isActive()) {
    			transaction.commit();
    		}
        } catch (IllegalArgumentException | NullPointerException e) {
    	    if ( transaction != null && transaction.isActive() ) {
    	    	transaction.rollback();
    	    }
//    	    LOGGER.warn("[ failed to make transaction ]", e);
    	    LOGGER.warn("[ failed to make transaction ]");
        }
    	if (author == null) {
    		LOGGER.info("[ nothing was found with such argument (Long id) ]");
        	return Optional.empty();
        }
        return Optional.of(author);
	}
/**
 * Creates Transaction to persist entity, specifies lock.
 * If catches IllegalArgumentException | NullPointerException makes 'rollback'.
 * @param Long id to search in authors such id.
 * @return base entity where id field has same Long value as argument.
 */	
	@Override
	public Optional<BaseEntity> save(BaseEntity entity) {
		Authors author = (Authors) entity;
		EntityTransaction transaction = entityManager.getTransaction();
        try {
        	transaction.begin();
        	
        	entityManager.lock(author, this.lockModeType);
            entityManager.persist(author);
            
    		if (transaction.isActive()) {
    			transaction.commit();
    		}
            return Optional.of(author);
        } catch (IllegalArgumentException | NullPointerException e) {
    	    if ( transaction != null && transaction.isActive() ) {
    	    	transaction.rollback();
    	    }
//    	    LOGGER.warn("[ failed to make transaction ]", e);
    	    LOGGER.warn("[ failed to make transaction ]");
        }
		LOGGER.info("[ nothing was saved cuz there was an exception ]");
        return Optional.empty();
	}
/**
 * Creates Queries and Transaction to remove entity, specifies lock.
 * Removes author entity, joined book entity(if it has no other authors) and joined book tag.
 * If catches IllegalArgumentException | NullPointerException makes 'rollback'.
 * @param entity to remove.
 * @return removed entity on success or empty.
 */	
	@Override
	public Optional<BaseEntity> delete(BaseEntity entity) {
		Authors author = (Authors) entity;
		EntityTransaction transaction = entityManager.getTransaction();
    	if(author != null) {
            try {
            	transaction.begin();
        		
        		Query standaloneAuthorBooks = entityManager
        				.createNativeQuery(
        				"SELECT ab.book_id FROM author_book ab "
        				+ "JOIN author_book ab2 ON ab.book_id = ab2.book_id "
        				+ "WHERE ab.author_id = ? "
        				+ "GROUP BY ab.book_id HAVING COUNT(ab.book_id) = 1"
        				);
        		standaloneAuthorBooks.setParameter(1, author.getId());
        		standaloneAuthorBooks.setLockMode(lockModeType);
        		List<Integer> bookIdsOfOneAuthor = (List<Integer>) standaloneAuthorBooks.getResultList();
        		
        		if (bookIdsOfOneAuthor.isEmpty() == false) {
        			
            		Query deleteStandAloneBooks = entityManager.createNativeQuery(
            				"DELETE FROM Books b WHERE b.id IN (:id)"
            				);
            		deleteStandAloneBooks.setParameter("id", bookIdsOfOneAuthor);
            		deleteStandAloneBooks.setLockMode(lockModeType);
            		deleteStandAloneBooks.executeUpdate();
            		
            		Query deleteStandAloneBookTagLinks = entityManager.createNativeQuery(
            				"DELETE FROM Book_tag bt WHERE bt.book_id IN (:id)"
            				);
            		deleteStandAloneBookTagLinks.setParameter("id", bookIdsOfOneAuthor);
            		deleteStandAloneBookTagLinks.setLockMode(lockModeType);
            		deleteStandAloneBookTagLinks.executeUpdate();
            		
            		entityManager.remove(author);
        		}
        		
        		if (transaction.isActive()) {
        			transaction.commit();
        		}
                return Optional.of(author);
            } catch (IllegalArgumentException | NullPointerException e) {
        	    if ( transaction != null && transaction.isActive() ) {
        	    	transaction.rollback();
        	    }
//        	    LOGGER.warn("[ failed to make transaction ]", e);
        	    LOGGER.warn("[ failed to make transaction ]");
            }
    	}
		LOGGER.info("[ nothing was deleted with such argument (BaseEntity entity) ]");
    	return Optional.empty();
	}
/**
 * Creates Transaction to commit entity changes, specifies lock.
 * Copies fields values from dest entity to src. 
 * / currently just modifies time stamp of author books /
 * If catches IllegalArgumentException | NullPointerException makes 'rollback'.
 * @param src entity that will be committed.
 * @param dest entity with values to copy.
 * @return removed entity on success or empty.
 */	
	@Override
	public Optional<BaseEntity> update(BaseEntity src, BaseEntity dest) {
		Authors author = (Authors) src;
		EntityTransaction transaction = entityManager.getTransaction();
		if (author != null) {
			try {
				transaction.begin();

				entityManager.lock(author, this.lockModeType);
				author.getBooks().forEach(book -> {
					book.setReleaseDate(new Date());
				});

				if (transaction.isActive()) {
					transaction.commit();
				}
				return Optional.of(author);
			} catch (RollbackException | IllegalArgumentException | NullPointerException e) {
				if (transaction != null && transaction.isActive()) {
					transaction.rollback();
				}
//	    	    LOGGER.warn("[ failed to make transaction ]", e);
	    	    LOGGER.warn("[ failed to make transaction ]");
			}
		}
		LOGGER.info("[ nothing was updated with such argument (BaseEntity src) ]");
		return Optional.empty();
	}
}
