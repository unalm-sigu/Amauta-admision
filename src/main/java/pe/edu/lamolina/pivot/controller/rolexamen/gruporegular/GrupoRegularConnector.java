package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.seguridad.Usuario;
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

    boolean validarCursosMasivos(RolExamenes rolExamenes, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos, GrupoHorasExamen grupoHorasExamen);

    boolean validarCursosMasivos(RolExamenes rolExamenes,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos);

    boolean validarCursosMasivos(List<CursoMasivoExamen> cursosMasivosByRolExamen,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnosBySeccion);

    boolean validarGrupoRegular(GrupoHorasExamen grupoHorasExamen,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas);

    boolean validarGrupoRegular(LetraGrupoRegular letraGrupoRegular,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas);

    boolean validarGrupoEspecial(RolExamenes rolExamenes, GrupoHorasExamen grupoHorasExamen, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos);

    boolean validarGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas);

    void fillActiveInfoCursosMasivos(List<CursoMasivoExamen> cursosMasivoExamen);

    void fillActiveInfoLetrasGruposRegulares(List<LetraGrupoRegular> letrasGrupoRegular);

    void fillActiveInfoGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial);

    void validarSituacionBeforeOr(String accion, String situacion, Boolean... or);

    void validarSituacion(String accion, String situacion, Boolean... or);

}
