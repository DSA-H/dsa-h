package sepm.dsa.dao;

import sepm.dsa.model.BaseModel;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<Model extends BaseModel> {

	/**
	 * Adds the given model to the persistence context and the database.
	 *
	 * @param model The model to be added.
	 * @return The persisted model.
	 */
	Model add(Model model);

	/**
	 * Merges the given model into the persistence context and updated its database entry.
	 *
	 * @param model The model to be updated.
	 * @return The persisted model.
	 */
	Model update(Model model);

	/**
	 * Deletes the given model from the persistence context and from the database.
	 *
	 * @param model THe model to be deleted.
	 */
	void remove(Model model);

	/**
	 * Retrieves the model with the given identifier.
	 *
	 * @param id Identifier of the model to be retrieved.

	 * @return If found, the model instance; Otherwise null.
	 */
	Model get(Serializable id);

	/**
	 * Retrieves all models of this dao's type.
	 *
	 * @return List containing all models.
	 */
	List<Model> getAll();
}
