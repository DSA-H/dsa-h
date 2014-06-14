package sepm.dsa.dao;

import sepm.dsa.model.CurrencySet;
import sepm.dsa.model.Region;

import java.util.List;

public interface RegionDao extends BaseDao<Region> {

    /**
     * @param currencySet the preferred Currency Set (not null)
     * @return all Region with this preferred CurrencySet, might be an empty list (not null)
     */
    List<Region> getAllByPreferredCurrencySet(CurrencySet currencySet);

}
