package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.ModalidadGrupoDAO;
import pe.edu.lamolina.pivot.model.inscripcion.ModalidadGrupo;
import org.springframework.stereotype.Repository;

@Repository
public class ModalidadGrupoDAOH extends AbstractDAO<ModalidadGrupo> implements ModalidadGrupoDAO {

    public ModalidadGrupoDAOH() {
        super();
        setClazz(ModalidadGrupo.class);
    }
}

