package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

public interface AlumnoGrupoEspecialDAO extends EasyDAO<AlumnoGrupoEspecial> {

    Map<Long, Integer> countBySeccionesGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoEspecial> allBySeccionGrupoEspecialAndEstados(SeccionGrupoEspecial seccionGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoEspecial> allBySeccionGrupoEspecialAndEstados(List<SeccionGrupoEspecial> seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    void deleteByRolExamenes(RolExamenes rolExamenes);

    public List<AlumnoGrupoEspecial> allByDynatableAndSeccionGrupoEsp(DynatableFilter filter, SeccionGrupoEspecial seccionGrupoEspecial);

    void updateEstadoExclusion(AlumnoGrupoEspecial alumnoGrupoEspecial);

    void updateEstado(AlumnoGrupoEspecial alumnoGrupoEspecial);
}
