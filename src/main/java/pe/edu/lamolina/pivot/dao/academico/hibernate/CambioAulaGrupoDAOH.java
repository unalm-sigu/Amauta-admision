package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.CambioAulaGrupoDAO;

@Repository
public class CambioAulaGrupoDAOH extends AbstractEasyDAO<CambioAulaGrupo> implements CambioAulaGrupoDAO {

    @Override
    public CambioAulaGrupo find(CambioAulaGrupo cambioAulaGrupo) {
        Octavia sql = Octavia.query()
                .from(CambioAulaGrupo.class, "cag")
                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi", "co.persona")
                .leftJoin("se.seccionSuperior", "aulaInicio", "aulaFin", "grupoHorasInicio", "grupoHorasFin")
                .filter("cag.id", cambioAulaGrupo);
        return find(sql);
    }

    @Override
    public List<CambioAulaGrupo> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(CambioAulaGrupo.class, "cag")
                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi", "co.persona")
                .leftJoin("aulaInicio", "aulaFin", "grupoHorasInicio", "grupoHorasFin")
                .filter("se.id", seccion)
                .orderBy("cag.fechaSolicitud desc");
        return all(sql);
    }

    @Override
    public List<CambioAulaGrupo> allBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(CambioAulaGrupo.class, "cag")
                .join("seccion se", "colaborador co", "co.cargo ca", "oficina ofi", "co.persona")
                .leftJoin("aulaInicio", "aulaFin", "grupoHorasInicio", "grupoHorasFin")
                .in("se.id", secciones);
        return all(sql);
    }
}
