package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResolucionService {

    List<Resolucion> allResolucionesByFilter(DynatableFilter filter, DataSessionPivot dsp);

    List<Reincorporacion> allReincorporacionByFilter(DynatableFilter filter, Resolucion resolucion);

    List<TipoResolucion> allTiposResolucion();

    void saveResolucion(Resolucion resolucion, DataSessionPivot ds, CicloAcademico cicloAcademico);

    void saveConfirmarResVB(Resolucion resolucion, DataSessionPivot ds, CicloAcademico cicloAcademico);

    List<Tramite> allTramitesByTipoEstadoTram(TipoTramiteEnum tipoTramiteEnum, EstadoTramiteEnum estadoTramiteEnum);

    List<ReunionConsejo> allReunionesConsejoByOficina(Oficina oficina);

    List<TramiteReunionConsejo> allTramiteReunionConsejoByReunion(ReunionConsejo reunionConsejo, TipoResolucion tipoResolucion);

    Resolucion findResolucion(Long resolucionId);

    Tramite findTramite(Long tramiteId);

    void updateResolucion(Resolucion resolucion, DataSessionPivot dataSessionPivot);

    void uploadResolucionFile(Resolucion resolucion, MultipartFile file, DataSessionPivot ds);

    void saveConfirmarSubirDocumento(Resolucion resolucion, DataSessionPivot ds);

    List<CicloAcademico> allCiclosToReincorporacion();

    List<Oficina> allOFicinasByUser(DataSessionPivot ds);

    public List<CursoDirigido> allCursoDirigido(DynatableFilter filter, Resolucion resolucion);

}
