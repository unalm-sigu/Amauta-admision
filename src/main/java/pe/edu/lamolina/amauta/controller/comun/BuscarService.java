package pe.edu.lamolina.amauta.controller.comun;

import java.util.List;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;

public interface BuscarService {

    List<Curso> allCursosSCA(String nombre, PlanCalificacion planCalificacion, CicloAcademico ciclo);

    List<Ubicacion> allDistritosByName(String nombre);

    List<DepartamentoAcademico> allDepartamentosByName(String nombre);

    List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre);

    List<Pais> allPaisesByName(String nombre);

    List<Universidad> allUniversidadByName(String nombre);

    List<Empresa> allEmpresaByName(Pais pais, String nombre);

    List<SituacionAcademica> allSituaciones();

    List<ModalidadEstudio> allModalidadEstudios();

    List<Facultad> allFacultades();

    List<Carrera> allCarrerasByName(String nombre);

    List<GrupoSeccion> allGrupoSeccionesByCiclo(CicloAcademico ciclo, String codigo, Long curso);

    List<Curso> allCurso(String codigo, CicloAcademico ciclo);

    List<Seccion> allSeccionByCodigo(String codigo, CicloAcademico ciclo);

    List<CicloAcademico> allCicloByDescripcion(String nombre);

    List<PlanCalificacion> allPlanCalificacionByDescripcion(String nombre);

    List<Curso> allCursoByModalidadEstudioNombre(String nombre, ModalidadEstudioEnum moda);

    List<CicloAcademico> allCicloByDescripcionDescendent(String nombre);

    List<Universidad> allUniversidadByNamePais(String nombre, Long pais);

}
