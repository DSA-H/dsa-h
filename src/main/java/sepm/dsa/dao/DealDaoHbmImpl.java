package sepm.dsa.dao;

import org.hibernate.Query;
import sepm.dsa.model.*;

import java.util.List;
import java.util.Vector;

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

    @Override
    public List<Deal> playerDealsWithTraderInTimeRange(Player player, Trader trader, long fromDate, long toDate) {
        log.debug("calling playerDealsWithTraderInTimeRange(" + player + "," + trader + "," + fromDate + "," + toDate + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Deal.findAllBetweenPlayerAndTraderInTimeRange");
        query.setParameter("playerId", player == null ? null : player.getId());
        query.setParameter("traderId", trader == null ? null : trader.getId());
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        List<?> list = query.list();

        List<Deal> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Deal) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Deal> getAllByProduct(Product product) {
        log.debug("calling getAllByProduct(" + product + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Deal.findAllByProduct");
        query.setParameter("productId", product == null ? null : product.getId());
        List<?> list = query.list();

        List<Deal> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Deal) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Deal> getAllByPlayer(Player player) {
        log.debug("calling getAllByPlayer(" + player + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Deal.findAllByPlayer");
        query.setParameter("playerId", player == null ? null : player.getId());
        List<?> list = query.list();

        List<Deal> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Deal) o);
        }

        log.trace("returning " + result);
        return result;
    }
}
