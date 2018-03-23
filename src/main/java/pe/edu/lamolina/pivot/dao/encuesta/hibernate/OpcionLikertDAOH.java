package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuesta.CursoSinEncuesta;
import pe.edu.lamolina.model.encuesta.OpcionLikert;
import pe.edu.lamolina.pivot.dao.encuesta.OpcionLikertDAO;

@Repository
public class OpcionLikertDAOH extends AbstractEasyDAO<OpcionLikert> implements OpcionLikertDAO {

    public OpcionLikertDAOH() {
        super();
        setClazz(OpcionLikert.class);
    }

    @Override
    public List<OpcionLikert> allOpcionLikert() {
        Octavia sql = Octavia.query()
                .from(OpcionLikert.class, "ol")
                .join("tipoLikert tipo");
        return all(sql);
    }

}
