package sepm.dsa.dao;

public interface SaveCancelDao {

    void save();

    /**
     * After calling cancel, changed objects need to be refresh using the
     * refresh(Object...) method
     */
    void cancel();

    void closeSession();

    void refresh(Object... objects);

}
