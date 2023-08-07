package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoAtencionTutoradoDAO;
import pe.edu.lamolina.model.tutoria.TipoAtencionTutorado;

@Repository
public class TipoAtencionTutoradoDAOH extends AbstractEasyDAO<TipoAtencionTutorado> implements TipoAtencionTutoradoDAO {

    public TipoAtencionTutoradoDAOH() {
        super();
        setClazz(TipoAtencionTutorado.class);
    }

    @Override
    public List<TipoAtencionTutorado> all() {
        Octavia sql = new Octavia()
                .from(TipoAtencionTutorado.class, "tat")
                .orderBy("tat.grupoAtencion", "tat.orden");

        return all(sql);
    }

}
