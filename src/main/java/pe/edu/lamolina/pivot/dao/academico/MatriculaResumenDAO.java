package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;

public interface MatriculaResumenDAO extends EasyDAO<MatriculaResumen> {

    MatriculaResumen findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<MatriculaResumen> allByCiclo(CicloAcademico ciclo);

    MatriculaResumen findByFilter(CicloAcademico ciclo, Alumno alumno, EstadoMatriculaEnum estadoMatriculaCursoEnum);

    List<MatriculaResumen> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    MatriculaResumen findMatriculadoByAlumno(CicloAcademico cicloAcademico, Alumno alumno);

    AlumnoResumen findResumenByCicloRolDynateable(CicloAcademico ciclo, String codigo, List<Long> filtros);

    void updatePuntajePrioridad(MatriculaResumen matriculaResumen);

    List<MatriculaResumen> allNoMatriculadoByCiclo(CicloAcademico cicloAcademico);

    void updatePrioridad(MatriculaResumen matriculaResumen);

    void updateTurnoAtencion(CicloAcademico cicloAcademico, TurnoAtencion turnoAtencion);

    List<MatriculaResumen> allMatriculaResumenByAlumno(Alumno alumno);

    List<MatriculaResumen> allByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    List<MatriculaResumen> findNotasIncompletas(List<Alumno> alumnos, CicloAcademico cicloAcademico);

    void updateList(List<Long> matriculables);

    void deleteMatriculable(CicloAcademico cicloAcademico);

    void saveMatriculables(List<Long> alumnos, CicloAcademico cicloAcademico);

    MatriculaResumen findByAntPrioridad(MatriculaResumen matri, CicloAcademico cicloAcademico, Boolean esUltimoCiclo);

    MatriculaResumen findByDesPrioridad(MatriculaResumen matri, CicloAcademico cicloAcademico, Boolean esUltimoCiclo);

    void savePosGradoVerano(List<String> situacionesPregrado, CicloAcademico cicloAcademicoAnterior, CicloAcademico cicloAcademico);

    void savePreGradoVerano(List<String> situacionesPregrado, CicloAcademico cicloAcademicoAnterior, CicloAcademico cicloAcademico);

    List<MatriculaResumen> allByCicloFull(CicloAcademico ciclo);

    Long countMatriculablesByConsejero(Persona persona, CicloAcademico cicloAcademico);

    Long countNoMatriculablesByConsejero(Persona persona, CicloAcademico cicloAcademico);

    Long countRetiroCicloByConsejero(Persona persona, CicloAcademico cicloAcademico);

    List<MatriculaResumen> allByCicloMATAndNMAT(CicloAcademico cicloBD);

    List<MatriculaResumen> allByCicloMat(CicloAcademico ciclo);

    void updateCreditos(MatriculaResumen matri);

    void updateColumns(MatriculaResumen matriculaResumenUpd, String... columns);

    void updateBeneficiado(MatriculaResumen matriculaResumen);

    List<MatriculaResumen> allSinConsejeria(Carrera carrera, CicloAcademico ciclo);

    List<MatriculaResumen> allByCiclos(List<CicloAcademico> ciclos);

    public List<MatriculaResumen> allByAlumnoCiclos(Alumno alumno, List<CicloAcademico> ciclos);

    public List<MatriculaResumen> allUltimosCiclosMatriculables(CicloAcademico academico);
    
    MatriculaResumen findByDesPrioridadTemp(MatriculaResumen matri, CicloAcademico cicloAcademico, Boolean esUltimoCiclo);
    
    MatriculaResumen findByAntPrioridadTemp(MatriculaResumen matri, CicloAcademico cicloAcademico, Boolean esUltimoCiclo);

    public MatriculaResumen findByCodigoCiclo(String codigo, CicloAcademico cicloAcademico);

    public MatriculaResumen findFull(MatriculaResumen matriculaResumen);

}
