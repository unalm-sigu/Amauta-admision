package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCicloDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaCiclo;

@Repository
public class EncuestaCicloDAOH extends AbstractEasyDAO<EncuestaCiclo> implements EncuestaCicloDAO {

    public EncuestaCicloDAOH() {
        super();
        setClazz(EncuestaCiclo.class);
    }

    @Override
    public EncuestaCiclo findByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EncuestaCiclo.class, "ec")
                .join("cicloPostula cp", "examenVirtual ev", "cp.cicloAcademico ca")
                .filter("cp.id", ciclo);

        return (EncuestaCiclo) sql.find(getCurrentSession());
    }

    @Override
    public List<EncuestaCiclo> allByEncuestas(List<ExamenVirtual> encuestas) {
        Octavia sql = Octavia.query()
                .from(EncuestaCiclo.class, "ec")
                .join("cicloPostula cp", "examenVirtual ev", "cp.cicloAcademico ca")
                .in("ev.id", encuestas);

        return sql.all(getCurrentSession());
    }

}
