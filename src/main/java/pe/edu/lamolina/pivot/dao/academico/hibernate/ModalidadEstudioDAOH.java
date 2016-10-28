package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import org.springframework.stereotype.Repository;

@Repository
public class ModalidadEstudioDAOH extends AbstractDAO<ModalidadEstudio> implements ModalidadEstudioDAO {

    public ModalidadEstudioDAOH() {
        super();
        setClazz(ModalidadEstudio.class);
    }
}

