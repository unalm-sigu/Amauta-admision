package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;

public interface MatriculaResumenDAO extends EasyDAO<MatriculaResumen> {

    MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<MatriculaResumen> allByCiclo(CicloAcademico ciclo);

    MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaEnum estadoMatriculaCursoEnum);

    List<MatriculaResumen> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    AlumnoResumen findResumenByCicloRolDynateable(CicloAcademico ciclo, String codigo, List<Long> filtros);

    void updatePuntajePrioridad(MatriculaResumen matriculaResumen);

    public List<MatriculaResumen> allNoMatriculadoByCiclo(CicloAcademico cicloAcademico);

    void updatePrioridad(MatriculaResumen matriculaResumen);

    void updateTurnoAtencion(CicloAcademico cicloAcademico, TurnoAtencion turnoAtencion);
}
