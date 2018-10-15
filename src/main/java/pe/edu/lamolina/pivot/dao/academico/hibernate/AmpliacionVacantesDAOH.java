package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AmpliacionVacanteEstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.AmpliacionVacantesDAO;

@Repository
public class AmpliacionVacantesDAOH extends AbstractEasyDAO<AmpliacionVacantes> implements AmpliacionVacantesDAO {

    public AmpliacionVacantesDAOH() {
        super();
        setClazz(AmpliacionVacantes.class);
    }

    @Override
    public List<AmpliacionVacantes> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(AmpliacionVacantes.class, "av")
                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi")
                .filter("se.id", seccion)
                .orderBy("av.fechaSolicitud desc");
        return all(sql);
    }

    @Override
    public AmpliacionVacantes find(AmpliacionVacantes ampliacionVacante) {
        Octavia sql = Octavia.query()
                .from(AmpliacionVacantes.class, "av")
                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi", "co.persona")
                .leftJoin("se.seccionSuperior")
                .filter("av.id", ampliacionVacante);
        return find(sql);
    }

    @Override
    public List<AmpliacionVacantes> allPendientesBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(AmpliacionVacantes.class, "av")
                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi")
                .filter("se.id", seccion)
                .filter("av.estado", AmpliacionVacanteEstadoEnum.PENDIENTE);
        return all(sql);
    }

    @Override
    public List<AmpliacionVacantes> allBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(AmpliacionVacantes.class, "av")
                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi")
                .in("se.id", secciones)
                .orderBy("av.fechaSolicitud desc");
        return all(sql);
    }

}
