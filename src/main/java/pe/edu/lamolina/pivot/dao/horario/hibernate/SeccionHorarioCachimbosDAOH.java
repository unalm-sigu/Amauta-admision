package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;

@Repository
public class SeccionHorarioCachimbosDAOH extends AbstractEasyDAO<SeccionHorarioCachimbos> implements SeccionHorarioCachimbosDAO {

    public SeccionHorarioCachimbosDAOH() {
        super();
        setClazz(SeccionHorarioCachimbos.class);
    }

    @Override
    public List<SeccionHorarioCachimbos> allByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(SeccionHorarioCachimbos.class, "shc")
                .join("horarioCachimbos hc", "hc.cicloAcademico ciclo", "hc.carrera car", "seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .filter("car.id", carrera)
                .filter("ci.id", cicloAcademico)
                .filter("ciclo.id", cicloAcademico)
                .in("cur.id", cursos);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<SeccionHorarioCachimbos> allByHorario(HorarioCachimbos horario) {
        Octavia sql = Octavia.query()
                .from(SeccionHorarioCachimbos.class, "shc")
                .join("horarioCachimbos hc", "hc.cicloAcademico ciclo", "hc.carrera car", "seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .filter("hc.id", horario);
        return sql.all(getCurrentSession());
    }
}
