package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.TipoTramite;

public interface AccionTramiteAcademicoDAO extends EasyDAO<AccionTramiteAcademico> {

    AccionTramiteAcademico find(long id);

    List<AccionTramiteAcademico> allByTipoTramiteAndEstadoTramiteInicial(TipoTramite tipoTramite, EstadoTramite estadoTramiteInicial);

    List<AccionTramiteAcademico> all(TipoTramite tipoTramite, EstadoTramite estadoTramiteInicial);

    List<AccionTramiteAcademico> allByTipoTramite(TipoTramite tipoTramite);
}
