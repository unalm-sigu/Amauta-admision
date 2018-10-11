package pe.edu.lamolina.pivot.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoConceptoMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;

public interface AlumnoConceptoMatriculaDAO extends EasyDAO<AlumnoConceptoMatricula> {

    List<AlumnoConceptoMatricula> allAlumnoResumenCuotas(AlumnoResumenCuotas alumnoResumenCuotas);

}
