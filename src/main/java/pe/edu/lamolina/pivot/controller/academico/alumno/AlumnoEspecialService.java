package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoEspecialService {

    List<TipoDocIdentidad> allDocumentos();

    List<CicloAcademico> allCiclos();

    List<SituacionAcademica> allSituaciones();

    void saveAlumno(Alumno alumno, Usuario usuario);

}
