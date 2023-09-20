package pe.edu.lamolina.amauta.controller.tramite.trasladointerno;

import java.util.List;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

public interface TramiteTrasladoService {

    List<TramiteTraslado> allTramitesByFilter(DynatableFilter filter, List<CicloAcademico> ciclos);

    void saveTramiteTraslado(TramiteTraslado traslado, DataSessionPivot ds);

    Context reporte(Tramite tramite, DataSessionPivot ds);

    List<Carrera> getCarreras(DataSessionPivot ds);

    void anular(Long idTramiteTraslado, Usuario  usuarioAnulacion);

    List<CicloAcademico> allCicloAcademico();

}
