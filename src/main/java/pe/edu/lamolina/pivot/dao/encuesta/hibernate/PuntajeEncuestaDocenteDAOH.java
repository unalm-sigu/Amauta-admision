package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteDAO;

@Repository
public class PuntajeEncuestaDocenteDAOH extends AbstractEasyDAO<PuntajeEncuestaDocente> implements PuntajeEncuestaDocenteDAO {

    public PuntajeEncuestaDocenteDAOH() {
        super();
        setClazz(PuntajeEncuestaDocente.class);
    }

    @Override
    public List<PuntajeEncuestaDocente> allByEncuestaDocente(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .filter("ed.id", encuestaDocente)
                .orderBy("te.nombre");

        return all(sql);
    }

}
