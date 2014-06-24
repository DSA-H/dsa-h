package sepm.dsa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.dao.SaveCancelDao;
import sepm.dsa.model.BaseModel;

import java.util.Collection;

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
    public void refresh(BaseModel... objects) {
        log.debug("calling save(" + objects + ")");
        saveCancelDao.refresh(objects);
    }

    @Override
    public void refresh(Collection<? extends BaseModel> objects) {
        log.debug("calling refresh(" + objects + ")");
        saveCancelDao.refresh(objects);
    }

    public void setSaveCancelDao(SaveCancelDao saveCancelDao) {
        log.debug("calling setSaveCancelDao(" + saveCancelDao + ")");
        this.saveCancelDao = saveCancelDao;
    }
}
