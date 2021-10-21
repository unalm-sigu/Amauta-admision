package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.amauta.dao.bienestar.CronogramaEventoSubvencionadoDAO;
import pe.edu.lamolina.model.bienestar.CronogramaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ViajeCurso;

@Repository
public class CronogramaEventoSubvencionadoDAOH extends AbstractEasyDAO<CronogramaEventoSubvencionado> implements CronogramaEventoSubvencionadoDAO {

    public CronogramaEventoSubvencionadoDAOH() {
        super();
        setClazz(CronogramaEventoSubvencionado.class);
    }

    @Override
    public List<CronogramaEventoSubvencionado> allByViajeCurso(ViajeCurso viajeCurso) {
        Octavia sql = Octavia.query()
                .from(CronogramaEventoSubvencionado.class, "ces")
                .join("viajeCurso vc")
                .filter("vc.id", viajeCurso)
                .orderBy("ces.orden");

        return all(sql);
    }

}
