package sepm.dsa.dao;

import sepm.dsa.model.BaseModel;

import java.util.Collection;

public interface SaveCancelDao {

    /**
     * Makes all changes in the persistence context persistent.
     */
    void save();

    /**
     * After calling cancel, changed objects need to be refresh using the
     * refresh(Object...) method
     */
    void cancel();

    /**
     * Closes the current HibernateSession
     */
    void closeSession();

    /**
     * Synchronizes the persisted objects in the persistence context to the database state
     *
     * @param objects (not null)
     */
    void refresh(BaseModel... objects);

    /**
     * Synchronizes the persisted objects in the persistence context to the database state
     * @param objects (not null)
     */
    void refresh(Collection<? extends BaseModel> objects);
}
