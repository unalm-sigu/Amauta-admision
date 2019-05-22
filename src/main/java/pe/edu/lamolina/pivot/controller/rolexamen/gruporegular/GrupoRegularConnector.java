package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GrupoRegularConnector {

    void savedLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    void crearLetraGrupoRegularByLetra(
            LetraGrupoRegular letraGrupoRegular,
            List<CursoMasivoExamen> cursosMasivosExamen,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Map<String, List<Seccion>> grupoHorasLetraMap,
            List<Seccion> seccionesEspeciales,
            DataSessionPivot ds);

    boolean procesarSeccionesByLetra(
            LetraGrupoRegular letraGrupoRegular,
            List<CursoMasivoExamen> cursosMasivosExamen,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Seccion seccion,
            List<Seccion> seccionesByLetra,
            DataSessionPivot ds);

    boolean validarCursosMasivos(GrupoHorasExamen grupoHorasExamen, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos);

    boolean validarCursosMasivos(RolExamenes rolExamenes,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos);

    boolean validarCursosMasivos(List<CursoMasivoExamen> cursosMasivosByRolExamen,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnosBySeccion);

    boolean validarGrupoRegular(GrupoHorasExamen grupoHorasExamen,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas);

    boolean validarGrupoRegular(LetraGrupoRegular letraGrupoRegular,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas);

    boolean validarGrupoEspecial(GrupoHorasExamen grupoHorasExamen, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos);

    boolean validarGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas);

    void fillActiveInfoCursosMasivos(List<CursoMasivoExamen> cursosMasivoExamen);

    void fillActiveInfoLetrasGruposRegulares(List<LetraGrupoRegular> letrasGrupoRegular);

    void fillActiveInfoGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial);

    void validarSituacionBeforeOr(String accion, String situacion, Boolean... or);

    void validarSituacion(String accion, String situacion, Boolean... or);

    SeccionGrupoRegular crearObjectSeccionGrupoRegular(Seccion seccion, LetraGrupoRegular letraGrupoRegular, DataSessionPivot ds);

    AlumnoGrupoRegular crearObjectAlumnoGrupoRegular(Alumno alumno, SeccionGrupoRegular seccionGrupoRegular, DataSessionPivot ds);

    RolExamenesLogger validacionActivarDocente(GrupoHorasExamen grupoHorasExamen, Docente docente);

    RolExamenesLogger validacionActivarAlumno(GrupoHorasExamen grupoHorasExamen, Alumno alumno);

    List<Aula> allAulasOeraWithHorarioByRolExamenes(RolExamenes rolExamenes, OficinaEnum oficinaEnum);

    boolean checkDisponibilidadAula(Aula aula, GrupoHorasExamen grupoHorasExamen);

    Map<Long, List<Aula>> aulasAgrupadasPorModulo(Aula aulaSeccionOriginal);

}
