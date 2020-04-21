package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;

public interface TramiteCorreccionHistorialDAO extends EasyDAO<TramiteCorreccionHistorial> {

    public List<TramiteCorreccionHistorial> allByCicloDynatable(CicloAcademico cicloAcademico, DynatableFilter filter);

    void updateColumns(TramiteCorreccionHistorial correccionHistorial, String... columns);

    public TramiteCorreccionHistorial findTramite(Tramite tramite);
}
