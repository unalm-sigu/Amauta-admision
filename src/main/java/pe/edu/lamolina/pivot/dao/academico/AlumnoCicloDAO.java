package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;

public interface AlumnoCicloDAO extends EasyDAO<AlumnoCiclo> {

    List<AlumnoCiclo> allByCicloAcademicoPlanCurricular(PlanCurricular plan, CicloAcademico ciclo);

    Long countByCicloAcademicoPlanCurricular(CicloAcademico cicloAcademico, PlanCurricular planCurricular);

    AlumnoCiclo findLock(Long id);

    AlumnoCiclo findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allByAlumno(Alumno alumno);

    AlumnoCiclo findLastByAlumno(Alumno alumno);

    AlumnoCiclo findActiveAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allByEstadoAndCicloAsc(Alumno alumno, EstadoMatriculaEnum... estadoMatriculaEnum);

    AlumnoCiclo findAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    AlumnoCiclo findActiveSiguienteByAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allActivesByAlumnoAsc(Alumno alumno);

    List<AlumnoCiclo> allByAlumnoAsc(Alumno alumno);

    List<AlumnoCiclo> allByModalidadEstAndSituacionesAcadAndEstadoMatAsc(
            ModalidadEstudio modalidadEstudio,
            Carrera carrera,
            List<SituacionAcademica> situaciones,
            EstadoMatriculaEnum estadoMatriculaEnum);

    List<AlumnoCiclo> allDataByAlumno(Alumno alumno,
            EstadoMatriculaEnum estadoMatriculaEnum);

    AlumnoCiclo findActiveByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    AlumnoCiclo findByAlumnoCicloEstado(Alumno alumno, CicloAcademico cicloAcademico, List<EstadoMatriculaEnum> estadosEnums);

    Long countCiclosEstudiados(Alumno alumno, CicloAcademico cicloAcademico);

    AlumnoCiclo findInhaSiguienteByAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    AlumnoCiclo findInhaAnteriorByAlumno(Alumno alumno, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allAnterioresEQByCicloAlumno(Alumno alumno, CicloAcademico cicloAcademico, Integer limit);

    void updateSituacionInicioFinal(AlumnoCiclo alumnoCiclo);

    void updateSituacionInicioFinalEstado(AlumnoCiclo alumnoCiclo);

    void updateSituacionFinal(AlumnoCiclo alumnoCiclo);

    AlumnoCiclo findLastActiveRegByAlumno(Alumno alumno);

    List<AlumnoCiclo> allCicloRegularByAlumno(Alumno alum);

    AlumnoCiclo findUltimoCicloRegularByAlumno(Alumno alum, CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allByCicloAcademico(CicloAcademico ciclo);

    List<AlumnoCiclo> allByControlesOrdenMerito(List<ControlOrdenMerito> coms);

    void deleteControlMeritoByCiclo(CicloAcademico cicloAcademico);

    void deleteOrdenMeritoByCiclo(CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allByControlMeritoCiclo(DynatableFilter filter, ControlOrdenMerito controlBD);

    List<AlumnoCiclo> allByControlMeritoCarrera(DynatableFilter filter, ControlOrdenMerito controlBD);

    List<AlumnoCiclo> allByControlMeritoFacultad(DynatableFilter filter, ControlOrdenMerito controlBD);

    List<AlumnoCiclo> allByControlMeritoCicloNivel(DynatableFilter filter, ControlOrdenMerito controlBD, Integer nivel);

    List<AlumnoCiclo> allByControlMeritoCarreraNivel(DynatableFilter filter, ControlOrdenMerito controlBD, Integer nivel);

    List<AlumnoCiclo> allByControlMeritoFacultadNivel(DynatableFilter filter, ControlOrdenMerito controlBD, Integer nivel);

    List<AlumnoCiclo> allActivosRegularesByCicloResumen(CicloAcademico ciclo);

    AlumnoCiclo findActivosRegularesByCicloResumen(CicloAcademico cicloAcademico, Alumno alumno);

    void updateSituacionFinalOnly(AlumnoCiclo alumnoCiclo);

    List<AlumnoCiclo> allByNmatAndInh(List<CicloAcademico> cicloAnt);

    AlumnoCiclo findActivosRegularesByCiclo(CicloAcademico ciclo, Alumno alumno);

    List<AlumnoCiclo> allByAlumnoDescRegular(Alumno alumno);

    AlumnoCiclo findLastNotInSituacion(Alumno alumno, SituacionAcademicaEnum... situacionAcademicaEnums);

    void updateColumns(AlumnoCiclo alumnoCiclo, String... columns);

    AlumnoCiclo findLastByAlumnoAndSituacion(Alumno alumno, SituacionAcademicaEnum... situacionesAcademicas);

    List<AlumnoCiclo> allByAlumnos(List<Alumno> alumnos);

    List<AlumnoCiclo> allWithSituacionByCiclo(CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allWithSituacionErrorByCiclo(CicloAcademico cicloAcademico);

    List<AlumnoCiclo> allWithSituacionByAlumnos(List<Alumno> alumnos);

    List<AlumnoCiclo> allByCicloAcademicos(List<CicloAcademico> ciclos);

    void deleteById(AlumnoCiclo alumnoCiclo);

    public List<AlumnoCiclo> allByAlumnosReg(List<Alumno> alumnos);

    List<AlumnoCiclo> allByCicloOrderMerito(CicloAcademico cicloAcademico);

}
