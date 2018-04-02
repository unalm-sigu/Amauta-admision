package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuesta.TipoLikert;
import pe.edu.lamolina.pivot.dao.encuesta.TipoLikertDAO;

@Repository
public class TipoLikertDAOH extends AbstractEasyDAO<TipoLikert> implements TipoLikertDAO {

    public TipoLikertDAOH() {
        super();
        setClazz(TipoLikert.class);
    }

    @Override
    public List<TipoLikert> allByOpciones(Integer opciones) {
        Octavia sql = Octavia.query()
                .from(TipoLikert.class, "tl")
                .filter("tl.opciones", opciones);
        return all(sql);
    }

}
