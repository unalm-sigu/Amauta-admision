package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TramitesAcademicosService {

    List<Tramite> allTramitesByFilter(DynatableFilter filter);

    void aceptarSolReincorporacion(Tramite tramite, Usuario usuario);

    void agendarSolicitud(Tramite tramite, ReunionConsejo reunionConsejo, Usuario usuario);

    List<ReunionConsejo> allReunionConsejoByDyna(DynatableFilter filter, Oficina oficina);

    void revertTramiteAcademico(Tramite tramite, DataSessionPivot ds);

}
