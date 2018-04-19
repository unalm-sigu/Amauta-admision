package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface TipoConstanciaDAO extends EasyDAO<TipoDocumentoAcademico> {

    public List<TipoDocumentoAcademico> allDynatable(DynatableFilter filter);

}
