package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.amauta.dao.tramite.VariableGenericaDAO;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.OERA;

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

    @Override
    public List<VariableGenerica> allByPregrado() {
        Octavia sql = Octavia.query()
                .from(VariableGenerica.class, "vg")
                .join("oficina ofi")
                .filter("ofi.codigo", OERA);
        return all(sql);
    }

    @Override
    public List<VariableGenerica> allByPregradoByCodigoEnum(List<String> codigos) {

        Octavia sql = Octavia.query()
                .from(VariableGenerica.class, "vg")
                .join("oficina ofi")
                .in("vg.codigoEnum",codigos)
                .filter("ofi.codigo", OERA);
        return all(sql);

    }

}
