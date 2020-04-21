package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.amauta.dao.general.AusenciaJefeDAO;

@Repository
public class AusenciaJefeDAOH extends AbstractEasyDAO<AusenciaJefe> implements AusenciaJefeDAO {

    public AusenciaJefeDAOH() {
        super();
        setClazz(AusenciaJefe.class);
    }

    @Override
    public AusenciaJefe findSinCerrar(AusenciaJefe ausencia) {
        Octavia sql = Octavia.query()
                .from(AusenciaJefe.class, "au")
                .join("oficina ofi", "encargado enc", "jefe")
                .filter("ofi.id", ausencia.getOficina())
                .filter("enc.id", ausencia.getEncargado())
                .filter("fechaInicioEncargatura", ausencia.getFechaInicioEncargatura())
                .isNull("fechaFinEncargatura");

        return find(sql);
    }

    @Override
    public List<AusenciaJefe> allNoCerradasByOficinas(List<Oficina> oficinas) {
        Octavia sql = Octavia.query()
                .from(AusenciaJefe.class, "au")
                .join("oficina ofi", "encargado enc", "jefe")
                .in("ofi.id", oficinas)
                .isNull("fechaFinEncargatura");

        return all(sql);
    }
}
