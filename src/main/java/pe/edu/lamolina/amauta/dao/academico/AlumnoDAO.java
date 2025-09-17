package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableResumen;

public interface AlumnoDAO extends EasyDAO<Alumno> {

    List<Alumno> allByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo);

    List<Alumno> allByModalidadesDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, List<String> modalidades);

    Alumno findByCodigo(String codigoAlumno);

    Alumno findFlatByCodigo(String codigoAlumno);

    Alumno findByPersona(Persona persona, CicloAcademico cicloAcademico);

    Alumno findByPersona(Persona persona);

    Alumno find(Alumno alumno);

    Alumno findAllInfo(Long id);

    List<Alumno> allWithAllInfo(List<Alumno> alumnos);

    List<Alumno> allInfoByAlumnos(List<Alumno> alumnos);

    Alumno findSituacionAcademica(Alumno alumno);

    Alumno findByPersonaCicloIngreso(Persona persona, CicloAcademico ciclo);

    Alumno findLock(Long id);

    AlumnoResumen findResumen();

    AlumnoResumen findResumen(List<Carrera> carreras);

    MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico);

    Long countByPlanCurricular(PlanCurricular plan);

    List<Alumno> allByPersona(Persona persona);

    List<Alumno> allByPlanCurricular(PlanCurricular planCurricular);

    List<Alumno> allByRolDynatable(DynatableFilter filter, List<Carrera> carreras);

    List<Alumno> allByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    List<Alumno> allByName(String nombre);
    List<Alumno> allAlumnosSancionados(String nombre);

    List<Alumno> allByNameFacultad(String nombre, Facultad facultad);

    List<Alumno> allByNamePosgrado(String nombre);

    List<Alumno> allByNamePregrado(String nombre);

    List<Alumno> allIngresantePregradoByCiclo(ModalidadEstudio modalidad, CicloAcademico cicloAcademico, List<Alumno> alumnoExclude);

    List<Alumno> allByNameModalidadEstudioCiclo(String nombre, ModalidadEstudio modalidad, CicloAcademico cicloAcademico);

    List<Alumno> allByPersonas(List<Persona> personas);

    List<Alumno> allBySituaciones(ModalidadEstudio modalidad, List<SituacionAcademica> situaciones);

    void updateCicloActivoSituacionAcad(Alumno alumno);

    void updateSituacionAcad(Alumno alumno);

    void updateSituacionCicloCapa(Alumno alumno);

    void updateSituacionCicloCapaPPA(Alumno alumno);

    void updateCicloActivoRegular(Alumno alumno);

    List<Alumno> allByNombreFacultad(String name, List<Facultad> facultad);

    List<Alumno> allByIds(Long[] idAlumnos);

    void updateSituacionCapaCredCur(Alumno alumno);

    List<Alumno> allByNameSinMatriculaResumen(String nombre, CicloAcademico cicloAcademico);

    List<Alumno> allByNameSinMatriculaResumenPRE_VIS(String nombre, CicloAcademico cicloAcademico);

    List<Alumno> allByCarreraCicloMayores(Carrera carrera, String codigoCiclo);

    List<Alumno> allMatriculadosByGpoSeccion(GrupoSeccion gpoSecc);

    void updatePlanCurricular(Alumno alumno);

    List<String> allYearsCiclos();

    void updateFields(Alumno alumno, String[] fields);

    List<Alumno> allPendingPromedioByCicloYear(String year);

    List<Alumno> allPendingPREPromedioByCicloYear(String year);

    void updatePromedioProcesado(Alumno alumno);

    List<Alumno> allIngresantesByCiclos(List<CicloAcademico> ciclosIngresantes, String mod);

    List<Alumno> allMatriculadosNoEgresadosByCiclos(List<CicloAcademico> ciclosPrevios);

    List<Alumno> allEstudiaronByCiclos(List<CicloAcademico> ciclosPrevios);

    List<Alumno> allDesertorByName(String nombre, Long oficinaCode);

    List<Alumno> allPendingEpgPromedioByCicloYear(String year);

    List<Alumno> allByNameCondicional(String nombre, CicloAcademico cicloAcademico);

    List<Alumno> allByAlumnos(List<Alumno> alumnos);

    List<Alumno> allMatriculadosByDetalleGpoAlu(DetalleGrupoAlumno dga, CicloAcademico ciclo);

    List<Alumno> allMatriculablesByDetalleGpoAlu(DetalleGrupoAlumno dga, CicloAcademico ciclo);

    List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina);

    List<Alumno> allPendingPromedioByCicloYearAndModalidadEst(String year, ModalidadEstudioEnum modalidadEstudioEnum);

    List<Alumno> allByModalidadSituacionesNoAptas(ModalidadEstudioEnum modalidadEstudioEnum, List<String> situacionesNoAptas);

    List<Alumno> allByPlanCarrera(String codigoCarrera);

    List<Alumno> allPregradoPendingPlanCurricula(CicloAcademico cicloIngreso);

    int updateList(List<Alumno> alumnos, String... columns);

    List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras);

    List<Alumno> pendientesHistorial(CicloAcademico cicloAcademico);

    List<Alumno> allByNoMatriculableCicloAnt(List<CicloAcademico> cicloAnt);

    List<Alumno> allByCustomQuery(CicloAcademico cicloAcademico);

    List<Alumno> allDynatableAlumnoOmisoEleccion(DynatableFilter filter);

    List<Alumno> allByCodigos(List<String> codigosMatricula);

    Alumno findFirstByPersona(Persona persona);

    List<Alumno> allAlumnoHistoricoByCarrerasDynatable(DynatableFilter filter, List<Carrera> carreras, String todo);

    List<Alumno> correccionNivelacion(CicloAcademico cicloAcademico);

    List<Alumno> allActivoPregradoByNombre(String nombre);

    List<Alumno> allIngresantePregradoByCicloIngreso(ModalidadEstudio modalidad, CicloAcademico ciclo);

    Alumno findBySitCodigo(String findBySitCodigo);

    List<Alumno> allAlumnoByYear(Integer year);

    List<Alumno> allIngresantePregradoByCicloIngreso(CicloAcademico ciclo);

    List<Alumno> allAlumnosTmp();
}
