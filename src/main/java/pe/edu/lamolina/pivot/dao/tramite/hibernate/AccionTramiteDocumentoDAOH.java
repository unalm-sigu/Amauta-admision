package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteDocumentoDAO;

@Repository
public class AccionTramiteDocumentoDAOH extends AbstractEasyDAO<AccionTramiteDocumento> implements AccionTramiteDocumentoDAO {
    
    public AccionTramiteDocumentoDAOH() {
        super();
        setClazz(AccionTramiteDocumento.class);
    }
    
    @Override
    public List<AccionTramiteDocumento> allNextByEstadoInicio(TipoDocumentoAcademico tipoDocumentoAcademico, EstadoTramite estadoTramite) {
        Octavia sql = new Octavia()
                .from(AccionTramiteDocumento.class, "atd")
                .join("tipoDocumentoAcademico tipo","estadoTramite eti", "estadoTramiteFinal etf")
                .filter("eti.id", estadoTramite)
                .filter("tipo.id", estadoTramite);
        
        return all(sql);
    }
    
    @Override
    public AccionTramiteDocumento findOrderOneByTipoDocumento(TipoDocumentoAcademico tipoDocumentoAcademico, Long Order) {
        Octavia sql = new Octavia()
                .from(AccionTramiteDocumento.class, "atd")
                .join("tipoDocumentoAcademico tda", "estadoTramite", "estadoTramiteFinal")
                .filter("tda.id", tipoDocumentoAcademico)
                .filter("orden", Order);
        
        return find(sql);
    }
    
}
