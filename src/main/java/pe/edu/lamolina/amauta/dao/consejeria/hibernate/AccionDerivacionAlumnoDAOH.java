package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AccionDerivacionAlumnoDAO;
import pe.edu.lamolina.model.tutoria.AccionDerivacionAlumno;

@Repository
public class AccionDerivacionAlumnoDAOH extends AbstractEasyDAO<AccionDerivacionAlumno> implements AccionDerivacionAlumnoDAO {

    public AccionDerivacionAlumnoDAOH() {
        super();
        setClazz(AccionDerivacionAlumno.class);
    }

}
