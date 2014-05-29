package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Tavern;

@Transactional
public class TavernDaoImpl
	extends BaseDaoHbmImpl<Tavern>
	implements TavernDao {
}
