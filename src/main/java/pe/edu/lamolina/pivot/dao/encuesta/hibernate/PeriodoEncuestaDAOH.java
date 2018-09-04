package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;

@Repository
public class PeriodoEncuestaDAOH extends AbstractEasyDAO<PeriodoEncuesta> implements PeriodoEncuestaDAO {

    public PeriodoEncuestaDAOH() {
        super();
        setClazz(PeriodoEncuesta.class);
    }

    @Override
    public List<PeriodoEncuesta> allByEncuesta(EncuestaEstudiantil encuestaEstudiantil) {
        Octavia sql = Octavia.query()
                .from(PeriodoEncuesta.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .filter("ee.id", encuestaEstudiantil);
        return all(sql);
    }

}
