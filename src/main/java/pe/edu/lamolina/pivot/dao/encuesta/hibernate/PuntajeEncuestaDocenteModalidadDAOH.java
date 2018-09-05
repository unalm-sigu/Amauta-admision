package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.pivot.dao.encuesta.PuntajeEncuestaDocenteModalidadDAO;

@Repository
public class PuntajeEncuestaDocenteModalidadDAOH extends AbstractEasyDAO<PuntajeEncuestaDocenteModalidad> implements PuntajeEncuestaDocenteModalidadDAO {

    public PuntajeEncuestaDocenteModalidadDAOH() {
        super();
        setClazz(PuntajeEncuestaDocenteModalidad.class);
    }

    @Override
    public List<PuntajeEncuestaDocenteModalidad> allByEncuestaDocenteModalidad(EncuestaDocenteModalidad encuestaDocenteModalidad) {
        Octavia sql = Octavia.query()
                .from(PuntajeEncuestaDocenteModalidad.class, "pedm")
                .join("encuestaDocenteModalidad edm", "temaEncuesta te")
                .filter("edm.id", encuestaDocenteModalidad)
                .orderBy("te.nombre");
        
        return all(sql);
    }

}
