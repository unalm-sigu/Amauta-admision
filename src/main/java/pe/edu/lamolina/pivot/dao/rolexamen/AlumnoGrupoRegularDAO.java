package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.Date;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoGrupoRegularDAO extends EasyDAO<AlumnoGrupoRegular> {

    List<AlumnoGrupoRegular> allByLetraGrupoActives(LetraGrupoRegular letraGrupoRegular);

    List<AlumnoGrupoRegular> allByLetraGrupoAndEstado(LetraGrupoRegular letraGrupoRegular, AlumnoRolExamenEstadoEnum estadoEnum);

    List<AlumnoGrupoRegular> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular, List<AlumnoRolExamenEstadoEnum> estados);

    List<AlumnoGrupoRegular> allBySeccionGrupoRegularAndEstados(SeccionGrupoRegular seccionGrupoRegular,
            AlumnoRolExamenEstadoEnum... estados);

    void updateEstado(AlumnoGrupoRegular alumnoGrupoRegular);

    void updateEstado(List<Alumno> alumnos, AlumnoRolExamenEstadoEnum estadoEnum, Usuario usuario, Date fecha);

    Map<Long, Integer> countByLetrasGruposRegulares(List<LetraGrupoRegular> letraGrupoRegulars, AlumnoRolExamenEstadoEnum... estados);

    void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);
}
