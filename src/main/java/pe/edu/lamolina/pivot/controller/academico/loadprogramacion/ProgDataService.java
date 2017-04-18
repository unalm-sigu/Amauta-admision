package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ProgDataService {

    String extraerEmailCompania(Persona persona, DataSessionPivot ds);

    Persona extraerDocumentoIdentidad(Persona persona, DataSessionPivot ds);

    void changeDocumentoIdentidad(Persona persona, TipoDocIdentidad tipoDocumento, String numeroDocIdentidad, String emailCompania, DataSessionPivot ds);

    Persona savePersona(Persona persona, Map<String, TipoDocIdentidad> mapTiposDoc, DataSessionPivot ds);

    void saveAlumno(Alumno alumno, DataSessionPivot ds);

    Persona revisarPersona(Persona persona, DataSessionPivot ds);

    Map<String, GrupoSeccion> loadDataGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo);

    Map<String, Seccion> loadDataSecciones(List<Seccion> secciones, CicloAcademico ciclo, Map<String, GrupoSeccion> mapGpoSecciones);

    Map<String, Docente> loadDataDocentes(List<Docente> docentes, CicloAcademico ciclo);

    Map<String, DocenteSeccion> loadDataDocentesSecciones(
            List<DocenteSeccion> docentesSecciones,
            Map<String, Seccion> mapSecciones,
            Map<String, Docente> mapDocentes);

    void revisarDocenteSecciones(Map<String, DocenteSeccion> mapDocenteSecciones, CicloAcademico ciclo, DataSessionPivot ds);

    void loadDataMatriculados(
            MatriculaSeccion matriSecc,
            Map<String, MatriculaResumen> mapResumenes,
            Map<String, Seccion> mapSecciones,
            CicloAcademico ciclo, DataSessionPivot ds);

    void revisarAlumnoMatriculado(MatriculaResumen aluResumen, Map<String, MatriculaResumen> mapResumenes, Map<String, AlumnoBlocked> mapBloqueados);

    void revisarSecciones(List<Seccion> secciones, CicloAcademico ciclo);

    void revisarGrupoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo);

    void revisarBloqueados(Map<String, AlumnoBlocked> mapBloqueados);

    void detenerRevisionBloqueado();

}
