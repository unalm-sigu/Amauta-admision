package pe.edu.lamolina.pivot.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.finanzas.AbonoPostulanteDAO;
import pe.edu.lamolina.pivot.model.finanzas.AbonoPostulante;
import org.springframework.stereotype.Repository;

@Repository
public class AbonoPostulanteDAOH extends AbstractDAO<AbonoPostulante> implements AbonoPostulanteDAO {

    public AbonoPostulanteDAOH() {
        super();
        setClazz(AbonoPostulante.class);
    }
}

