package pe.edu.lamolina.pivot.controller.academico.alumno;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoService {

    List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, String codigo, List<Long> filtros);

    AlumnoResumen findResumen();

    List<MatriculaCurso> allMatriculaCursoByAlumno(Long idAlumno);

    Alumno findAlumno(Alumno alumno);

    List<AlumnoCicloCurso> findAlumnoHistorial(Alumno alumno);

    List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno);

    List<AlumnoCicloCurso> allPromediosByAlumnoOrderByCurso(Alumno alumno);

//    List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, String codigo, List<Long> filtros);
//
//    AlumnoResumen findResumen();
//
//    List<MatriculaCurso> allMatriculaCursoByAlumno(Long idAlumno);
//
//    Alumno findAlumno(Alumno alumno, CicloAcademico academico);
    List<CicloAcademico> allCicloAcademico();

    List<TipoDocIdentidad> allDocumento();

    List<SituacionAcademica> allSituaciones();

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    Alumno findAlumnoFisico(Long idAlumno);

    List<Carrera> allCarreraByName(String nombre, Compania cia);

    void saveAlumnoFisico(Alumno alumno, Usuario usuario);

    void saveAlumnoEspecial(Alumno alumno, Usuario usuario);

    void updateAlumnoFisico(Alumno alumno, Usuario usuarioRegistra);

    void updateAlumnoEspecial(Alumno alumno, Usuario usuarioRegistra);

    Alumno findAlumno(Long idAlumno);

    String goMatricula(Long idAlumno);

    List<HorarioSeccion> allSeccionHorarioAlumnoByAlumnoCicloACademico(Alumno alumno, CicloAcademico academico);

    ObjectNode findHorarioBySeccionesHorarios(List<HorarioSeccion> seccionesHorarios);

    Hora getHoraByNroHora(Integer numero);

    List<MatriculaCurso> allCursosMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Hora> allHoras();

    Alumno allInfo(Alumno alumno);

}
