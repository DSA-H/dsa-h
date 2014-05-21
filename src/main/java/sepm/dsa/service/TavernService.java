package sepm.dsa.service;

import sepm.dsa.model.Tavern;

public interface TavernService {

	/**
	 * Calculates the price for a night's stay in the given tavern.
	 * @param tavern The chosen tavern.
	 * @return The costs of a hypothetical stay.
	 */
	int getPriceForStay(Tavern tavern);
}
