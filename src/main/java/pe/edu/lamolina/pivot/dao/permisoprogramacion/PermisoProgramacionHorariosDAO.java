package pe.edu.lamolina.pivot.dao.permisoprogramacion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;

public interface PermisoProgramacionHorariosDAO extends EasyDAO<PermisosProgramacionHorarios> {

    List<PermisosProgramacionHorarios> allPermisos(List<Colaborador> colaboradors);
}
