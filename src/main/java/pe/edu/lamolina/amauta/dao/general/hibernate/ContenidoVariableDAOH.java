package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.inscripcion.ContenidoCartaVariable;
import pe.edu.lamolina.model.inscripcion.ContenidoVariable;
import pe.edu.lamolina.amauta.dao.general.ContenidoVariableDAO;

@Repository
public class ContenidoVariableDAOH extends AbstractEasyDAO<ContenidoVariable> implements ContenidoVariableDAO {

    public ContenidoVariableDAOH() {
        super();
        setClazz(ContenidoVariable.class);
    }

    @Override
    public List<ContenidoVariable> allByContenidoId(Long idContenido) {
        Octavia sql = Octavia.query()
                .select("cv")
                .from(ContenidoCartaVariable.class, "ccv")
                .join("contenidoCarta cc", "contenidoVariable cv")
                .filter("cc.id", idContenido);

        return sql.all(getCurrentSession());
    }

}
