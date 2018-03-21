package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuesta.EncuestaDocente;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;

@Repository
public class EncuestaDocenteDAOH extends AbstractEasyDAO<EncuestaDocente> implements EncuestaDocenteDAO {

    public EncuestaDocenteDAOH() {
        super();
        setClazz(EncuestaDocente.class);
    }

}
