package pe.edu.lamolina.pivot.controller.matricula.matriculable;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MatriculableService {

    List<MatriculaResumen> allAlumnosByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico);

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    void generar(CicloAcademico cicloAcademico, DataSessionPivot ds);

    AlumnoResumen allResumenAlumnosByCicloRol(CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    void generarPrioridad(CicloAcademico ciclo);

    List<ConfiguracionTurnosAtencion> allConfiguracionTurnoByCiclo(CicloAcademico cicloAcademico);

    void procesarTurnoMatricula(CicloAcademico cicloAcademico, Long configuracionTurnoAtencion);

    void loadEgresados(MultipartFile file);

    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    public void eliminarPrioridad(CicloAcademico cicloAcademico);

    public void finalizarPrioridad(CicloAcademico cicloAcademico);

    public void finalizarMatriculable(CicloAcademico cicloAcademico);

    public void limpiarMatriculable(CicloAcademico cicloAcademico);

    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds);

    public void saveMatriculable(Alumno alumno, DataSessionPivot ds);

    public void generarVerano(CicloAcademico cicloAcademico, DataSessionPivot ds);

}
