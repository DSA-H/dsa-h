package sepm.dsa.dao;

import sepm.dsa.model.Deal;
import sepm.dsa.model.Player;

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
