package sepm.dsa.service;

import java.util.Collection;

public interface SaveCancelService {

    void save();

    /**
     * After calling cancel, changed objects need to be reset using the
     * reset(Object...) method
     */
    void cancel();

    void reset(Object... objects);

}
