package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.ConjuntoExamenGrupos;
import pe.edu.lamolina.pivot.dao.rolexamen.ConjuntoExamenGruposDAO;

@Repository
public class ConjuntoExamenGruposDAOH extends AbstractEasyDAO<ConjuntoExamenGrupos> implements ConjuntoExamenGruposDAO {

    public ConjuntoExamenGruposDAOH() {
        super();
        setClazz(ConjuntoExamenGrupos.class);
    }
}
