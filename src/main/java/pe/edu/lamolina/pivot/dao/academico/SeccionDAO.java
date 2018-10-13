package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;

public interface SeccionDAO extends EasyDAO<Seccion> {

    List<Seccion> allByCargaAcademica(DynatableFilter filter, Docente docente);

    List<Seccion> allByFilter(Long idGrupo);

    Seccion findByCodeCiclo(String codigo, CicloAcademico ciclo);

    List<Seccion> allByCiclo(CicloAcademico ciclo);

    List<Seccion> allActivosByGposSeccion(List<GrupoSeccion> gruposSeccion);

    List<Seccion> allOperativesByGpoSeccion(GrupoSeccion gruposSeccion);

    List<Seccion> allActivosByGpoSeccion(GrupoSeccion gruposSeccion);

    List<Seccion> allWithMatriculadosByGposSeccion(List<GrupoSeccion> gsOrigenes);

    List<Seccion> allByGpoSeccionEstados(GrupoSeccion gruposSeccion, List<SeccionEstadoEnum> estadoEnums);

    List<Seccion> allByGposSeccion(GrupoSeccion gruposSeccion);

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

}
