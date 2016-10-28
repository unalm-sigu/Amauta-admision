package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.CarreraPostulaDAO;
import pe.edu.lamolina.pivot.model.inscripcion.CarreraPostula;
import org.springframework.stereotype.Repository;

@Repository
public class CarreraPostulaDAOH extends AbstractDAO<CarreraPostula> implements CarreraPostulaDAO {

    public CarreraPostulaDAOH() {
        super();
        setClazz(CarreraPostula.class);
    }
}

