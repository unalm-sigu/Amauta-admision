package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.ModalidadIngresoCicloDAO;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadIngresoCiclo;
import org.springframework.stereotype.Repository;

@Repository
public class ModalidadIngresoCicloDAOH extends AbstractDAO<ModalidadIngresoCiclo> implements ModalidadIngresoCicloDAO {

    public ModalidadIngresoCicloDAOH() {
        super();
        setClazz(ModalidadIngresoCiclo.class);
    }
}

