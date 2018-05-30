package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.pivot.dao.tramite.VariableGenericaDAO;

@Repository
public class VariableGenericaDAOH extends AbstractEasyDAO<VariableGenerica> implements VariableGenericaDAO {

    public VariableGenericaDAOH() {
        super();
        setClazz(VariableGenerica.class);
    }

    @Override
    public List<VariableGenerica> allByCodigo(List<String> listVariable) {
        Octavia sql = Octavia.query()
                .from(VariableGenerica.class, "vg")
                .in("vg.codigo", listVariable);
        return all(sql);
    }

}
