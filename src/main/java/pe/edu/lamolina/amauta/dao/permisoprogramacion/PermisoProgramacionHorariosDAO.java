package pe.edu.lamolina.amauta.dao.permisoprogramacion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;

public interface PermisoProgramacionHorariosDAO extends EasyDAO<PermisosProgramacionHorarios> {

    List<PermisosProgramacionHorarios> allPermisos(List<Colaborador> colaboradors);

    List<PermisosProgramacionHorarios> allByNivelPermiso(PermisoProgramacionNivelEnum nivelEnum, Long idColaboradorAnexo);

    public PermisosProgramacionHorarios findByColaborador(Long id, PermisoProgramacion permisoProgramacion);
}
