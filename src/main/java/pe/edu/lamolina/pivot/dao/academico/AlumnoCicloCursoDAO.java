package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;

public interface AlumnoCicloCursoDAO extends EasyDAO<AlumnoCicloCurso> {

    AlumnoCicloCurso findByAlumnoCicloCurso(Alumno alumno, CicloAcademico cicloAcademico, Curso curso);

    List<AlumnoCicloCurso> findHistorial(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allOperativesByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumnoOrderByCurso(Alumno alumno);

    List<AlumnoCicloCurso> allOperativesByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCicloCurso> allActivoByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allAprobadoActivoByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allDesaprobadoActivoByAlumno(Alumno alumno);

    Long countByCursoAlumno(Curso curso, Alumno alumno);

    List<AlumnoCicloCurso> allOperativesByAlumnoAnterioresCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCicloCurso> allOperativesByAlumnoCicloLessOrEqual(Alumno alumno, CicloAcademico cicloAcademico);

    Long countByCursoAlumnoAnterioresCiclo(Curso curso, Alumno alumno, CicloAcademico cicloAcademico);

    AlumnoCicloCurso findByAlumnoCicloCursoEstados(Alumno alumno, CicloAcademico cicloAcademico, Curso curso, List<EstadoMatriculaEnum> estados);

    List<AlumnoCicloCurso> allActivoByAlumnoCiclo(AlumnoCiclo alumnoCiclo);

    void deleteByAlumnoCiclo(AlumnoCiclo alumnoCiclo);

    AlumnoCicloCurso find(AlumnoCicloCurso alumnoCicloCursoForm);

    List<AlumnoCicloCurso> allByAlumnoCiclo(AlumnoCiclo alumnoCiclo);

    List<AlumnoCicloCurso> allByAlumnoCicloAsc(Alumno alumno);

    List<AlumnoCicloCurso> allByAlumnoAndAlumnoCiclo(Alumno alumno, AlumnoCiclo alumnoCiclo);

    List<AlumnoCicloCurso> allByAlumnoCicloActivosOrAutorizacionRegistro(AlumnoCiclo alumnoCiclo, AutorizacionRegistro autorizacionRegistro);

    List<AlumnoCicloCurso> allByAlumnoCicloActivosAndAutorizacionRegistro(AlumnoCiclo alumnoCiclo, AutorizacionRegistro autorizacionRegistro);

    List<AlumnoCicloCurso> allByAutorizacionRegistro(AutorizacionRegistro autorizacionRegistro);

    void updateEstadoRegistroActivo(AlumnoCicloCurso alumnoCicloCurso);
}
