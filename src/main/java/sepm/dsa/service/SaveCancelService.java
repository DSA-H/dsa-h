package sepm.dsa.service;

import java.util.Collection;

public interface SaveCancelService {

    void save();

    /**
     * After calling cancel, changed objects need to be refresh using the
     * refresh(Object...) method
     */
    void cancel();

    void closeSession();

    void refresh(Object... objects);

}
