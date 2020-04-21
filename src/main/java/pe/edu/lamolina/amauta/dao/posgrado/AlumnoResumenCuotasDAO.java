package pe.edu.lamolina.amauta.dao.posgrado;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;

public interface AlumnoResumenCuotasDAO extends EasyDAO<AlumnoResumenCuotas> {

    AlumnoResumenCuotas findByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico);

}
