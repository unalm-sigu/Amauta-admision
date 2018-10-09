package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoConceptoMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoConceptoMatriculaDAO;

@Repository
public class AlumnoConceptoMatriculaDAOH extends AbstractEasyDAO<AlumnoConceptoMatricula> implements AlumnoConceptoMatriculaDAO {

    public AlumnoConceptoMatriculaDAOH() {
        super();
        setClazz(AlumnoConceptoMatricula.class);
    }

    @Override
    public List<AlumnoConceptoMatricula> allAlumnoResumenCuotas(AlumnoResumenCuotas alumnoResumenCuotas) {
        Octavia sql = Octavia.query()
                .from(AlumnoConceptoMatricula.class, "acm")
                .join("alumnoResumenCuotas arc")
                .filter("arc.id", alumnoResumenCuotas);
        return all(sql);
    }

}
