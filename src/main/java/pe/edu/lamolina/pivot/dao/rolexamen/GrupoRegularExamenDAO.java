package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;

public interface GrupoRegularExamenDAO extends EasyDAO<GrupoRegularExamen> {

    List<GrupoRegularExamen> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegulare, List<GrupoHorasRolExamenEstadoEnum> estados);

    void updateEstado(GrupoRegularExamen grupoRegularExamenUpd);
}
