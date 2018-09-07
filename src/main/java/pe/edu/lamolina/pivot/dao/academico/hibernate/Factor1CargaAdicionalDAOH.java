package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Factor1CargaAdicional;
import pe.edu.lamolina.pivot.dao.academico.Factor1CargaAdicionalDAO;

@Repository
public class Factor1CargaAdicionalDAOH extends AbstractEasyDAO<Factor1CargaAdicional> implements Factor1CargaAdicionalDAO {

    public Factor1CargaAdicionalDAOH() {
        super();
        setClazz(Factor1CargaAdicional.class);
    }

    @Override
    public List<Factor1CargaAdicional> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(Factor1CargaAdicional.class, "fca")
                .join("fca.cicloAcademico ca", "fca.situacionDocente sd", "fca.categoriaDocente cd")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<Factor1CargaAdicional> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Factor1CargaAdicional.class, "fca")
                .join("fca.cicloAcademico ca", "fca.situacionDocente sd", "fca.categoriaDocente cd")
                .searchFields("sd.nombre", "sd.codigo", "cd.nombre", "cd.codigo")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

}
