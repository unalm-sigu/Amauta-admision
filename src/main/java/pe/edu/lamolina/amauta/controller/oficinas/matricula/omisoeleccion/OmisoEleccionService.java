package pe.edu.lamolina.amauta.controller.oficinas.matricula.omisoeleccion;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface OmisoEleccionService {

    List<Alumno> allDeudaAlumno(DynatableFilter filter);

    void saveOmision(AlumnoOmisoEleccion omisoEleccion, DataSessionPivot ds);

    void anularOmision(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    List<String> cargarDeudas(MultipartFile file, String codigoCiclo, DataSessionPivot ds);

    List<CicloAcademico> allCicloAcademico(CicloAcademico cicloAcademico);

    List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds);

    void modificarAporte(Alumno alumno, DataSessionPivot ds);

    ResumenAporteAlumno findResumenAporteAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    MatriculaResumen findMatriculaResumen(Alumno alumno, CicloAcademico cicloAcademico);

}
