package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.reportes.ExcelData.Bean.ResultadoNotaSeccion;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface NotaAlumnoNivelacionDAO extends EasyDAO<NotaAlumnoNivelacion> {

    List<NotaAlumnoNivelacion> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allByDynatableSeccion(DynatableFilter filter, CursoNivelacion seccion);

    List<NotaAlumnoNivelacion> allBySeccion(CursoNivelacion seccion);

    MatriculablesResumen findResumen(CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allByCiclo(CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allActivosByCiclo(CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allSinCursoByCiclo(CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allConCursoByCiclo(CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allConNotaByAlumno(Alumno alumno);

    List<NotaAlumnoNivelacion> allInscritosByCursoNivelacion(CursoNivelacion seccion);

    List<NotaAlumnoNivelacion> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    List<NotaAlumnoNivelacion> allByAlumnoNivelacion(AlumnoNivelacion alumnoNiv);

    List<NotaAlumnoNivelacion> allByAlumnosNivelacion(List<AlumnoNivelacion> alumnosNiv);

    List<NotaAlumnoNivelacion> allByCursoNivelacion(CursoNivelacion cursoNiv);

    int saveList(List<NotaAlumnoNivelacion> notasAlumnos);

    int updateList(List<NotaAlumnoNivelacion> notasAlumnos, String... columnas);

    List<ResultadoNotaSeccion> allResultadoNotaSeccionByCicloAndSeccion(CicloAcademico cicloAcademico, String seccion);

}
