package pe.edu.lamolina.pivot.dao.aporte.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteCiclo;
import pe.edu.lamolina.pivot.dao.aporte.AporteCicloDAO;

@Repository
public class AporteCicloDAOH extends AbstractEasyDAO<AporteCiclo> implements AporteCicloDAO {

    public AporteCicloDAOH() {
        super();
        setClazz(AporteCiclo.class);
    }

    @Override
    public List<AporteCiclo> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AporteCiclo.class, "ac")
                .join("cicloAcademico ca", "aporte ap", "cuentaBancaria cb")
                .filter("ca.id", cicloAcademico)
                .orderBy("ap.nombre asc");

        return all(sql);
    }

    @Override
    public List<AporteCiclo> allByCicloAcademicoModalidadEstudio(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio) {
        Octavia sql = Octavia.query()
                .from(AporteCiclo.class, "ac")
                .join("cicloAcademico ca", "aporte ap", "cuentaBancaria cb")
                .filter("ca.codigo", cicloAcademico.getCodigo())
                .filter("ca.modalidadEstudio", modalidadEstudio)
                .orderBy("ap.nombre asc");

        return all(sql);
    }

    @Override
    public List<AporteCiclo> allByCicloAcademicoAporte(CicloAcademico cicloAcademico, Aporte aporte) {
        Octavia sql = Octavia.query()
                .from(AporteCiclo.class, "ac")
                .join("cicloAcademico ca", "aporte ap", "cuentaBancaria cb")
                .filter("ca.codigo", cicloAcademico.getCodigo())
                .filter("ap.id", aporte);

        return all(sql);
    }

    @Override
    public AporteCiclo findByCicloAcademicoAporte(CicloAcademico cicloAcademico, Aporte aporte) {
        Octavia sql = Octavia.query()
                .from(AporteCiclo.class, "ac")
                .join("cicloAcademico ca", "aporte ap", "cuentaBancaria cb")
                .filter("ca.codigo", cicloAcademico.getCodigo())
                .filter("ap.id", aporte);

        return find(sql);
    }
}
