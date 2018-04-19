package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.TipoConstanciaDAO;

@Repository
public class TipoConstanciaDAOH extends AbstractEasyDAO<TipoDocumentoAcademico> implements TipoConstanciaDAO {

    public TipoConstanciaDAOH() {
        super();
        setClazz(TipoDocumentoAcademico.class);
    }

    @Override
    public List<TipoDocumentoAcademico> allDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TipoDocumentoAcademico.class, "tda")
                .searchFields("tda.nombre")
                .orderBy("nombre");
        return all(sql);
    }
}
