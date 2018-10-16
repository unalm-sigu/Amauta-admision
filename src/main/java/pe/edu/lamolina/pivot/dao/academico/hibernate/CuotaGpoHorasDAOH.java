package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotaGpoHoras;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;

@Repository
public class CuotaGpoHorasDAOH extends AbstractEasyDAO<CuotaGpoHoras> implements CuotaGpoHorasDAO{
   
    public CuotaGpoHorasDAOH() {
        super();
        setClazz(CuotaGpoHoras.class);
    }

    @Override
    public List<CuotaGpoHoras> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico ) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CuotaGpoHoras.class, "cgpo")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .join("ab.nombre", "ab.codigo", "ab.estado", "gh.codigo", "gh.letra", "ca.descripcion")
                .filter("ca.id", cicloAcademico)
                .searchFields("ab.nombre", "gh.codigo", "ca.descripcion")
                .orderBy("cgpo.id desc");
        return sql.all(getCurrentSession());
    }
    
}
