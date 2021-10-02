package pe.edu.lamolina.amauta.dao.socioeconomico.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.amauta.dao.socioeconomico.FlujoFichaSocioeconomicaDAO;
import pe.edu.lamolina.model.socioeconomico.FlujoFichaSocioeconomica;

@Repository
public class FlujoFichaSocioeconomicaDAOH extends AbstractEasyDAO<FlujoFichaSocioeconomica> implements FlujoFichaSocioeconomicaDAO {

    public FlujoFichaSocioeconomicaDAOH() {
        super();
        setClazz(FlujoFichaSocioeconomica.class);
    }
}
