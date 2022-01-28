package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaHistorialDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;

@Service
public class ConsejeriaHistorialDAOH extends AbstractEasyDAO<ConsejeriaHistorial> implements ConsejeriaHistorialDAO {

    public ConsejeriaHistorialDAOH() {
        super();
        setClazz(ConsejeriaHistorial.class);
    }

    @Override
    public List<ConsejeriaHistorial> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {

        DynatableSql sql = new DynatableSql(filter)
                .from(ConsejeriaHistorial.class, "ac")
                .join("cicloAcademico ca")
                .searchFields("ca.descripcion")
                .filter("ca.id", cicloAcademico)
                .orderBy("ac.fechaCreacion desc");
        return all(sql);
    }

}
