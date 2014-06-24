package sepm.dsa.service;

import sepm.dsa.model.BaseModel;

import java.util.Collection;

public interface SaveCancelService {

    /**
     * Save the current application data state
     */
    void save();

    /**
     * After calling cancel, changed objects may need to be refreshed using the
     * refresh(Object...) method
     */
    void cancel();

    /**
     * Closes the Session, just use this if you understand the impact!
     */
    void closeSession();

    /**
     * Synchronizes the objects with the database
     * @param objects entity objects, not null
     */
    void refresh(BaseModel... objects);

    /**
     * Synchronizes the objects with the database
     * @param objects entity objects, not null
     */
    void refresh(Collection<? extends BaseModel> objects);

}
