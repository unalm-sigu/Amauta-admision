package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.EstadoTramiteAcademico;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteAcademicoDAO;

@Repository
public class EstadoTramiteAcademicoDAOH extends AbstractEasyDAO<EstadoTramiteAcademico> implements EstadoTramiteAcademicoDAO {

    public EstadoTramiteAcademicoDAOH() {
        super();
        setClazz(EstadoTramiteAcademico.class);
    }

    @Override
    public EstadoTramiteAcademico findByTipoTramiteOrden(TipoTramite tipoTramite, Integer orden) {
        Octavia sql = Octavia.query()
                .from(EstadoTramiteAcademico.class, "eta")
                .join("tipoTramite tt")
                .left("tipoOficinaOrigen too", "tipoOficinaDestino tod")
                .filter("tt.id", tipoTramite)
                .filter("eta.orden", orden);
        return find(sql);
    }

}
