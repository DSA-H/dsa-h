package sepm.dsa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSAModelNotFoundException;
import sepm.dsa.model.Player;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class PlayerDaoImpl
        extends BaseDaoHbmImpl<Player>
        implements PlayerDao {

}
