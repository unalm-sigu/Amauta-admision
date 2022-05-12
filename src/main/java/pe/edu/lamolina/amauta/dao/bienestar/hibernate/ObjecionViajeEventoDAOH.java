package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.bienestar.ObjecionViajeEventoDAO;
import pe.edu.lamolina.model.bienestar.ObjecionViajeEvento;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

@Repository
public class ObjecionViajeEventoDAOH extends AbstractEasyDAO<ObjecionViajeEvento> implements ObjecionViajeEventoDAO {

    public ObjecionViajeEventoDAOH() {
        super();
        setClazz(ObjecionViajeEvento.class);
    }

    @Override
    public ObjecionViajeEvento find(long id) {
        Octavia sql = Octavia.query()
                .from(ObjecionViajeEvento.class, "ove")
                .leftJoin("viajeCurso", "eventoAgrupacion", "objecionOrigen")
                .filter("id", id);

        return find(sql);
    }

    @Override
    public List<ObjecionViajeEvento> allByViaje(ViajeCurso viaje) {
        Octavia sql = Octavia.query()
                .from(ObjecionViajeEvento.class, "ovc")
                .join("viajeCurso vc")
                .filter("vc.id", viaje)
                .orderBy("ovc.id");

        return all(sql);
    }

}
