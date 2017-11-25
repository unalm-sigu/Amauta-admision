package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;

@Repository
public class AlumnoHorarioDAOH extends AbstractEasyDAO<AlumnoHorario> implements AlumnoHorarioDAO {

    public AlumnoHorarioDAOH() {
        super();
        setClazz(AlumnoHorario.class);
    }
}

