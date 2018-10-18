package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.VariableContenidoEnum;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoParametro;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDocumentoParametroDAO;

@Repository
public class TramiteDocumentoParametroDAOH extends AbstractEasyDAO<TramiteDocumentoParametro> implements TramiteDocumentoParametroDAO {

    public TramiteDocumentoParametroDAOH() {
        super();
        setClazz(TramiteDocumentoParametro.class);
    }

    @Override
    public TramiteDocumentoParametro findByTipoDocAndPlantilla(TramiteDocumentoAcademico documentoAcademico, PlantillaDocumentoAcademico plantilla, VariableContenidoEnum variableContenidoEnum) {
        Octavia sql = new Octavia()
                .from(TramiteDocumentoParametro.class,"tdp")
                .join("variableGenerica vg","plantillaDocumento pd","tipoDocumentoAcademico tda")
                .filter("vg.codigoEnum", variableContenidoEnum)
                .filter("pd.id", plantilla)
                .filter("tda.id", documentoAcademico);
        return null;
    }

}
