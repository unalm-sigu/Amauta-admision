package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.DocumentoModalidadDAO;
import pe.edu.lamolina.pivot.model.inscripcion.DocumentoModalidad;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentoModalidadDAOH extends AbstractDAO<DocumentoModalidad> implements DocumentoModalidadDAO {

    public DocumentoModalidadDAOH() {
        super();
        setClazz(DocumentoModalidad.class);
    }
}

