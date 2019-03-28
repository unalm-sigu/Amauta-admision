package pe.edu.lamolina.pivot.dao.sip.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.pivot.dao.sip.TurnoEntrevistaObuaeDAO;

@Repository
public class TurnoEntrevistaObuaeDAOH extends AbstractEasyDAO<TurnoEntrevistaObuae> implements TurnoEntrevistaObuaeDAO {

    public TurnoEntrevistaObuaeDAOH() {
        super();
        setClazz(TurnoEntrevistaObuae.class);
    }

    @Override
    public List<TurnoEntrevistaObuae> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(TurnoEntrevistaObuae.class, "teo")
                .join("eventoCiclo ec", "ec.cicloPostula cp", "cp.cicloAcademico ca")
                .filter("ca.id", ciclo)
                .orderBy("teo.fecha");

        return all(sql);
    }

}
