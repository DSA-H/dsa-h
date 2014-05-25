package sepm.dsa.service;

import sepm.dsa.model.Tavern;

public class TavernServiceImpl implements TavernService {

    @Override
    public int getPriceForStay(Tavern tavern) {
        // @TODO Really calculate a price
        return 4; // chosen by fair dice roll.
        // guaranteed to be random.
        // http://xkcd.com/221/
    }
}
