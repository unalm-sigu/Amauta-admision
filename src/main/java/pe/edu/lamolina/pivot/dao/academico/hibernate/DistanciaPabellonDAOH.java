package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.DistanciaPabellonDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.enums.EstadoEnum;

@Repository
public class DistanciaPabellonDAOH extends AbstractEasyDAO<DistanciaPabellon> implements DistanciaPabellonDAO {

    public DistanciaPabellonDAOH() {
        super();
        setClazz(DistanciaPabellon.class);
    }

    @Override
    public List<DistanciaPabellon> allActivos() {
        Octavia sql = Octavia.query()
                .from(DistanciaPabellon.class, "dp")
                .join("departamentoAcademico da", "pabellon au")
                .filter("dp.estado", EstadoEnum.ACT)
                .orderBy("dp.nombre");

        return all(sql);
    }

    @Override
    public List<DistanciaPabellon> allFactorDistanciaByDepartamento(DepartamentoAcademico departamentoAcademico) {
        Octavia sql = Octavia.query()
                .from(DistanciaPabellon.class, "dp")
                .join("departamentoAcademico da", "pabellon au")
                .filter("da.id", departamentoAcademico)
                .orderBy("dp.id desc");

        return all(sql);
    }

    @Override
    public List<DistanciaPabellon> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DistanciaPabellon.class, "dpa")
                .join("departamentoAcademico da", "pabellon au")
                .searchFields("da.codigo", "au.codigo")
                .orderBy("dpa.id desc");
        return all(sql);
    }

}
