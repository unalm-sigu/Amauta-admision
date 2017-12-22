package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class HorarioAulaDAOH extends AbstractDAO<HorarioAula> implements HorarioAulaDAO {

    public HorarioAulaDAOH() {
        super();
        setClazz(HorarioAula.class);
    }

    @Override
    public List<HorarioAula> allHorarioAula() {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<HorarioAula> allByAula(Aula aula, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec", "sec.grupoSeccion gs")
                .join("gs.cicloAcademico ca")
                .filter("au.id", aula)
                .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<HorarioAula> allBySeccionAula(Seccion seccion, Aula aula) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .filter("au.id", aula)
                .filter("sec.id", seccion);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<HorarioAula> allByAulaCiclo(Aula aula, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico cic")
                .filter("au.estado", EstadoEnum.ACT.name())
                .filter("au.id", aula)
                .filter("cic.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteBySeccionAula(Seccion seccion, Aula aula) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("delete from HorarioAula ha where ha.seccion.id=:prm_seccion and ha.aula.id=:prm_aula ");

        Query query = getCurrentSession().createQuery(queryStr.toString());

        query.setParameter("prm_seccion", seccion.getId());
        query.setParameter("prm_aula", aula.getId());

        query.executeUpdate();
    }

    @Override
    public List<HorarioAula> allByAulaCicloDiasHoras(Aula aula, CicloAcademico cicloAcademico, List<Dia> dias, List<Hora> horas) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("au.id", aula)
                .filter("ca.id", cicloAcademico)
                .in("d.id", dias)
                .in("h.id", horas);
        return sql.all(getCurrentSession());
    }

    public List<HorarioAula> allByDiaHoraCiclo(Dia dia, Hora hora, CicloAcademico cicloAcademico) {
        
        return null;
    }

}
