package pe.edu.lamolina.pivot.dao.sip.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.pivot.dao.sip.TurnoEntrevistaObuaeDAO;

@Repository
public class TurnoEntrevistaObuaeDAOH extends AbstractEasyDAO<TurnoEntrevistaObuae> implements TurnoEntrevistaObuaeDAO {

    public TurnoEntrevistaObuaeDAOH() {
        super();
        setClazz(TurnoEntrevistaObuae.class);
    }

    @Override
    public List<TurnoEntrevistaObuae> allFromRecorridoByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .selectDistinct("teo")
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ca", "turnoEntrevistaObuae teo")
                .filter("ca.id", ciclo);

        return all(sql);
    }

}
