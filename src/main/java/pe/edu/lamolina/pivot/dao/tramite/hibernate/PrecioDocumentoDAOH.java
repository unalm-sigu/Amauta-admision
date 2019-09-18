package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.PrecioDocumentoDAO;

@Repository
public class PrecioDocumentoDAOH extends AbstractEasyDAO<PrecioDocumento> implements PrecioDocumentoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PrecioDocumentoDAOH() {
        super();
        setClazz(PrecioDocumento.class);
    }

    @Override
    public List<PrecioDocumento> allPrecioDocumento() {
        Octavia sql = Octavia.query()
                .from(PrecioDocumento.class, "pd")
                .join("tipoDocumento td", "idioma idi", "cuentaBancaria cb");
        return all(sql);
    }

    @Override
    public PrecioDocumento findByTipoIdioma(TipoDocumentoAcademico tipoDocumento, Idioma idioma) {
        Octavia sql = Octavia.query()
                .from(PrecioDocumento.class, "pd")
                .join("tipoDocumento td", "idioma idi").
                left("cuentaBancaria cb").
                filter("td.id", tipoDocumento).
                filter("idi.id", idioma);
        return find(sql);
    }

    @Override
    public List<PrecioDocumento> allByTipoDocumentoAcademico(List<TipoDocumentoAcademico> tipos) {
        Octavia sql = Octavia.query()
                .from(PrecioDocumento.class, "pd")
                .join("tipoDocumento td", "idioma idi", "cuentaBancaria cb")
                .in("td.id", tipos);
        return all(sql);
    }

    @Override
    public List<PrecioDocumento> allByTipoDocumentoAcademico(TipoDocumentoAcademico tipo) {
        Octavia sql = Octavia.query()
                .from(PrecioDocumento.class, "pd")
                .join("tipoDocumento td", "idioma idi", "cuentaBancaria cb")
                .filter("td.id", tipo);
        return all(sql);
    }

}
