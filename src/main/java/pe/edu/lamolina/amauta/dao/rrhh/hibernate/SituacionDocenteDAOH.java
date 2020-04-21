package pe.edu.lamolina.amauta.dao.rrhh.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rrhh.SituacionDocente;
import pe.edu.lamolina.amauta.dao.rrhh.SituacionDocenteDAO;

@Repository
public class SituacionDocenteDAOH extends AbstractEasyDAO<SituacionDocente> implements SituacionDocenteDAO {

    public SituacionDocenteDAOH() {
        super();
        setClazz(SituacionDocente.class);
    }

}
