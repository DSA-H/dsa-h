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
        // lol
    }

	@Override
	public List<Tavern> getAllForLocation(Location location) {
		// TODO implement
		return new ArrayList<Tavern>();
	}
}
