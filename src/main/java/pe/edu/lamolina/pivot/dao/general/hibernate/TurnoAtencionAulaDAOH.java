package pe.edu.lamolina.pivot.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.TurnoAtencionAula;
import pe.edu.lamolina.pivot.dao.general.TurnoAtencionAaulaDAO;

@Repository
public class TurnoAtencionAulaDAOH extends AbstractEasyDAO<TurnoAtencionAula> implements TurnoAtencionAaulaDAO {

    public TurnoAtencionAulaDAOH() {
        this.setClazz(TurnoAtencionAula.class);
    }

}
