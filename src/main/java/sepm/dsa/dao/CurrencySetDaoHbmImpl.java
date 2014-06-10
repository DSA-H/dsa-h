package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.CurrencySet;

@Transactional(readOnly = true)
public class CurrencySetDaoHbmImpl extends BaseDaoHbmImpl<CurrencySet> implements CurrencySetDao {

}
