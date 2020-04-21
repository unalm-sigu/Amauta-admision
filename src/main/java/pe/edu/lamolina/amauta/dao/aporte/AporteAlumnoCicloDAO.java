package pe.edu.lamolina.amauta.dao.aporte;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;

public interface AporteAlumnoCicloDAO extends EasyDAO<AporteAlumnoCiclo> {

    List<AporteAlumnoCiclo> allByAlumnoCiclo(Alumno alumno, CicloAcademico cicloModalidad);

    List<AporteAlumnoCiclo> allAporteCarnetByCiclo(CicloAcademico cicloAcademico);

    List<AporteAlumnoCiclo> allByDeudasAlumno(List<DeudaAlumno> deudasAlumnos);

    List<AporteAlumnoCiclo> allAporteCarnetByCicloMatriculaResumen(CicloAcademico cicloAcademico, List<MatriculaResumen> matriculaResumens);

    List<AporteAlumnoCiclo> allByResumenAporteAlumno(ResumenAporteAlumno resumen);

    public List<AporteAlumnoCiclo> allAporteDuplicadoCarnetByCicloMatriculaResumen(CicloAcademico cicloAcademico, List<MatriculaResumen> matriculaResumens);

    public  List<AporteAlumnoCiclo> allByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico);

}
