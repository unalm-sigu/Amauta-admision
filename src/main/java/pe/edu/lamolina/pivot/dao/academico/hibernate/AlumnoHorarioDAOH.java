package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

@Repository
public class AlumnoHorarioDAOH extends AbstractEasyDAO<AlumnoHorario> implements AlumnoHorarioDAO {

    public AlumnoHorarioDAOH() {
        super();
        setClazz(AlumnoHorario.class);
    }

    @Override
    public List<AlumnoHorario> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoHorario.class, "ah")
                .join("cicloAcademico ciclo ", "alumno alu")
                .leftJoin("horarioCachimbos hoca")
                .filter("ciclo.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }
}
