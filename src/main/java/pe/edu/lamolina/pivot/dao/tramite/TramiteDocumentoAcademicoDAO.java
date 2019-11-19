package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface TramiteDocumentoAcademicoDAO extends EasyDAO<TramiteDocumentoAcademico> {

    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter);

    public TramiteDocumentoAcademico find(TramiteDocumentoAcademico tramiteDocumentoAcademico);

    public void updateColumns(TramiteDocumentoAcademico tramiteDocumentoAcademico, String... string);

    public TramiteDocumentoAcademico findTramite(Tramite tramite);

    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter, Persona persona);
}
