package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula.SeccionDTO;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.dto.CantidadMatriculadosDTO;

public interface MatriculaSeccionDAO extends EasyDAO<MatriculaSeccion> {

    List<MatriculaSeccion> allMatriculadosBySeccion(Seccion seccion);

    List<MatriculaSeccion> allMatriculadosBySeccion(List<Seccion> seccion, EstadoMatriculaEnum... estados);

    List<MatriculaSeccion> allMatriculadosBySecciones(List<Seccion> secciones);

    List<MatriculaSeccion> allBySeccion(Seccion seccion);

    List<MatriculaSeccion> allByCurso(List<Curso> cursos, CicloAcademico ciclo, String[] orderBy, EstadoMatriculaEnum... estados);

    List<CantidadMatriculadosDTO> cantidadMatriculadosPorCurso(List<Curso> cursos, CicloAcademico ciclo, EstadoMatriculaEnum... estados);

    List<MatriculaSeccion> allMatriculadosByGpoSeccion(GrupoSeccion grupoSeccion);

    List<MatriculaSeccion> allMatriculadosByCiclo(CicloAcademico ciclo);

    List<MatriculaSeccion> allByCiclo(CicloAcademico ciclo);

    List<MatriculaSeccion> allMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<MatriculaSeccion> allMatriculadosByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico);

    List<MatriculaSeccion> allPrematriculadoByMatriculaResumen(List<MatriculaResumen> matriculaResumens);

    List<MatriculaSeccion> allByAlumnoCicloEstados(Alumno alumno, CicloAcademico academico, List<String> asList);

    List<MatriculaSeccion> allMatriculadosByAlumnosSecciones(List<Alumno> alumnos, List<Seccion> secciones);

    List<MatriculaSeccion> allActivesByMatriculaResumen(List<MatriculaResumen> matriculaResumen);

    List<MatriculaSeccion> allByMatriculaResumen(MatriculaResumen matResumen);

    MatriculaSeccion findByMatriculaMatSeccion(MatriculaResumen matriculaResumen, Seccion seccion);

    List<MatriculaSeccion> alldByMatriculaAndSeccion(MatriculaResumen matriculaResumen, Seccion seccion);

    List<MatriculaSeccion> allByMatriculaMatSeccion(List<MatriculaResumen> matriculasResumen, Seccion seccion);

    List<MatriculaSeccion> allByCicloAcademico(CicloAcademico ciclo);

    void updateEstado(List<MatriculaSeccion> matriculaSeccionMatTemp, EstadoMatriculaEnum eme);

    void updateColumns(MatriculaSeccion matriculaSeccion, String... columns);

    MatriculaSeccion findByMatResumenAndTipoSecAndEstado(MatriculaResumen matriculaResumen, TipoSeccionEnum tipoSeccionEnum, EstadoMatriculaEnum... estadoMatriculaEnum);

    MatriculaSeccion findByMatResumenAndTipoSecAndNoEstado(MatriculaResumen matriculaResumen, TipoSeccionEnum tipoSeccionEnum, EstadoMatriculaEnum... estadoMatriculaEnum);

    MatriculaSeccion findByMatriculaMatSeccion(MatriculaResumen matriculaResumen, Seccion seccion, EstadoMatriculaEnum... estado);

    MatriculaSeccion findByMatriculaMatSeccionAndNoEstado(MatriculaResumen matriculaResumen, Seccion seccion, EstadoMatriculaEnum... estado);

    void deleteAllByCiclo(CicloAcademico ciclo);

    public List<MatriculaSeccion> allByMatriculaResumenes(List<MatriculaResumen> asList);

    List<MatriculaSeccion> allByGrupoSeccion(List<GrupoSeccion> gruposSeccion, EstadoMatriculaEnum... estadosMatriculaSeccion);

    List<MatriculaSeccion> allMatriculadosBySeccion(String seccion, CicloAcademico cicloAcademico);

    public MatriculaSeccion findByAlumnoSeccion(String codigo, String seccion);

    List<MatriculaSeccion> allMatriculadosByMatriculaSeccion(List<MatriculaResumen> matriculasResumen, EstadoMatriculaEnum... estados);

    List<MatriculaSeccion> allBySeccionLite(Seccion seccion);

    List<MatriculaSeccion> allBySecciones(List<Seccion> secciones);

    List<MatriculaSeccion> matriculadosPorSeccion(SeccionDTO seccionDTO);

    List<CantidadMatriculadosDTO> cantidadMatriculados(SeccionDTO seccionDTO);

    public List<MatriculaSeccion> allBySeccionesMat(List<Seccion> secciones);
}
