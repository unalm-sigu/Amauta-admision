package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ItemInformeFinalTutoriaDAO;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;
import pe.edu.lamolina.model.tutoria.ItemInformeFinalTutoria;

@Repository
public class ItemInformeFinalTutoriaDAOH extends AbstractEasyDAO<ItemInformeFinalTutoria> implements ItemInformeFinalTutoriaDAO {

    public ItemInformeFinalTutoriaDAOH() {
        super();
        setClazz(ItemInformeFinalTutoria.class);
    }

    @Override
    public List<ItemInformeFinalTutoria> allByInforme(InformeFinalTutoria informe) {
        Octavia sql = Octavia.query()
                .from(ItemInformeFinalTutoria.class, "iif")
                .join("informeFinalTutoria inf", "parteInformeTutoria pa")
                .filter("inf.id", informe)
                .orderBy("iif.orden");

        return all(sql);
    }

}
