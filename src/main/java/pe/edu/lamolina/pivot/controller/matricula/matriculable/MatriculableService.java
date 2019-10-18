package pe.edu.lamolina.pivot.controller.matricula.matriculable;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MatriculableService {

    List<MatriculaResumen> allAlumnosByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, List<Carrera> carreras, String todo);

    MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico);

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    void generar(CicloAcademico cicloAcademico, DataSessionPivot ds);

    AlumnoResumen allResumenAlumnosByCicloRol(CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    void revisarSituacionAcademica(Alumno alumno, DataSessionPivot ds);

    void revisarSituacionesAcademicas(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void generarPrioridad(CicloAcademico ciclo);

    List<ConfiguracionTurnosAtencion> allConfiguracionTurnoByCiclo(CicloAcademico cicloAcademico);

    void procesarTurnoMatricula(CicloAcademico cicloAcademico, Long configuracionTurnoAtencion);

    void loadEgresados(MultipartFile file);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    void eliminarPrioridad(CicloAcademico cicloAcademico);

    void finalizarPrioridad(CicloAcademico cicloAcademico);

    void finalizarMatriculable(CicloAcademico cicloAcademico);

    void limpiarMatriculable(CicloAcademico cicloAcademico);

    List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds);

    MatriculaResumen saveMatriculable(Alumno alumno, String tipoCondicional, DataSessionPivot ds);

    void generarAportes(Alumno alumno, MatriculaResumen matriculable, DataSessionPivot ds);

    void generarVerano(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void recalcularPrioridad(GrupoSeccion gpoSecc, CicloAcademico ciclo);

    void inhabilitarMatriculable(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void verificarAlumnosNmat(DataSessionPivot ds, List<AlumnoCiclo> alumnoCiclos);

    void beneficiar(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    List<CicloAcademico> allCiclosActivos();

    List<AlumnoCiclo> allAlumnosCicloNmat(CicloAcademico cicloActivo);

    void quitarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void agregarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void actualizarPrioridadCero(DataSessionPivot ds);

    ResumenAporteAlumno findResumenAporteAlumno(ResumenAporteAlumno resumenAporteAlumno);

    MatriculaResumen findMatriculaResumen(MatriculaResumen matriculaResumen);

    List<DeudaAlumno> allByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<ActoPreBean> allActosPregrado(CicloAcademico cicloAcademico, String tipoReporte);

    void agregarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

    void quitarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds);

}
