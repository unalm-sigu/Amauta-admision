package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ProgDataService {

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

    void revisarAlumnoMatriculado(MatriculaResumen aluResumen, Map<String, MatriculaResumen> mapResumenes, Map<String, String> mapBloqueados);

    void revisarSecciones(List<Seccion> secciones, CicloAcademico ciclo);

    void revisarGrupoSecciones(List<GrupoSeccion> gruposSecciones);

    void revisarBloqueados(Map<String, String> mapBloqueados);

    void detenerRevisionBloqueado();

}
