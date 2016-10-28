package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.DocumentoRequisitoDAO;
import pe.edu.lamolina.pivot.model.inscripcion.DocumentoRequisito;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentoRequisitoDAOH extends AbstractDAO<DocumentoRequisito> implements DocumentoRequisitoDAO {

    public DocumentoRequisitoDAOH() {
        super();
        setClazz(DocumentoRequisito.class);
    }
}

