package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoService {

    List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, String codigo, List<Long> filtros);

    AlumnoResumen findResumen();

    List<MatriculaCurso> allMatriculaCursoByAlumno(Long idAlumno);

    List<CicloAcademico> allCicloAcademico();

    List<TipoDocIdentidad> allDocumento();

    List<SituacionAcademica> allSituaciones();

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    void saveAlumno(Alumno alumno, Usuario usuario);

    Alumno findAlumnoFisico(Long idAlumno);

}
