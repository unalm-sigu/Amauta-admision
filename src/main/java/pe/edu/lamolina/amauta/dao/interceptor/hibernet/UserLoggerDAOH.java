package pe.edu.lamolina.amauta.dao.interceptor.hibernet;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.interceptor.UserLogger;
import pe.edu.lamolina.amauta.dao.interceptor.UserLoggerDAO;

@Repository
public class UserLoggerDAOH extends AbstractEasyDAO<UserLogger> implements UserLoggerDAO {

}
