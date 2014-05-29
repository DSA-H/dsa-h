package sepm.dsa.service;

import sepm.dsa.model.Player;

import java.util.List;

public interface PlayerService {

    /**
     * Adds the player to the database.
     * @param player to be persisted must not be null
     */
    void add(Player player);

    /**
     * Updates the given player.
     * @param player Player to be updated. Must not be null.
     */
    void update(Player player);

    /**
     * Removes the given player from the database.
     * @param player Player to be removed. Must no be null.
     */
    void remove(Player player);

    /**
     * Retrieves a player from the database.
     * @param id Identifier of the player.
     * @return the requested player or null if it can not be found
     */
    Player get(int id);

    /**
     * Retrieves a list of all players from the database.
     * @return list of all players or empty list if no players exist
     */
    List<Player> getAll();
}
