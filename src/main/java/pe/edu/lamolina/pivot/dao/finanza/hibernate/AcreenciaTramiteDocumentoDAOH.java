package pe.edu.lamolina.pivot.dao.finanza.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.finanzas.AcreenciaTramiteDocumento;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaTramiteDocumentoDAO;

@Repository
public class AcreenciaTramiteDocumentoDAOH extends AbstractEasyDAO<AcreenciaTramiteDocumento> implements AcreenciaTramiteDocumentoDAO {

    public AcreenciaTramiteDocumentoDAOH() {
        super();
        setClazz(AcreenciaTramiteDocumento.class);
    }

    @Override
    public AcreenciaTramiteDocumento findByTramiteDocumentoAcademico(TramiteDocumentoAcademico tramiteDocumentoAcademico) {
        Octavia sql = new Octavia()
                .from(AcreenciaTramiteDocumento.class, "atd")
                .join("tramiteDocumentoAcademico tda")
                .filter("tda.id", tramiteDocumentoAcademico);
        return find(sql);
    }

}
