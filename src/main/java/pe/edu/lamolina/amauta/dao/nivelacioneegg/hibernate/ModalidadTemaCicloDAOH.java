package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ModalidadTemaCicloDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.ModalidadTemaCiclo;

@Repository
public class ModalidadTemaCicloDAOH extends AbstractEasyDAO<ModalidadTemaCiclo> implements ModalidadTemaCicloDAO {

    public ModalidadTemaCicloDAOH() {
        super();
        setClazz(ModalidadTemaCiclo.class);
    }

    @Override
    public ModalidadTemaCiclo find(long id) {
        Octavia sql = Octavia.query()
                .from(ModalidadTemaCiclo.class, "mtc")
                .join("temaCiclo tc", "tc.temaExamen te")
                .join("tc.cicloPostula cp", "cp.cicloAcademico ci")
                .leftJoin("modalidadIngreso mi")
                .filter("mtc.id", id);

        return find(sql);
    }

    @Override
    public List<ModalidadTemaCiclo> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ModalidadTemaCiclo.class, "mtc")
                .join("temaExamen te", "cicloAcademico ci")
                .leftJoin("temaCiclo tc", "tc.temaExamen", "te.temaSuperior")
                .leftJoin("modalidadIngreso mi")
                .searchFields("te.codigo", "te.nombre")
                .filter("ci.id", ciclo)
                .orderBy("mtc.otrasModalidades", "tc.orden");

        return all(sql);
    }

    @Override
    public List<ModalidadTemaCiclo> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(ModalidadTemaCiclo.class, "mtc")
                .join("temaCiclo tc", "tc.temaExamen te")
                .join("tc.cicloPostula cp", "cp.cicloAcademico ci")
                .leftJoin("modalidadIngreso mi")
                .filter("ci.id", ciclo);

        return all(sql);
    }

}
