package sepm.dsa.service;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SaveCancelServiceImpl implements SaveCancelService {

    private static final Logger log = LoggerFactory.getLogger(SaveCancelServiceImpl.class);
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
    public void reset(Object... objects) {
        log.info("calling reset(" + objects + ")");
        for (Object o : objects) {
            sessionFactory.getCurrentSession().refresh(o);
        }
    }

//    @Override
//    public void reset(Collection<?> objects) {
//        log.info("calling reset(" + objects + ")");
//        for (Object o : objects) {
//            sessionFactory.getCurrentSession().refresh(o);
//        }
//    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
