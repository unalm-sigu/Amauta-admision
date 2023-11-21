package pe.edu.lamolina.amauta.controller.academico.alumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.CursoConvalidado;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Docente;

public interface AlumnoService {

    void deshabilitarAlumnoCursoCurricula(AlumnoCursoCurricula alumnoCursoCu, DataSessionPivot ds);

    List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, List<Carrera> carreras);

    List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> facultades, String todo);

    AlumnoResumen findResumen(VerificadorServiceImp.CantidadItemsEnum cantidadEnum, List<Carrera> carreras);

    List<CicloAcademico> allCicloAcademico();

    List<TipoDocIdentidad> allDocumento();

    List<TipoDocIdentidad> allDocumentosPersonaNatural();

    List<SituacionAcademica> allSituaciones();

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    Alumno findAlumnoFisico(Long idAlumno);

    void saveAlumnoFisico(Alumno alumno, DataSessionPivot ds);

    void saveAlumnoEspecial(Alumno alumno, DataSessionPivot ds);

    Alumno validarAlumnoEspecial(Alumno alumnoVisitanteForm);

    void updateAlumnoFisico(Alumno alumno, DataSessionPivot ds);

    void updateAlumnoEspecial(Alumno alumno, DataSessionPivot ds);

    List<Carrera> allCarrerasByuser(Persona persona, DataSessionPivot ds);

    List<AlumnoCursoCurricula> allCursosByAlumno(Alumno alumno, DynatableFilter filter);

    List<CursoCicloAcademico> allCursoCiclo(String nombre, CicloAcademico cicloAcademico);

    void saveCursoCurricula(AlumnoCursoCurricula alumnoCursoCurricula, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<AlumnoCursoCurricula> allAlumnoCursoCurso(Alumno alumno);

    List<TramiteTraslado> allTramiteTrasladoByAlumno(Alumno alumno);

    List<CursoConvalidado> saveListCursoConvalidado(TrasladoBean trasladoBean, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<CursoConvalidado> alllCursoConvalidadoInTraslado(List<TramiteTraslado> listTramiteTraslado);

    Boolean verificarTramiteTraslado(Alumno alumno);

    List<Curso> allCurso(String nombre);

    void marcarFalla(Alumno alumno);

    void saveAccesoEspecial(AccesoEspecialBean accesoEspecialBean, DataSessionPivot ds);

    Usuario findUsuarioByPersona(Persona persona);

    List<AlumnoCursoCurricula> allAlumnoCursoByalumno(Alumno alumno, DynatableFilter filter);

    void habilitarAlumnoCursoCurricula(AlumnoCursoCurricula alumnoCursoCurricula, DataSessionPivot ds);

    List<CursoOpcionalCurricula> allcursosOpcional(Long idAlumno);

    void agregarAlumnoCursoCurricula(CursoOpcionalCurricula cursoOpcional, Alumno alumno);

    Alumno saveFotoCarnet(Alumno alumnoForm, DataSessionPivot ds);

    List<Carrera> allCarrerasOfFacultadEconomia();

    List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras);

    Docente finDocenteAccesoEspecial();

    List<CicloAcademico> ciclosAcademicosConvalidarCurso(Alumno alumno);

}
