package pe.edu.lamolina.pivot.dao.vacante;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;

public interface VacanteAlumnoDAO extends EasyDAO<VacanteAlumno> {

    public List<VacanteAlumno> allBySeccion(List<Seccion> secciones);

}
