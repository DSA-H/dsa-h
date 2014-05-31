package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.BaseModel;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Transactional
abstract class BaseDaoHbmImpl<Model extends BaseModel> implements BaseDao<Model> {
	protected SessionFactory sessionFactory;
	protected static Logger log;

	private Class<Model> modelClass;

	public BaseDaoHbmImpl() {
		modelClass = getEntityClass();
		log = LoggerFactory.getLogger(modelClass);
	}

	// ------------- Helper methods --------------------------------

	/**
	 * Computes the Class of the type parameter Model.
	 *
	 * @return Model.class
	 */
	@SuppressWarnings("unchecked")
	protected Class<Model> getEntityClass() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		return (Class<Model>) pt.getActualTypeArguments()[0];
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		log.debug("calling setSessionFactory(" + sessionFactory + ")");
		this.sessionFactory = sessionFactory;
	}

	// ------------- Generic DAO methods ---------------------------

	@Transactional(readOnly = false)
	public Model add(Model model) {
		log.debug("calling add(" + model + ")");
		sessionFactory.getCurrentSession().save(model);
		return model;
	}

	@Transactional(readOnly = false)
	@SuppressWarnings("unchecked")
	public Model update(Model model) {
		log.debug("calling update(" + model + ")");
		return (Model) sessionFactory.getCurrentSession().merge(model);
	}

	@Transactional(readOnly = false)
	public void remove(Model model) {
		log.debug("calling remove(" + model + ")");
		sessionFactory.getCurrentSession().delete(model);
	}

	@SuppressWarnings("unchecked")
	public Model get(Serializable id) {
		log.debug("calling get(" + id + ")");
		Model result = (Model) sessionFactory.getCurrentSession().get(modelClass, id);
		log.trace("returning " + result);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Model> getAll() {
		log.debug("calling getAll()");
		List<Model> result = sessionFactory.getCurrentSession().createQuery("FROM " + modelClass.getName()).list();
		log.trace("returning " + result);
		return result;
	}
}
