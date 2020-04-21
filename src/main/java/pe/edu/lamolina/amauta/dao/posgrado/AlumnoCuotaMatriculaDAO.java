package pe.edu.lamolina.amauta.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoCuotaMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;

public interface AlumnoCuotaMatriculaDAO extends EasyDAO<AlumnoCuotaMatricula> {

    List<AlumnoCuotaMatricula> allAlumnoResumenCuotas(AlumnoResumenCuotas alumnoResumenCuotas);

}
