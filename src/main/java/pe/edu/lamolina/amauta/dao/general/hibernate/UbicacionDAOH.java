package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.amauta.dao.general.UbicacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Ubicacion;

@Repository
public class UbicacionDAOH extends AbstractEasyDAO<Ubicacion> implements UbicacionDAO {

    public UbicacionDAOH() {
        super();
        setClazz(Ubicacion.class);
    }

    @Override
    public List<Ubicacion> allDistritos(String nombre) {
        Octavia sql = Octavia.query()
                .from(Ubicacion.class, "ubdi")
                .join("tipoUbicacion ti", "ubicacionSuperior ubpr", "ubpr.ubicacionSuperior ubde")
                .join("ubpr.tipoUbicacion", "ubde.tipoUbicacion")
                .like("ubdi.nombre", nombre)
                .filter("ti.simbolo", "DIST")
                .orderBy("ubdi.nombre", "ubpr.nombre", "ubde.nombre")
                .limit(15);

        return all(sql);
    }

}
