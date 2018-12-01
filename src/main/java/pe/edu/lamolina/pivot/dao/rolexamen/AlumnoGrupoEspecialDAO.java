package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

public interface AlumnoGrupoEspecialDAO extends EasyDAO<AlumnoGrupoEspecial> {

    Map<Long, Integer> countBySeccionesGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoEspecial> allBySeccionGrupoEspecialAndEstados(SeccionGrupoEspecial seccionGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    void deleteByRolExamenes(RolExamenes rolExamenes);
}
