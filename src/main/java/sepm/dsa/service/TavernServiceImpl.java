package sepm.dsa.service;

import sepm.dsa.model.Location;
import sepm.dsa.model.Tavern;

import java.util.ArrayList;
import java.util.List;

public class TavernServiceImpl implements TavernService {

    @Override
    public int getPriceForStay(Tavern tavern) {
        // @TODO Really calculate a price
        return 4; // chosen by fair dice roll.
        // guaranteed to be random.
        // http://xkcd.com/221/
    }

	@Override
	public List<Tavern> getAllForLocation(Location location) {
		// TODO implement
		Tavern t = new Tavern();
		t.setName("TestTavern");
		t.setLocation(location);
		t.setxPos(100);
		t.setyPos(100);
		t.setId(1);
		t.setUsage(0);
		List<Tavern> list = new ArrayList<Tavern>();
		list.add(t);
		return list;
	}

	@Override
	public void remove(Tavern tavern) {

	}

	@Override
	public void add(Tavern tavern) {

	}

	@Override
	public void update(Tavern tavern) {

	}
}
