package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;

@Repository
public class HorarioSeccionDAOH extends AbstractEasyDAO<HorarioSeccion> implements HorarioSeccionDAO {

    public HorarioSeccionDAOH() {
        super();
        setClazz(HorarioSeccion.class);
    }

    @Override
    public List<HorarioSeccion> allBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .leftJoin("sec.aula", "sec.grupoHoras")
                .in("sec.id", secciones);

        return all(sql);
    }

    @Override
    public List<HorarioSeccion> allBySeccionesSortByDiaHora(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec", "sec.grupoSeccion gs", "gs.curso")
                .leftJoin("sec.aula", "sec.grupoHoras")
                .orderBy("di.numeroDia", "ho.numero")
                .in("sec.id", secciones);
        return all(sql);
    }

    @Override
    public List<HorarioSeccion> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec")
                .leftJoin("aula")
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<HorarioSeccion> allBySeccionDia(Seccion seccion, Dia dia) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec")
                .leftJoin("aula")
                .filter("sec.id", seccion)
                .filter("di.id", dia);
        return all(sql);
    }

    @Override
    public List<HorarioSeccion> allByCicloCurso(CicloAcademico cicloAcademico, List<Curso> cursos) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec", "sec.grupoSeccion gru", "gru.cicloAcademico ciclo", "gru.curso cu")
                .filter("ciclo.id", cicloAcademico)
                .in("cu.id", cursos);

        return all(sql);
    }

    @Override
    public void deleteAllByNotInList(List<HorarioSeccion> horarios) {
        StringBuilder sql = new StringBuilder();

        if (horarios.isEmpty()) {
            sql.append("delete HorarioSeccion");
        } else {
            sql.append("delete HorarioSeccion hs where hs not in :HORARIOS");
        }

        Query query = getCurrentSession().createQuery(sql.toString());

        if (!horarios.isEmpty()) {
            query.setParameterList("HORARIOS", horarios);
        }

        query.executeUpdate();
    }

    @Override
    public void deleteAllInList(List<HorarioSeccion> horarios) {
        if (horarios.isEmpty()) {
            return;
        }

        String sql = "delete HorarioSeccion hs where hs in :HORARIOS";
        Query query = getCurrentSession().createQuery(sql);
        query.setParameterList("HORARIOS", horarios);
        query.executeUpdate();
    }

    public HorarioSeccion findBySeccionDiaHora(Seccion seccion, Dia dia, Hora hora) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec", "sec.grupoSeccion gru", "gru.cicloAcademico ciclo", "gru.curso cu")
                .filter("seccion", seccion)
                .filter("dia", dia)
                .filter("hora", hora);

        return (HorarioSeccion) sql.find(getCurrentSession());
    }

    @Override
    public List<HorarioSeccion> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioSeccion.class, "hs")
                .join("dia di", "hora ho", "seccion sec", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .leftJoin("aula")
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {
        StringBuilder sql = new StringBuilder("")
                .append(" DELETE ").append(HorarioSeccion.class.getSimpleName()).append(" hs ")
                .append(" WHERE EXISTS ")
                .append(" ( ")
                .append("   SELECT 1 FROM ").append(Seccion.class.getName()).append(" sec ")
                .append("     JOIN sec.grupoSeccion gs ")
                .append("     JOIN gs.cicloAcademico ci ")
                .append("    WHERE ci.id = :CICLO ")
                .append("      AND hs.seccion.id = sec.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}
