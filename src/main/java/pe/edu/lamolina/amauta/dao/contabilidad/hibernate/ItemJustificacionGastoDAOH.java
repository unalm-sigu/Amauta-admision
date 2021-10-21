package pe.edu.lamolina.amauta.dao.contabilidad.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.contabilidad.ItemJustificacionGastoDAO;
import pe.edu.lamolina.model.contabilidad.ItemJustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;

@Repository
public class ItemJustificacionGastoDAOH extends AbstractEasyDAO<ItemJustificacionGasto> implements ItemJustificacionGastoDAO {

    public ItemJustificacionGastoDAOH() {
        super();
        setClazz(ItemJustificacionGasto.class);
    }

    @Override
    public ItemJustificacionGasto find(long id) {
        Octavia sql = Octavia.query()
                .from(ItemJustificacionGasto.class, "ijg")
                .join("justificacionGasto jg", "jg.viajeCurso vc", "vc.alumnoDelegado")
                .filter("id", id);

        return find(sql);
    }

    @Override
    public List<ItemJustificacionGasto> allByJustificacion(JustificacionGasto justificacion) {
        Octavia sql = Octavia.query()
                .from(ItemJustificacionGasto.class, "ijg")
                .join("justificacionGasto jg", "jg.viajeCurso vc")
                .leftJoin("factura", "colaboradorVoBo")
                .filter("jg.id", justificacion)
                .orderBy("ijg.id");

        return all(sql);
    }

}
