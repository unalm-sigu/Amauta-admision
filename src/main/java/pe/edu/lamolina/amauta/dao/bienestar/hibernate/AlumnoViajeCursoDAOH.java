package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.bienestar.AlumnoViajeCursoDAO;
import pe.edu.lamolina.model.bienestar.AlumnoViajeCurso;

@Repository
public class AlumnoViajeCursoDAOH extends AbstractEasyDAO<AlumnoViajeCurso> implements AlumnoViajeCursoDAO {

    public AlumnoViajeCursoDAOH() {
        super();
        setClazz(AlumnoViajeCurso.class);
    }
}
