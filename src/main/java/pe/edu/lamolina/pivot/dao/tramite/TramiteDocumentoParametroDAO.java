package pe.edu.lamolina.pivot.dao.tramite;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.VariableGenericaEnum;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoParametro;

public interface TramiteDocumentoParametroDAO extends EasyDAO<TramiteDocumentoParametro> {

    public TramiteDocumentoParametro findByTipoDocAndPlantilla(TramiteDocumentoAcademico documentoAcademico, PlantillaDocumentoAcademico plantilla, VariableGenericaEnum variableContenidoEnum);

}
