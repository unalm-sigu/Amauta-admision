package pe.edu.lamolina.amauta.dao.vacante;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;

public interface VacanteAlumnoDAO extends EasyDAO<VacanteAlumno> {

    List<VacanteAlumno> allBySecciones(List<Seccion> secciones);

    List<VacanteAlumno> allBySeccion(Seccion seccion);

    List<VacanteAlumno> allActivosBySeccion(Seccion seccion);

    List<VacanteAlumno> allByAlumno(Alumno alumno);

    void deleteAllByCiclo(CicloAcademico cicloAcademico);

    void updateEstadoFechaModUsuarioMod(VacanteAlumno vacanteAlumno);

    List<VacanteAlumno> allActivoBySecciones(List<Seccion> secciones);

    public VacanteAlumno allByAlumnoAndSeccion(Alumno alumno, Seccion seccion);

    public void updateEstado(List<VacanteAlumno> vacantesAlumnoTemp);

}
