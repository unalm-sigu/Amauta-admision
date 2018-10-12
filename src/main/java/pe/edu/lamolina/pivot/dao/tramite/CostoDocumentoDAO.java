package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface CostoDocumentoDAO extends EasyDAO<PrecioDocumento> {

    public List<PrecioDocumento> allDynatable(DynatableFilter filter);

    PrecioDocumento findById(PrecioDocumento precioDocumento);

    public PrecioDocumento findTipoDocAndIdioma(TipoDocumentoAcademico tipoDocumento, Idioma idioma);

}
