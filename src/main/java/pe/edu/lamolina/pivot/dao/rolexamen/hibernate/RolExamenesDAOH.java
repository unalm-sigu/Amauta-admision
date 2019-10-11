package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
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
                .join("eventoCicloAcademico eca")
                .join("eca.eventoAcademico ea", "eca.cicloAcademico ca")
                .filter("rexa.id", id);
        return find(sql);
    }

    @Override
    public List<RolExamenes> allActiveByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(RolExamenes.class, "rexa")
                .join("eventoCicloAcademico eca")
                .join("eca.eventoAcademico ea", "eca.cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public List<RolExamenes> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RolExamenes.class, "re")
                .join("eventoCicloAcademico eca", "eca.cicloAcademico ca")
                //                .join("userRegistro ur", "ur.persona urp")
                .filter("ca.id", cicloAcademico)
                .searchFields("ca.descripcion", "re.nombre")
                .orderBy("re.id desc");
        return all(sql);
    }

    @Override
    public void updateRolExamenes(RolExamenes rolExamenes) {
        Octavia octavia = Octavia.update(RolExamenes.class);
        octavia.set(rolExamenes, "eventoCicloAcademico");
        octavia.set(rolExamenes, "nombre");
        this.update(octavia);
    }

    @Override
    public void updatePublicacion(RolExamenes rolExamenes) {
        Octavia octavia = Octavia.update(RolExamenes.class);
        octavia.set(rolExamenes, "estado");
        octavia.set(rolExamenes, "fechaPublicacion");
        this.update(octavia);
    }

    @Override
    public void updateSituacion(RolExamenes rolExamenes) {
        Octavia octavia = Octavia.update(RolExamenes.class);
        octavia.set(rolExamenes, "situacion");
        this.update(octavia);
    }

    @Override
    public void updateEstadoAndSituacion(RolExamenes rolExamenes) {
        Octavia octavia = Octavia.update(RolExamenes.class);
        octavia.set(rolExamenes, "situacion");
        octavia.set(rolExamenes, "estado");
        this.update(octavia);
    }

    @Override
    public RolExamenes findByEstadoCiclo(RolExamenesEstadoEnum rolExamenesEstadoEnum, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(RolExamenes.class, "rexa")
                .join("eventoCicloAcademico eca")
                .join("eca.eventoAcademico ea", "eca.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("rexa.estado", rolExamenesEstadoEnum);

        return find(sql);
    }

    @Override
    public RolExamenes findByCicloAndEstadoAndEventoAcademico(CicloAcademico cicloAcademico, EventoAcademicoEnum eventoAcademicoEnum) {
        Octavia sql = Octavia.query()
                .from(RolExamenes.class, "rexa")
                .join("eventoCicloAcademico eca", "userRegistro ur")
                .join("eca.eventoAcademico ea", "eca.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("ea.codigo", eventoAcademicoEnum);
        return find(sql);
    }

}
