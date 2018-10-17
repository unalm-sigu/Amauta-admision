package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;

@Repository
public class CuotaGpoHorasDAOH extends AbstractEasyDAO<CuotasGrupoHoras> implements CuotaGpoHorasDAO{
   
    public CuotaGpoHorasDAOH() {
        super();
        setClazz(CuotasGrupoHoras.class);
    }

    @Override
    public List<CuotasGrupoHoras> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico ) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CuotasGrupoHoras.class, "cgpo")
                .join("anexoBoletin ab", "grupoHoras gh", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .searchFields("ab.nombre", "gh.codigo", "ca.descripcion")
                .orderBy("cgpo.id desc");
        return all(sql);
    }
    
}
