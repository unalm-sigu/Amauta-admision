package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.flujo;

import org.joda.time.DateTime;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.Tramite;

public interface FlujoTramiteAcademicoService {

    void saveFlujoTramite(Tramite tramite, Usuario usuario, DateTime today);

    void saveFlujoTramite(Tramite tramite, Usuario usuario, DateTime today, boolean revert);

}
