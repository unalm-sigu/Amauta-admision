package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.CarreraCachimbos;
import org.springframework.stereotype.Repository;

@Repository
public class CarreraCachimbosDAOH extends AbstractEasyDAO<CarreraCachimbos> implements CarreraCachimbosDAO {

    public CarreraCachimbosDAOH() {
        super();
        setClazz(CarreraCachimbos.class);
    }
}

