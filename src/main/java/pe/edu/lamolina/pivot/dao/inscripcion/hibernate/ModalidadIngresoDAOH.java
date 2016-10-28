package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.ModalidadIngresoDAO;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngreso;
import org.springframework.stereotype.Repository;

@Repository
public class ModalidadIngresoDAOH extends AbstractDAO<ModalidadIngreso> implements ModalidadIngresoDAO {

    public ModalidadIngresoDAOH() {
        super();
        setClazz(ModalidadIngreso.class);
    }
}

