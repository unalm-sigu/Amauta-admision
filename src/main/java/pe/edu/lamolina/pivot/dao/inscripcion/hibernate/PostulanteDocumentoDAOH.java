package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDocumentoDAO;
import pe.edu.lamolina.pivot.model.inscripcion.PostulanteDocumento;
import org.springframework.stereotype.Repository;

@Repository
public class PostulanteDocumentoDAOH extends AbstractDAO<PostulanteDocumento> implements PostulanteDocumentoDAO {

    public PostulanteDocumentoDAOH() {
        super();
        setClazz(PostulanteDocumento.class);
    }
}

