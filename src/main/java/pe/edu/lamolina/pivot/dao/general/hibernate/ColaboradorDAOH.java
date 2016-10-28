package pe.edu.lamolina.pivot.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import org.springframework.stereotype.Repository;

@Repository
public class ColaboradorDAOH extends AbstractDAO<Colaborador> implements ColaboradorDAO {

    public ColaboradorDAOH() {
        super();
        setClazz(Colaborador.class);
    }
}

