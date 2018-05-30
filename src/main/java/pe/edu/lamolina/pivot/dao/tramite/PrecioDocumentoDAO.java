package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface PrecioDocumentoDAO extends EasyDAO<PrecioDocumento> {

    public List<PrecioDocumento> allPrecioDocumento();

    public PrecioDocumento findByTipoIdioma(TipoDocumentoAcademico tipoDocumento, Idioma idioma);

    public List<PrecioDocumento> allByTipoDocumentoAcademico(List<TipoDocumentoAcademico> tipos);

    public List<PrecioDocumento> allByTipoDocumentoAcademico(TipoDocumentoAcademico tipo);
}
