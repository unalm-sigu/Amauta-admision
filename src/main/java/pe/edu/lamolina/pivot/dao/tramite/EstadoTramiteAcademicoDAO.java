package pe.edu.lamolina.pivot.dao.tramite;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.EstadoTramiteAcademico;
import pe.edu.lamolina.model.tramite.TipoTramite;

@Repository
public interface EstadoTramiteAcademicoDAO extends EasyDAO<EstadoTramiteAcademico> {

    EstadoTramiteAcademico findByTipoTramiteOrden(TipoTramite tipoTramite, Integer orden);

    EstadoTramiteAcademico findByTipoAndEstadoTramite(TipoTramite tipoTramite, EstadoTramite estadoTramite);

}
