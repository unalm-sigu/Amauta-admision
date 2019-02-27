package pe.edu.lamolina.pivot.controller.academico.curso;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CursoService {

    List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos);

    Curso save(Curso curso, DataSessionPivot ds);

    Curso find(Long id);

    void cambiarEstadoCurso(Curso curso);

    List<ModalidadEstudio> modalidadesEstudioPrePost(Compania cia);

    List<Carrera> allByModalidadEstudioNombre(String codigoEstudio, String nombre);

    List<Idioma> allIdiomas();

    List<DepartamentoAcademico> allDepartamentos(Compania cia);

    List<Carrera> allCarrerasByPostgrado();

    List<Docente> allDocentesByDepartamento(String nombre, DepartamentoAcademico departamentoAcademico);

    NombreCurso saveIdioma(NombreCurso nombreCurso, DataSessionPivot ds);

    NombreCurso updateIdioma(NombreCurso nombreCurso, DataSessionPivot ds);

    void deleteIdioma(NombreCurso nombreCurso, DataSessionPivot ds);

    List<TipoCarpeta> allTiposCarpeta();

}
