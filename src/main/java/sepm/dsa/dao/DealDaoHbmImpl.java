package sepm.dsa.dao;

import sepm.dsa.model.Deal;

public class DealDaoHbmImpl extends BaseDaoHbmImpl<Deal>
        implements DealDao {

    @Override
    public Deal add(Deal model) {
        Deal result = super.add(model);
        result.getTrader().getDeals().add(result);
        result.getPlayer().getDeals().add(result);
        return result;
    }

    @Override
    public void remove(Deal model) {
        super.remove(model);
        model.getTrader().getDeals().remove(model);
        model.getPlayer().getDeals().remove(model);
    }
}
