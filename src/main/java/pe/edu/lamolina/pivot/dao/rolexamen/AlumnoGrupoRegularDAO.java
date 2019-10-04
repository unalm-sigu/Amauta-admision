package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.Date;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoGrupoRegularDAO extends EasyDAO<AlumnoGrupoRegular> {

    List<AlumnoGrupoRegular> allByLetraGrupoActives(LetraGrupoRegular letraGrupoRegular);

    List<AlumnoGrupoRegular> allByLetraGrupoAndEstado(LetraGrupoRegular letraGrupoRegular, AlumnoRolExamenEstadoEnum estadoEnum);

    List<AlumnoGrupoRegular> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoRegular> allBySeccionGrupoRegularAndEstados(SeccionGrupoRegular seccionGrupoRegular,
            AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoGrupoRegular> allBySeccionGrupoRegularAndEstados(List<SeccionGrupoRegular> seccionGrupoRegular,
            AlumnoRolExamenEstadoEnum... estados);

    void updateEstadoExclusion(AlumnoGrupoRegular alumnoGrupoRegular);

    void updateEstado(List<Alumno> alumnos, AlumnoRolExamenEstadoEnum estadoEnum, Usuario usuario, Date fecha);

    Map<Long, Integer> countByLetrasGruposRegulares(List<LetraGrupoRegular> letraGrupoRegulars, AlumnoRolExamenEstadoEnum... estados);

    Map<Long, Integer> countBySeccionesGruposRegulares(List<SeccionGrupoRegular> seccionesGrupoRegular, AlumnoRolExamenEstadoEnum... estados);

    void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    List<AlumnoGrupoRegular> allByDynatableAndLetraGrupoRegular(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular);

    void updateEstado(AlumnoGrupoRegular alumnoGrupoRegular);

    List<AlumnoGrupoRegular> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, AlumnoRolExamenEstadoEnum estadoEnum);

    void createForSeccionGrupoRegular(
            List<AlumnoGrupoRegular> alumnosGpoRegular,
            SeccionGrupoRegular seccionGpoRegular,
            Date fecha,
            Usuario user);

    int saveAll(List<AlumnoGrupoRegular> alumnosSeccGpoReg);

}
