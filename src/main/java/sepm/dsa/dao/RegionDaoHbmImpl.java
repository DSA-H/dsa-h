package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Region;

@Transactional(readOnly = true)
public class RegionDaoHbmImpl
	extends BaseDaoHbmImpl<Region>
	implements RegionDao {

	@Override
	public void remove(Region region) {
		super.remove(region);

		region.getAllBorders().forEach(rb -> {
			Region r = rb.getRegion1().equals(region) ? rb.getRegion2() : rb.getRegion1();
			r.getBorders1().remove(rb);
			r.getBorders2().remove(rb);
		});

	}
}
