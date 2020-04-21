package pe.edu.lamolina.amauta.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.horario.SeccionHorarioCachimbosDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;

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
                .join("horarioCachimbos hc", "hc.cicloAcademico ciclo", "hc.carrera car", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .leftJoin("sec.grupoHoras")
                .filter("car.id", carrera)
                .filter("ci.id", cicloAcademico)
                .filter("ciclo.id", cicloAcademico)
                .in("cur.id", cursos);

        return all(sql);
    }

    @Override
    public List<SeccionHorarioCachimbos> allByHorario(HorarioCachimbos horario) {
        Octavia sql = Octavia.query()
                .from(SeccionHorarioCachimbos.class, "shc")
                .join("horarioCachimbos hc", "hc.cicloAcademico ciclo", "hc.carrera car", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .leftJoin("sec.grupoHoras", "sec.aula", "gs.anexoBoletin")
                .filter("hc.id", horario);

        return all(sql);
    }

    @Override
    public List<SeccionHorarioCachimbos> allByHorarios(List<HorarioCachimbos> horarios) {
        Octavia sql = Octavia.query()
                .from(SeccionHorarioCachimbos.class, "shc")
                .join("horarioCachimbos hc", "hc.cicloAcademico ciclo", "hc.carrera car", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .in("hc.id", horarios);

        return all(sql);
    }

    @Override
    public List<SeccionHorarioCachimbos> allByCursoCiclo(CicloAcademico cicloAcademico, List<Curso> cursos) {
        Octavia sql = Octavia.query()
                .from(SeccionHorarioCachimbos.class, "shc")
                .join("horarioCachimbos hc", "hc.cicloAcademico ciclo", "hc.carrera car", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .filter("ci.id", cicloAcademico)
                .filter("ciclo.id", cicloAcademico)
                .in("cur.id", cursos);

        return all(sql);
    }

    @Override
    public List<SeccionHorarioCachimbos> allBySeccions(CicloAcademico cicloAcademico, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(SeccionHorarioCachimbos.class, "shc")
                .join("horarioCachimbos hc", "hc.cicloAcademico ciclo", "hc.carrera car", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ci")
                .filter("ci.id", cicloAcademico)
                .filter("ciclo.id", cicloAcademico)
                .in("sec.id", secciones);

        return all(sql);
    }

    @Override
    public void deleteByHorarioCachimbos(HorarioCachimbos horarioCachimbos) {

        StringBuilder sql = new StringBuilder();
        sql.append("  delete from ").append(SeccionHorarioCachimbos.class.getName()).append(" shc ");
        sql.append("  where shc.horarioCachimbos.id = :HORARIO ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("HORARIO", horarioCachimbos.getId());
        query.executeUpdate();

    }

    @Override
    public void deleteAllByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder sql = new StringBuilder();
        sql.append("  delete from ").append(SeccionHorarioCachimbos.class.getName()).append(" shc ");
        sql.append("  where shc.horarioCachimbos.id in ( ");
        sql.append("    select hc.id  from ").append(HorarioCachimbos.class.getName()).append(" hc ");
        sql.append("    where hc.cicloAcademico.id = :CICLO ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

}
