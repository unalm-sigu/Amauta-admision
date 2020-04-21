package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.flujo;

import java.util.List;
import org.joda.time.DateTime;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.Tramite;

public interface FlujoTramiteAcademicoService {

    void saveFlujoTramite(Tramite tramite, Usuario usuario, DateTime today);

    void saveFlujoTramite(Tramite tramite, Usuario usuario, DateTime today, boolean revert);

    List<AccionTramiteAcademico> allAccionesTramiteByTramite(Tramite tramite);

}
