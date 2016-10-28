package pe.edu.lamolina.pivot.dao.vacantes.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.vacantes.CarreraModalidadIngresoDAO;
import pe.edu.lamolina.pivot.model.vacantes.CarreraModalidadIngreso;
import org.springframework.stereotype.Repository;

@Repository
public class CarreraModalidadIngresoDAOH extends AbstractDAO<CarreraModalidadIngreso> implements CarreraModalidadIngresoDAO {

    public CarreraModalidadIngresoDAOH() {
        super();
        setClazz(CarreraModalidadIngreso.class);
    }
}

