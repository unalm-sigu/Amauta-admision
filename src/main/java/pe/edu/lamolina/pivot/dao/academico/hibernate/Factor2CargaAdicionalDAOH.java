package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Factor2CargaAdicional;
import pe.edu.lamolina.pivot.dao.academico.Factor2CargaAdicionalDAO;

@Repository
public class Factor2CargaAdicionalDAOH extends AbstractEasyDAO<Factor2CargaAdicional> implements Factor2CargaAdicionalDAO {

    public Factor2CargaAdicionalDAOH() {
        super();
        setClazz(Factor2CargaAdicional.class);
    }

    @Override
    public List<Factor2CargaAdicional> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(Factor2CargaAdicional.class, "fca")
                .join("fca.cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        
        return all(sql);
    }

    @Override
    public List<Factor2CargaAdicional> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Factor2CargaAdicional.class, "fca")
                .join("fca.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .orderBy("fca.cantidadInicio");

        return all(sql);
    }

}
