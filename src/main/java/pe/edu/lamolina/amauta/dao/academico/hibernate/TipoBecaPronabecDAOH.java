package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoBecaPronabecDAO;
import pe.edu.lamolina.model.pronabec.TipoBeca;

import java.util.List;

@Repository
public class TipoBecaPronabecDAOH extends AbstractEasyDAO<TipoBeca> implements TipoBecaPronabecDAO {

    public TipoBecaPronabecDAOH(){
        super();
        setClazz(TipoBeca.class);
    }

    @Override
    public List<TipoBeca> allTiposBecas() {
        Octavia sql = Octavia.query()
                .from(TipoBeca.class,"tb");
        return all(sql);
    }

    @Override
    public TipoBeca findByCodigo(String codigoTipoBeca) {
        Octavia sql = Octavia.query()
                .from(TipoBeca.class, "tb")
                .filter("tb.codigo", codigoTipoBeca);

        return find(sql);
    }
}
