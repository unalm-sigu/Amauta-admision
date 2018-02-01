package pe.edu.lamolina.pivot.dao.vacante;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;

public interface VacanteAlumnoDAO extends EasyDAO<VacanteAlumno> {

    public List<VacanteAlumno> allBySeccion(List<Seccion> secciones);

    public List<VacanteAlumno> allByAlumno(Alumno alumno);

    public void deleteAllByCiclo(CicloAcademico cicloAcademico);

}
