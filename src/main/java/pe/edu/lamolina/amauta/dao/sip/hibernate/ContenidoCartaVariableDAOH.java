package pe.edu.lamolina.amauta.dao.sip.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.inscripcion.ContenidoCartaVariable;
import pe.edu.lamolina.amauta.dao.sip.ContenidoCartaVariableDAO;

@Repository
public class ContenidoCartaVariableDAOH extends AbstractEasyDAO<ContenidoCartaVariable> implements ContenidoCartaVariableDAO {

    public ContenidoCartaVariableDAOH() {
        super();
        setClazz(ContenidoCartaVariable.class);
    }

    @Override
    public List<ContenidoCartaVariable> allByIdContenido(Long idContenido) {
        Octavia sql = Octavia.query()
                .from(ContenidoCartaVariable.class, "ccv")
                .join("contenidoCarta cc", "contenidoVariable")
                .filter("cc.id", idContenido);
        return all(sql);
    }

}
