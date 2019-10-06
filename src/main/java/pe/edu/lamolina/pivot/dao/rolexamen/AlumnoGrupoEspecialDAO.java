package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.Date;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoGrupoEspecialDAO extends EasyDAO<AlumnoGrupoEspecial> {

    Map<Long, Integer> countBySeccionesGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoEspecial> allBySeccionGrupoEspecialAndEstados(SeccionGrupoEspecial seccionGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoEspecial> allBySeccionGrupoEspecialAndEstados(List<SeccionGrupoEspecial> seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum... estados);

    void deleteByRolExamenes(RolExamenes rolExamenes);

    List<AlumnoGrupoEspecial> allByDynatableAndSeccionGrupoEsp(DynatableFilter filter, SeccionGrupoEspecial seccionGrupoEspecial);

    void updateEstadoExclusion(AlumnoGrupoEspecial alumnoGrupoEspecial);

    void updateEstado(AlumnoGrupoEspecial alumnoGrupoEspecial);

    List<AlumnoGrupoEspecial> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoEspecial> allByFechaEstados(Date fecha, AlumnoRolExamenEstadoEnum... estados);

    void createForSeccionGrupoEspecial(
            List<AlumnoGrupoEspecial> alumnosGpoEspecial,
            SeccionGrupoEspecial seccionGpoEspecial,
            Date fecha,
            Usuario user);

    int saveList(List<AlumnoGrupoEspecial> alumnosGpoEsp);
}
