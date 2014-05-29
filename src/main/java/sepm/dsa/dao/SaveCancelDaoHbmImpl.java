package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveCancelDaoHbmImpl implements SaveCancelDao {

    private static final Logger log = LoggerFactory.getLogger(SaveCancelDaoHbmImpl.class);
    private SessionFactory sessionFactory;

    @Override
    public void save() {
        log.info("calling save()");
    }

    @Override
    public void cancel() {
        log.info("calling cancel()");
    }

    @Override
    public void closeSession() {
        log.info("calling closeSession()");
    }

    @Override
    public void refresh(Object... objects) {
        log.info("calling refresh(" + objects + ")");
        for (Object o : objects) {
            sessionFactory.getCurrentSession().refresh(o);
        }
    }

//    @Override
//    public void refresh(Collection<?> objects) {
//        log.info("calling refresh(" + objects + ")");
//        for (Object o : objects) {
//            sessionFactory.getCurrentSession().refresh(o);
//        }
//    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
