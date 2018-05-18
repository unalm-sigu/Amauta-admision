package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;

@Repository
public class HorarioAulaDAOH extends AbstractEasyDAO<HorarioAula> implements HorarioAulaDAO {

    public HorarioAulaDAOH() {
        super();
        setClazz(HorarioAula.class);
    }

    @Override
    public List<HorarioAula> allHorarioAula() {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha");

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByAula(Aula aula, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec", "sec.grupoSeccion gs")
                .left("sec.grupoHoras gh")
                .join("gs.cicloAcademico ca")
                .filter("au.id", aula)
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allBySeccionAula(Seccion seccion, Aula aula) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .filter("au.id", aula)
                .filter("sec.id", seccion);

        return all(sql);
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

        return all(sql);
    }

    @Override
    public List<HorarioAula> allBySeccionCiclo(Seccion seccion, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia", "hora", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico cic")
                .filter("au.estado", EstadoEnum.ACT.name())
                .filter("sec.id", seccion)
                .filter("cic.id", cicloAcademico);

        return all(sql);
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
    public void deleteBySeccionDiaHoraAula(Seccion seccion, Dia dia, Hora hora, Aula aula) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("delete from HorarioAula ha where ha.seccion.id=:prm_seccion and ha.aula.id=:prm_aula ");
        queryStr.append(" and ha.dia.id=:prm_dia ");
        queryStr.append(" and ha.hora.id=:prm_hora ");

        Query query = getCurrentSession().createQuery(queryStr.toString());

        query.setParameter("prm_seccion", seccion.getId());
        query.setParameter("prm_aula", aula.getId());
        query.setParameter("prm_dia", dia.getId());
        query.setParameter("prm_hora", hora.getId());

        query.executeUpdate();
    }

    @Override
    public List<HorarioAula> allByPabellonCicloDiasHoras(Aula pabellon, CicloAcademico cicloAcademico, List<String> hdias) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "au.aulaSuperior aus", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("aus.id", pabellon)
                .filter("ca.id", cicloAcademico)
                .complexFilter("concat(h.codigo,'-',d.id)", "in", hdias);

        return all(sql);
    }

    @Override
    public List<HorarioAula> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(HorarioAula.class, "ha")
                .join("dia d", "hora h", "aula au", "seccion sec")
                .join("sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public void deleteAllInList(List<HorarioAula> horarios) {
        if (horarios.isEmpty()) {
            return;
        }

        String sql = "delete HorarioAula hs where hs in :HORARIOS";
        Query query = getCurrentSession().createQuery(sql);
        query.setParameterList("HORARIOS", horarios);
        query.executeUpdate();
    }

}
