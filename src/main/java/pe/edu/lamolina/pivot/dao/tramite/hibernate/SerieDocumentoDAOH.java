package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.hibernate.LockOptions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.pivot.dao.tramite.SerieDocumentoDAO;

@Repository
public class SerieDocumentoDAOH extends AbstractEasyDAO<SerieDocumento> implements SerieDocumentoDAO {

    public SerieDocumentoDAOH() {
        super();
        setClazz(SerieDocumento.class);
    }

    @Override
    public SerieDocumento findCorrelativo(TipoDocumentoCompania tipo, String nroSerie) {
        Octavia sql = Octavia.query()
                .from(SerieDocumento.class, "sd")
                .join("tipoDocumentoCompania tdc")
                .filter("tdc.id", tipo)
                .filter("sd.numeroSerie", nroSerie);
        return find(sql);

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public SerieDocumento findLock(Long id) {
        return (SerieDocumento) getCurrentSession().load(SerieDocumento.class, id, LockOptions.UPGRADE);
    }

}
