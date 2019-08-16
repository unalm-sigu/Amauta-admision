package pe.edu.lamolina.pivot.dao.academico;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface SeccionDAO extends EasyDAO<Seccion> {

    List<Seccion> allByCargaAcademica(DynatableFilter filter, Docente docente);

    List<Seccion> allByFilter(Long idGrupo);

    Seccion findByCodeCiclo(String codigo, CicloAcademico ciclo);

    List<Seccion> allByCiclo(CicloAcademico ciclo);

    List<Seccion> allByCiclo(CicloAcademico ciclo, SeccionEstadoEnum... estados);

    List<Seccion> allActivosByGposSeccion(List<GrupoSeccion> gruposSeccion);

    List<Seccion> allOperativesByGpoSeccion(GrupoSeccion gruposSeccion);

    List<Seccion> allActivosByGpoSeccion(GrupoSeccion gruposSeccion);

    List<Seccion> allByGpoSeccion(GrupoSeccion gruposSeccion);

    List<Seccion> allWithMatriculadosByGposSeccion(List<GrupoSeccion> gsOrigenes);

    List<Seccion> allByGpoSeccionEstados(GrupoSeccion gruposSeccion, List<SeccionEstadoEnum> estadoEnums);

    List<Seccion> allByGposSeccion(GrupoSeccion gruposSeccion);

    List<Seccion> allByGposSeccionOrderedByCodigo2(GrupoSeccion gruposSeccion);

    List<Seccion> allForBoletinByCiclo(CicloAcademico cicloAcademico);

    List<Seccion> allByGposSeccion(List<GrupoSeccion> gruposSeccion);

    void updateSeccionGrupoHora(Seccion seccion);

    void updateSeccionAula(Seccion seccion);

    void updateRestriccionCapa(Seccion seccion);

    void updateSeccionVacantes(Seccion seccion);

    List<Seccion> allActivosByCursosCiclo(List<Curso> cursos, CicloAcademico cicloAcademico);

    List<Seccion> allMatriculablesBySecciones(List<Seccion> secciones);

    void allRegenerateReservadoByCiclo(CicloAcademico cicloAcademico);

    void updateEstadoFechaModUsuarioMod(Seccion seccion);

    void updateCodigoFechaModUsuarioMod(Seccion seccion);

    void updateSituacionDocente(Seccion seccion);

    List<Seccion> allByCodigo(String codigo);

    List<Seccion> allUnusedByCiclo(CicloAcademico ciclo);

    List<Seccion> allByGrupoSeccionByClone(List<GrupoSeccion> gsOrigenes);

    List<Seccion> allSeccionOrderByciclo(CicloAcademico ciclo);

    Seccion find(Seccion seccion);

    void setCodigo2Null(CicloAcademico ciclo);

    void updatePrecioByTpc(CicloAcademico cicloAcademico, String tpc, BigDecimal precio);

    void deleteAllByCiclo(CicloAcademico ciclo);

    void deleteAllNotSuperiorByCiclo(CicloAcademico ciclo);

    List<Seccion> allByCursoCicloExceptSeccion(Curso curso, CicloAcademico ciclo, Seccion seccion);

    void updateMatriculados(Seccion origen, Integer matriculados);

    void updatePrecioBySeccion(Seccion seccion);

    void setNullCodigo2ByCiclo(CicloAcademico ciclo);

    void updateCodigo2(List<Seccion> secciones);

    List<Seccion> allForRolExamenAndTipoGrupoHora(CicloAcademico ciclo, TipoGrupoHorasEnum tipoGrupoHorasEnum);

    List<Seccion> allByCicloAndGrupoHoras(CicloAcademico ciclo, GrupoHoras grupoHoras);

    List<Seccion> allByCicloAndCurso(CicloAcademico ciclo, Curso curso);

    Seccion findByGpoSeccionTipoSeccion(GrupoSeccion gpoSecc, TipoSeccionEnum tipoSeccion);

    List<Seccion> allForAsignacionAulaByCiclo(CicloAcademico ciclo, SeccionEstadoEnum... estados);

    void updateAsignacionAula(Seccion seccion);

    public void updateMatriculados(Seccion seccion);

    void updateColumns(Seccion seccion, String... columns);

    List<Seccion> allByCicloAndNombreLimit(CicloAcademico ciclo, RolExamenes rolExamenes, String nombre);

    List<Seccion> allSeccionesAulaAutoByCiclo(CicloAcademico ciclo);

    void updateAulaAignacionAutoByCiclo(CicloAcademico cicloAcademico, Boolean asignacion);

    void resetAsignacionAulaAuto(List<Seccion> secciones);

    List<Seccion> allByGrupoSecciones(List<GrupoSeccion> gruposSeccion);

    List<Seccion> allConCruce(CicloAcademico cicloAcademico);

    List<Seccion> allConCruceHorario(CicloAcademico cicloAcademico);
    List<Seccion> findByNombreCiclo(String nombre, CicloAcademico ciclo);

}
