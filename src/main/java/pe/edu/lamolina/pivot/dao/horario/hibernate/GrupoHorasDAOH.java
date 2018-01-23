package pe.edu.lamolina.pivot.dao.horario.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;

@Repository
public class GrupoHorasDAOH extends AbstractEasyDAO<GrupoHoras> implements GrupoHorasDAO {
    
    public GrupoHorasDAOH() {
        super();
        setClazz(GrupoHoras.class);
    }
    
    @Override
    public GrupoHoras findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "gh")
                .filter("gh.codigo", codigo);
        
        return find(sql);
    }
    
    @Override
    public GrupoHoras findGrupoHorasByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "grup")
                .filter("codigo", codigo);
        
        return find(sql);
    }
    
    @Override
    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter, Long idTipoGrupo) {
        
        DynatableSql sql = new DynatableSql(filter)
                .from(GrupoHoras.class, "gh")
                .leftJoin("tipoGrupoHoras tgh")
                .searchFields("codigo", "letra", "tipoCiclo", "tipoSeccion", "color")
                .filter("tgh.id", idTipoGrupo)
                .orderBy("gh.id desc");
        
        return all(sql);
    }
    
    @Override
    public GrupoHoras find(GrupoHoras grupoHoras) {
        Octavia sql = Octavia.query()
                .from(GrupoHoras.class, "grup")
                .leftJoin("diaHoraGrupo dhg")
                .leftJoin("tipoGrupoHoras tgh")
                .filter("grup.id", grupoHoras);
        
        return find(sql);
    }
    
    @Override
    public List<GrupoHoras> allByTipoGrupoHora(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("gh")
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ca")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }
    
    @Override
    public List<GrupoHoras> allByTipoGrupoHoraDyna(DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico,
            Seccion seccion) {
        /*
        StringBuilder strb = new StringBuilder();
        strb.append(" Select gh.id, count(dhg) from GrupoHoras gh ");
        strb.append(" join gh.diaHoraGrupo dhg ");
        strb.append(" join gh.tipoGrupoHoras tgh ");
        strb.append(" join dhg.cicloAcademico ca ");
        strb.append(" where ");
        strb.append(" tgh.id=:prm_tipo_grupo_hora ");
        strb.append(" and  ca.id=:prm_ciclo ");
        strb.append(" group by  gh.id  ");
        strb.append(" having ount(dhg)>:prm_cant_horas ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_tipo_grupo_hora", tipoGrupoHoras.getId());
        query.setParameter("prm_ciclo", cicloAcademico.getId());
        query.setParameter("prm_cant_horas", seccion.getHorasSemanales());
        List<GrupoHoras> grupoHoras = query.list();
         */
        DynatableSql sql = new DynatableSql(filter)
                .selectDistinct("gh")
                .from(DiaHoraGrupo.class, "dhg")
                .join("grupoHorario gh", "gh.tipoGrupoHoras tgh", "cicloAcademico ca")
                .filter("tgh.id", tipoGrupoHoras)
                .filter("ca.id", cicloAcademico)
                .searchFields("gh.codigo");
        
        return sql.all(getCurrentSession());
    }
    
    @Override
    public List<GrupoHoras> allZetasByDynatable(DynatableFilter filter, TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        
        DynatableSql sql = new DynatableSql(filter)
                // .selectDistinct("gh")
                .from(GrupoHoras.class, "gh")
                .join("gh.tipoGrupoHoras tgh")
                .filter("tgh.id", tipoGrupoHoras);
        // .filter("ca.id", cicloAcademico);
        return sql.all(getCurrentSession());
    }
    
}
