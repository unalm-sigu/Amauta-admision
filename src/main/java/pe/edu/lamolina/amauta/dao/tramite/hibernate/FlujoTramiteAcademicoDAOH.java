package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.FlujoTramiteAcademico;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteAcademicoDAO;

@Repository
public class FlujoTramiteAcademicoDAOH extends AbstractEasyDAO<FlujoTramiteAcademico> implements FlujoTramiteAcademicoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public FlujoTramiteAcademicoDAOH() {
        super();
        setClazz(FlujoTramiteAcademico.class);
    }

    public List<FlujoTramiteAcademico> allByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(FlujoTramiteAcademico.class, "fta")
                .join("tramiteAcademico tra", "estadoTramite")
                .filter("tra.id", tramite);
        return all(sql);
    }

}
