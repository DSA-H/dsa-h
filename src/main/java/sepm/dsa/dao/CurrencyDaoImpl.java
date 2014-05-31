package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Currency;

@Transactional(readOnly = true)
public class CurrencyDaoImpl
	extends BaseDaoHbmImpl<Currency>
	implements CurrencyDao {
}
