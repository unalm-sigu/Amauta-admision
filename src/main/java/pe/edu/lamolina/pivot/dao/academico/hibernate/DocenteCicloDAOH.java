package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteCiclo;
import pe.edu.lamolina.pivot.dao.academico.DocenteCicloDAO;

@Repository
public class DocenteCicloDAOH  extends AbstractEasyDAO<DocenteCiclo> implements DocenteCicloDAO {

    public DocenteCicloDAOH() {
        super();
        setClazz(DocenteCiclo.class);
    }

    @Override
    public List<DocenteCiclo> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DocenteCiclo.class, "dc")
                .join("dc.cicloAcademico ca", "docente doc", "doc.persona per", "per.tipoDocumento tdoc", "doc.departamentoAcademico")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public void deshacerCarga(CicloAcademico cicloAcademico) {
        Query query = getCurrentSession().createQuery("delete from DocenteCiclo where cicloAcademico.id = :CICLO");
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public void deshacerMontos(CicloAcademico cicloAcademico) {
        Query query = getCurrentSession().createQuery("update DocenteCiclo set monto = null where cicloAcademico.id = :CICLO");
        query.setParameter("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public void generarMontos(CicloAcademico cicloAcademico, BigDecimal rca) {
        Query query = getCurrentSession().createQuery("update DocenteCiclo set monto =  factor1 * factor2 * creditosExceso * :RCA * 4 where cicloAcademico.id = :CICLO");
        query.setParameter("CICLO", cicloAcademico.getId());
        query.setParameter("RCA", rca);
        query.executeUpdate();
    }

}
