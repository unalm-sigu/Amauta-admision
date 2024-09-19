package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.BecasPronabecDAO;
import pe.edu.lamolina.model.pronabec.BecaPronabec;

@Repository
public class BecasPronabecDAOH extends AbstractEasyDAO<BecaPronabec> implements BecasPronabecDAO {
}
