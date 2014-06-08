package sepm.dsa.service;

import sepm.dsa.model.BaseModel;

import java.util.Collection;

public interface SaveCancelService {

    void save();

    /**
     * After calling cancel, changed objects need to be refresh using the
     * refresh(Object...) method
     */
    void cancel();

    void closeSession();

    void refresh(BaseModel... objects);

    void refresh(Collection<? extends BaseModel> objects);

}
