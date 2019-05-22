package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface CursoExcluidoDAO extends EasyDAO<CursoExcluido> {

    List<CursoExcluido> allByRolExamenes(RolExamenes rolExamenes);

    CursoExcluido findActiveByCursoAndRolExamenes(Curso curso, RolExamenes rolExamenes);

    void updateAnulacion(CursoExcluido cursoExcluidoUpd);

    List<CursoExcluido> allByRolExamenes(RolExamenes rolExamenes, EstadoEnum estadoEnum);

    void updateColumns(CursoExcluido sursoExcluido, String... columns);

}
