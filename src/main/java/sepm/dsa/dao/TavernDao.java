package sepm.dsa.dao;

import sepm.dsa.model.Tavern;

import java.util.List;

public interface TavernDao {

    /**
     * Adds the tavern to the database.
     *
     * @param tavern to be persisted must not be null
     */
    void add(Tavern tavern);

    /**
     * Updates the given tavern.
     *
     * @param tavern Tavern to be updated. Must not be null.
     */
    void update(Tavern tavern);

    /**
     * Removes the given tavern from the database.
     *
     * @param tavern Tavern to be removed. Must no be null.
     */
    void remove(Tavern tavern);

    /**
     * Retrieves a tavern from the database.
     *
     * @param id Identifier of the tavern.
     * @return null if no fitting tavern found
     */
    Tavern get(int id);

    /**
     * Retrieves a list of all taverns from the database.
     *
     * @return list of all taverns
     */
    List<Tavern> getAll();
}
