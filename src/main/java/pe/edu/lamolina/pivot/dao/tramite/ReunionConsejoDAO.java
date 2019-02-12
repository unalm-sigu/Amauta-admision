package pe.edu.lamolina.pivot.dao.tramite;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.ReunionConsejo;

public interface ReunionConsejoDAO extends EasyDAO<ReunionConsejo> {

    ReunionConsejo findByFechaAndOficina(Date fecha, Oficina oficina);

    List<ReunionConsejo> allByOficina(Oficina oficina);

    List<ReunionConsejo> allByDynatable(DynatableFilter filter, List<Oficina> oficina);

    public List<ReunionConsejo> allByOficinas(List<Oficina> oficinas);

}
