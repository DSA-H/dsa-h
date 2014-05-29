package sepm.dsa.dao;

public interface SaveCancelDao {

    void save();

    /**
     * After calling cancel, changed objects need to be reset using the
     * reset(Object...) method
     */
    void cancel();

    void closeSession();

    void reset(Object... objects);

}
