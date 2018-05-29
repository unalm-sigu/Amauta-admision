package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface SolicitudConstanciaDAO extends EasyDAO<TramiteDocumentoAcademico> {

    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter);

    public TramiteDocumentoAcademico find(TramiteDocumentoAcademico tramiteDocumentoAcademico);

}
