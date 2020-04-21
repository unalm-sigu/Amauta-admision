package pe.edu.lamolina.amauta.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.amauta.dao.general.ParametroDAO;

@Repository
public class ParametroDAOH extends AbstractEasyDAO<Parametro> implements ParametroDAO {

    public ParametroDAOH() {
        super();
        setClazz(Parametro.class);
    }

    @Override
    public Parametro findByAmbienteParametroSistema(AmbienteAplicacionEnum ambiente, ParametrosSistemasEnum parametrosSistemas) {
        Octavia sql = Octavia.query()
                .from(Parametro.class, "p")
                //.filter("sistema", sistema)
                .filter("ambiente", ambiente)
                .filter("contexto", parametrosSistemas.getContexto())
                .filter("parametro", parametrosSistemas.getParametro());

        return (Parametro) sql.find(getCurrentSession());
    }
}
