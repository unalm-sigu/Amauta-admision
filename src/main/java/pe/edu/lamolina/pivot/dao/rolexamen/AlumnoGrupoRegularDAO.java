package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;

public interface AlumnoGrupoRegularDAO extends EasyDAO<AlumnoGrupoRegular> {

    List<AlumnoGrupoRegular> allByLetraGrupoActives(LetraGrupoRegular letraGrupoRegular);

    List<AlumnoGrupoRegular> allByLetraGrupoAndEstado(LetraGrupoRegular letraGrupoRegular, AlumnoRolExamenEstadoEnum estadoEnum);

    List<AlumnoGrupoRegular> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular, List<AlumnoRolExamenEstadoEnum> estados);

    void updateEstado(AlumnoGrupoRegular alumnoGrupoRegular);
}
