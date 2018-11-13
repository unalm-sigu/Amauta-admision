package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;

@Repository
public class RolExamenesDAOH extends AbstractEasyDAO<RolExamenes> implements RolExamenesDAO {

    public RolExamenesDAOH() {
        super();
        setClazz(RolExamenes.class);
    }

    @Override
    public RolExamenes find(long id) {
        Octavia sql = Octavia.query()
                .from(RolExamenes.class, "rexa")
                .join("eventoCicloAcademico eca", "userRegistro ur")
                .join("eca.eventoAcademico ea", "eca.cicloAcademico ca")
                .filter("rexa.id", id);
        return find(sql);
    }

    @Override
    public List<RolExamenes> allActiveByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(RolExamenes.class, "rexa")
                .join("eventoCicloAcademico eca", "userRegistro ur")
                .join("eca.eventoAcademico ea", "eca.cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public List<RolExamenes> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RolExamenes.class, "re")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .searchFields("ca.descripcion")
                .orderBy("re.id desc");
        return all(sql);
    }

}
