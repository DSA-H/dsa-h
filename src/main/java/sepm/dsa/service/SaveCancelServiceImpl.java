package sepm.dsa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.dao.SaveCancelDao;

public class SaveCancelServiceImpl implements SaveCancelService {

    private static final Logger log = LoggerFactory.getLogger(SaveCancelServiceImpl.class);

    private SaveCancelDao saveCancelDao;

    @Override
    public void save() {
        log.debug("calling save()");
        saveCancelDao.save();
    }

    @Override
    public void cancel() {
        log.debug("calling cancel()");
        saveCancelDao.cancel();
    }

    @Override
    public void closeSession() {
        log.debug("calling closeSession()");
        saveCancelDao.closeSession();
    }

    @Override
    public void refresh(Object... objects) {
        log.debug("calling save(" + objects + ")");
        saveCancelDao.refresh(objects);
    }

    public void setSaveCancelDao(SaveCancelDao saveCancelDao) {
        log.debug("calling setSaveCancelDao()");
        this.saveCancelDao = saveCancelDao;
    }
}