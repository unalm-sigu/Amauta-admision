package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AmpliacionVacante;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.AmpliacionVacanteDAO;

@Repository
public class AmpliacionVacanteDAOH extends AbstractEasyDAO<AmpliacionVacante> implements AmpliacionVacanteDAO {

    public AmpliacionVacanteDAOH() {
        super();
        setClazz(AmpliacionVacante.class);
    }

    @Override
    public List<AmpliacionVacante> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(AmpliacionVacante.class, "av")
                .join("seccion se","colaborador co","co.cargo ca","oficina ofi")
                .filter("se.id", seccion)
                .orderBy("av.fechaSolicitud desc");
        return all(sql);
    }

    @Override
    public AmpliacionVacante find(AmpliacionVacante ampliacionVacante) {
        Octavia sql = Octavia.query()
                .from(AmpliacionVacante.class, "av")
                .join("seccion se","colaborador co","co.cargo ca","oficina ofi")
                .filter("av.id", ampliacionVacante);
        return find(sql);
    }

}
