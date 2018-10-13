package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.CostoDocumentoDAO;

@Repository
public class CostoDocumentoDAOH extends AbstractEasyDAO<PrecioDocumento> implements CostoDocumentoDAO {

    public CostoDocumentoDAOH() {
        super();
        setClazz(PrecioDocumento.class);
    }

    @Override
    public List<PrecioDocumento> allDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PrecioDocumento.class, "pda")
                .join("tipoDocumento tda", "idioma")
                .searchFields("tda.nombre")
                .orderBy("tda.nombre");
        return all(sql);
    }

    @Override
    public PrecioDocumento findById(PrecioDocumento precioDocumento) {
        Octavia sql = new Octavia()
                .from(PrecioDocumento.class, "pda")
                .join("tipoDocumento tda", "idioma")
                .filter("id", precioDocumento);
        return find(sql);
    }

    @Override
    public PrecioDocumento findTipoDocAndIdioma(TipoDocumentoAcademico tipoDocumento, Idioma idioma) {
        Octavia sql = new Octavia()
                .from(PrecioDocumento.class, "pda")
                .join("tipoDocumento tda", "idioma idi")
                .filter("tda.id", tipoDocumento)
                .filter("idi.id", idioma);
        return find(sql);
    }
}
