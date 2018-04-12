package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.SolicitudConstanciaDAO;

@Repository
public class SolicitudConstanciaDAOH extends AbstractEasyDAO<TramiteDocumentoAcademico> implements SolicitudConstanciaDAO {

    public SolicitudConstanciaDAOH() {
        super();
        setClazz(TramiteDocumentoAcademico.class);
    }
}
