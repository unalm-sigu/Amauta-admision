package pe.edu.lamolina.amauta.dao.mensajeria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.AsuntoMensajeDAO;
import pe.edu.lamolina.model.social.AsuntoMensaje;

@Repository
public class AsuntoMensajeDAOH extends AbstractEasyDAO<AsuntoMensaje> implements AsuntoMensajeDAO {

    public AsuntoMensajeDAOH() {
        super();
        setClazz(AsuntoMensaje.class);
    }

    @Override
    public AsuntoMensaje findByTablaInstancia(String nombreTabla, Long instanciaTabla) {
        Octavia sql = Octavia.query()
                .from(AsuntoMensaje.class, "am")
                .filter("nombreTabla", nombreTabla)
                .filter("instanciaTabla", instanciaTabla);

        return find(sql);
    }
}
