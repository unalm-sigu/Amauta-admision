package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.model.enums.TipoPreguntaEncuestaEnum;
import pe.edu.lamolina.pivot.dao.encuesta.ResumenEncuestaDocenteDAO;

@Repository
public class ResumenEncuestaDocenteDAOH extends AbstractEasyDAO<ResumenEncuestaDocente> implements ResumenEncuestaDocenteDAO {

    public ResumenEncuestaDocenteDAOH() {
        super();
        setClazz(ResumenEncuestaDocente.class);
    }

    @Override
    public List<ResumenEncuestaDocente> allByEncuestaDocente(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query(ResumenEncuestaDocente.class, "red")
                .join("red.encuestaDocente ed", "pregunta p")
                .filter("ed.id", encuestaDocente)
                .filter("p.tipo", TipoPreguntaEncuestaEnum.LIKERT);

        return all(sql);
    }

}
