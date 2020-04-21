package pe.edu.lamolina.amauta.dao.rrhh.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rrhh.DedicacionDocente;
import pe.edu.lamolina.amauta.dao.rrhh.DedicacionDocenteDAO;

@Repository
public class DedicacionDocenteDAOH extends AbstractEasyDAO<DedicacionDocente> implements DedicacionDocenteDAO {

    public DedicacionDocenteDAOH() {
        super();
        setClazz(DedicacionDocente.class);
    }

}
