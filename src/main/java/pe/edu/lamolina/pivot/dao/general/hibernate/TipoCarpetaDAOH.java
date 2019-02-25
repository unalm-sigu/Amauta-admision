package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;

@Repository
public class TipoCarpetaDAOH extends AbstractEasyDAO<TipoCarpeta> implements TipoCarpetaDAO {

    public TipoCarpetaDAOH() {
        super();
        setClazz(TipoCarpeta.class);
    }

    @Override
    public List<TipoCarpeta> allByTipoCarpetas(List<TipoCarpeta> tipoCarpetas) {
        Octavia sql = Octavia.query()
                .from(TipoCarpeta.class, "tip")
                .leftJoin("tipoCarpetaSuperior sup")
                .in("sup.id",tipoCarpetas)
                .orderBy("tip.nombre");
        return all(sql);
    }

    @Override
    public List<TipoCarpeta> allTipoCarpeta() {
        Octavia sql = Octavia.query()
                .from(TipoCarpeta.class, "tip")
                .leftJoin("tipoCarpetaSuperior sup")
                .isNull("sup.id")
                .orderBy("tip.nombre");
        return all(sql);
    }

}
