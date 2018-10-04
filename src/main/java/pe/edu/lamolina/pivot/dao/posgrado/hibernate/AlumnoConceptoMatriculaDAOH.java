package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoConceptoMatricula;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoConceptoMatriculaDAO;

@Repository
public class AlumnoConceptoMatriculaDAOH extends AbstractEasyDAO<AlumnoConceptoMatricula> implements AlumnoConceptoMatriculaDAO {

    public AlumnoConceptoMatriculaDAOH() {
        super();
        setClazz(AlumnoConceptoMatricula.class);
    }

}
