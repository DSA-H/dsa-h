package sepm.dsa.service;

import sepm.dsa.model.Tavern;

import java.util.List;

public interface TavernService {


	/**
	 * Asks the DAO to persist a {@code Tavern} in the Database
	 *
	 * @param tavern to be persisted must not be null
	 * @return The added tavern model.
	 */
	public Tavern add(Tavern tavern);

	/**
	 * Asks the DAO to update a already existing {@code Tavern} in the database
	 *
	 * @param tavern to update must not be null
	 * @return The updated tavern model.
	 */
	public Tavern update(Tavern tavern);

	/**
	 * Asks the DAO to delete a tavern permanently
	 *
	 * @param tavern to be deleted must not be null
	 */
	public void remove(Tavern tavern);

	/**
	 * Gets a tavern by its id
	 *
	 * @param id
	 * @return the tavern, or null if it does not exist
	 */
	Tavern get(int id);

	/**
	 * Asks the DAO to find all Taverns
	 *
	 * @return the taverns or empty list of no taverns exist (not null)
	 */
	public List<Tavern> getAll();

	/**
	 * Get all taverns for a specified location or empty List if nothing found
	 *
	 * @param locationId
	 * @return the taverns for the location or empty list
	 */
	List<Tavern> getAllByLocation(int locationId);

	/**
	 * Returns a random (Gaussian distribution) numbers of beds that are used for this tavern. Minimum 0, Maximum tavern.getBeds().
	 *
	 * @param tavern The chosen tavern; must not be null
	 * @return
	 */
	int calculateBedsUseage(Tavern tavern);

	/**
	 * Calculates the price for a night's stay in the given tavern in dependence of useage, townsize, quality and a random factor.
	 *
	 * @param tavern The chosen tavern; must not be null
	 * @return The costs of a hypothetical stay.
	 */
	int calculatePrice(Tavern tavern);
}
