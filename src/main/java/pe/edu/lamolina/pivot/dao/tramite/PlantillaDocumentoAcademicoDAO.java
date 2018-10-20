package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface PlantillaDocumentoAcademicoDAO extends EasyDAO<PlantillaDocumentoAcademico> {

    public List<PlantillaDocumentoAcademico> allDynatable(DynatableFilter filter);

    public PlantillaDocumentoAcademico find(Long id);

    public PlantillaDocumentoAcademico find(PlantillaDocumentoAcademico plantillaDocumentoAcademico);

    public PlantillaDocumentoAcademico findTipoDocumento(TipoDocumentoAcademico tipoDocumentoAcademico, Idioma idioma);

    List<PlantillaDocumentoAcademico> allDynatableIncrustacion(DynatableFilter filter);

    public List<PlantillaDocumentoAcademico> allIncrustaciones();

}
