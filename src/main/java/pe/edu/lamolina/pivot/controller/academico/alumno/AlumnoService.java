package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoService {

    List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, List<Carrera> carreras);

    List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> facultades);

    AlumnoResumen findResumen();

    List<CicloAcademico> allCicloAcademico();

    List<TipoDocIdentidad> allDocumento();

    List<TipoDocIdentidad> allDocumentosPersonaNatural();

    List<SituacionAcademica> allSituaciones();

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    Alumno findAlumnoFisico(Long idAlumno);

    void saveAlumnoFisico(Alumno alumno, Usuario usuario);

    void saveAlumnoEspecial(Alumno alumno, Usuario usuario);

    Alumno validarAlumnoEspecial(Alumno alumnoVisitanteForm);

    void updateAlumnoFisico(Alumno alumno, Usuario usuarioRegistra);

    void updateAlumnoEspecial(Alumno alumno, Usuario usuarioRegistra);

    List<Carrera> allCarrerasByuser(Usuario usuario, Persona persona);

    Parametro findParametroByEnum(ParametrosSistemasEnum parametrosSistemasEnum);

    String goMatricula(Long idAlumno, Usuario usuario);

    List<AlumnoCursoCurricula> allCursosByAlumno(Alumno alumno, DynatableFilter filter);

    List<CursoCicloAcademico> allCursoCiclo(String nombre, CicloAcademico cicloAcademico);

    void saveCursoCurricula(AlumnoCursoCurricula alumnoCursoCurricula, CicloAcademico cicloAcademico);

}
