package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;

public interface SeccionExcluidoDAO extends EasyDAO<SeccionExcluido> {

    List<SeccionExcluido> allByRolExamenes(RolExamenes rolExamenes);

    void deleteBySecciones(List<Seccion> secciones);

    SeccionExcluido findByRolExamenesAndSeccion(RolExamenes rolExamenes, Seccion seccion, EstadoEnum... estadoEnum);

    void deleteByRolExamenes(RolExamenes rolExamenes);

    List<SeccionExcluido> allByCursoExcluido(CursoExcluido cursoExcluido, EstadoEnum... estadoEnum);

    void updateColumns(SeccionExcluido seccionExcluido, String... columns);

    Integer countByCursoExcluido(CursoExcluido cursoExcluido, EstadoEnum... estados);

    void deleteByCursoExcluido(CursoExcluido cursoExcluido);
}
