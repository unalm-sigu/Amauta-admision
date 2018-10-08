package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface TipoConstanciaDAO extends EasyDAO<TipoDocumentoAcademico> {

    List<TipoDocumentoAcademico> allDynatable(DynatableFilter filter);

    TipoDocumentoAcademico find(TipoDocumentoAcademico tipoDocumentoAcademico);

    List<TipoDocumentoAcademico> allTipoDocumentoAcademicoByName(String nombre);

    List<TipoDocumentoAcademico> all();

}
