package demo.library.model;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

public abstract class BaseRepository implements AutoCloseable {

	private boolean active;

	protected EntityManager entityManager;

	protected LockModeType lockModeType;
	/**
	 * Constructor. Accepts EntityManagerFactory to create EntityManager.
	 * @throws IllegalArgumentException().
	 * @param entityManagerFactory that will create entity manager.
	 */
	public BaseRepository(EntityManagerFactory entityManagerFactory) {
		active = false;
		lockModeType = lockModeType.OPTIMISTIC;
		if (entityManagerFactory.isOpen()) {
			entityManager = entityManagerFactory.createEntityManager();
			if (entityManager.isOpen()) {
				active = true;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public abstract Optional<BaseEntity> save(BaseEntity entity);

	public abstract Optional<BaseEntity> findById(Long id);

	public abstract List<BaseEntity> findAll();

	public abstract List<BaseEntity> findLikeName(String name);

	public abstract Optional<BaseEntity> findByName(String name);

	public abstract Optional<BaseEntity> delete(BaseEntity entity);

	public abstract Optional<BaseEntity> update(BaseEntity src, BaseEntity dest);

	public void setLockModeType(LockModeType lockModeType) {
		this.lockModeType = lockModeType;
	}

	@Override
	public void close() throws Exception {
		if (active) {
			entityManager.close();
		}
	}
}
