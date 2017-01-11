package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.general.UbicacionDAO;
import pe.edu.lamolina.pivot.model.general.Ubicacion;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;

@Repository
public class UbicacionDAOH extends AbstractDAO<Ubicacion> implements UbicacionDAO {

    public UbicacionDAOH() {
        super();
        setClazz(Ubicacion.class);
    }

    @Override
    public List<Ubicacion> allDistritos(String nombre) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ubdi")
                .parents("tipoUbicacion ti", "ubicacionSuperior ubpr", "_ubpr.ubicacionSuperior ubde")
                .parents("_ubpr.tipoUbicacion", "_ubde.tipoUbicacion")
                .filterStr("ubdi.nombre like", nombre)
                .filter("ti.simbolo", "DIST")
                .orderBy("ubdi.nombre", "ubpr.nombre", "ubde.nombre")
                .setPageSize(15);

        return this.all(sqlUtil);
    }
    
}

