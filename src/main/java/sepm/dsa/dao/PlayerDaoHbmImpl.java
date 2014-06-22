package sepm.dsa.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Deal;
import sepm.dsa.model.Player;

@Repository
@Transactional
public class PlayerDaoHbmImpl
        extends BaseDaoHbmImpl<Player>
        implements PlayerDao {

    @Override
    public void remove(Player model) {
        super.remove(model);
        for(Deal deal : model.getDeals()) {
            deal.setPlayer(null);
        }
    }
}
