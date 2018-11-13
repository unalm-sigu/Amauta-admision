package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;

@Repository
public class GrupoRegularExamenDAOH extends AbstractEasyDAO<GrupoRegularExamen> implements GrupoRegularExamenDAO {

    public GrupoRegularExamenDAOH() {
        super();
        setClazz(GrupoRegularExamen.class);
    }

    @Override
    public List<GrupoRegularExamen> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular,
            List<GrupoHorasRolExamenEstadoEnum> estados) {
        Octavia sql = Octavia.query()
                .from(GrupoRegularExamen.class, "gre")
                .join("letraGrupoRegular lgr", "grupoHoras gh")
                .filter("lgr.id", letrasGruposRegular)
                .in("gre.estado", estados);
        return all(sql);
    }

    @Override
    public void updateEstado(GrupoRegularExamen grupoRegularExamenUpd) {
        Octavia octavia = Octavia.update(GrupoRegularExamen.class);
        octavia.set(grupoRegularExamenUpd, "estado");
        octavia.set(grupoRegularExamenUpd, "usuarioExclusion");
        octavia.set(grupoRegularExamenUpd, "fechaExclusion");
        this.update(octavia);
    }

}
