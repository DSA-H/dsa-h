package sepm.dsa.dao;

import sepm.dsa.model.BaseModel;

import java.util.Collection;

public interface SaveCancelDao {

    void save();

    /**
     * After calling cancel, changed objects need to be refresh using the
     * refresh(Object...) method
     */
    void cancel();

    void closeSession();

    /**
     * Synchronizes the persisted objects in the persistence context to the database state
     *
     * @param objects
     */
    void refresh(BaseModel... objects);

    void refresh(Collection<? extends BaseModel> objects);
}
