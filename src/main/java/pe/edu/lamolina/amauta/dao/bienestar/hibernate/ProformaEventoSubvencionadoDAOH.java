package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.amauta.dao.bienestar.ProformaEventoSubvencionadoDAO;
import pe.edu.lamolina.model.bienestar.ProformaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

@Repository
public class ProformaEventoSubvencionadoDAOH extends AbstractEasyDAO<ProformaEventoSubvencionado> implements ProformaEventoSubvencionadoDAO {

    public ProformaEventoSubvencionadoDAOH() {
        super();
        setClazz(ProformaEventoSubvencionado.class);
    }

    @Override
    public List<ProformaEventoSubvencionado> allByViajeCurso(ViajeCurso viajeCurso) {
        Octavia sql = Octavia.query()
                .from(ProformaEventoSubvencionado.class, "pes")
                .join("viajeCurso vc")
                .filter("vc.id", viajeCurso)
                .orderBy("pes.orden");

        return all(sql);
    }

}
