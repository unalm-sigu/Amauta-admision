package pe.edu.lamolina.pivot.controller.programacionhorarios.loadprogramacion;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ProgDataService {

    String extraerEmailCompania(
            Persona persona,
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds);

    Persona extraerDocumentoIdentidad(
            Persona persona,
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds);

    void changeDocumentoIdentidad(
            Persona persona,
            List<Persona> personasVinculadas,
            TipoDocIdentidad tipoDocumento,
            String numeroDocIdentidad,
            String emailCompania,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds);

    Persona savePersona(
            Persona persona,
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds);

    void saveAlumno(
            Alumno alumno,
            Map<Long, Persona> mapIdPersonas,
            Map<String, Alumno> mapAlumnos,
            Map<String, SituacionAcademica> mapSituaciones,
            Map<String, Carrera> mapCarreras,
            Map<String, CicloAcademico> mapCiclo, DataSessionPivot ds);

    Docente saveDocente(Docente docente, ModalidadEstudio modalidad, Map<String, DepartamentoAcademico> mapDptos, DataSessionPivot ds);

    void anularDocentes(Map<String, Docente> mapDocentes, ModalidadEstudio modalidad, DataSessionPivot ds);

    Persona revisarPersona(
            Persona persona,
            List<Persona> personasVinculadas,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds);

    void revisionPreviaGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo);

    Map<String, GrupoSeccion> loadDataGpoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo, DataSessionPivot ds);

    Map<String, Seccion> loadDataSecciones(List<Seccion> secciones, CicloAcademico ciclo, Map<String, GrupoSeccion> mapGpoSecciones, DataSessionPivot ds);

    Map<String, DocenteSeccion> loadDataDocentesSecciones(
            List<DocenteSeccion> docentesSecciones,
            Map<String, Seccion> mapSecciones,
            Map<String, Docente> mapDocentes,
            CicloAcademico ciclo);

    void revisarDocenteSecciones(Map<String, DocenteSeccion> mapDocenteSecciones, CicloAcademico ciclo, DataSessionPivot ds);

    void loadDataMatriculados(
            MatriculaSeccion matriSecc,
            Map<String, MatriculaResumen> mapResumenes,
            Map<String, Seccion> mapSecciones,
            CicloAcademico ciclo, DataSessionPivot ds);

    void revisarAlumnoMatriculado(MatriculaResumen aluResumen);

    void revisarSecciones(List<Seccion> secciones, CicloAcademico ciclo);

    void revisarGrupoSecciones(List<GrupoSeccion> gruposSecciones, CicloAcademico ciclo);

    void revisarBloqueados(Map<String, AlumnoBlocked> mapBloqueados);

//    void revisarHorarioSecciones(List<HorarioSeccion> horariosSeccion, CicloAcademico ciclo);
    void revisarHorarioGrupos(List<DiaHoraGrupo> horariosGrupo, CicloAcademico ciclo);

    void detenerRevisionBloqueado();

    void deleteHorarioSeccionNoUsados(List<HorarioSeccion> horarios, CicloAcademico cicloAcademico);

    List<Persona> allPersonasByPer(
            Persona persona,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas,
            DataSessionPivot ds);

    void codigo2NullGpoSeccion(CicloAcademico ciclo);

    void codigo2NullSeccion(CicloAcademico ciclo);

    void crearCursos(String rutaFileCursos, Map<String, Curso> mapCursos, Map<String, DepartamentoAcademico> mapDepartamentosAcademicos, DataSessionPivot ds);

    List<HorarioSeccion> crearHorarioSecciones(String rutaFileHorarioSecciones, Map<String, Seccion> mapSecciones, Map<Integer, Dia> mapDias, Map<Integer, Hora> mapHoras, Map<String, Aula> mapAulas, CicloAcademico ciclo);

    List<DiaHoraGrupo> crearHorarioGrupos(String rutaFileHorarioGrupos, Map<Integer, Dia> mapDias, Map<Integer, Hora> mapHoras, Map<String, GrupoHoras> mapGrupos, CicloAcademico ciclo);

    void actualizarCiclo(List<CicloAcademico> ciclos);

}
