package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.UnitType;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class UnitTypeDaoImpl extends BaseDaoHbmImpl<UnitType> implements UnitTypeDao {

    private static final Logger log = LoggerFactory.getLogger(UnitTypeDaoImpl.class);

}
