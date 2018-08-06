package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface ResolucionService {

    List<Resolucion> allResolucionesByFilter(DynatableFilter filter);

    List<TipoResolucion> allTiposResolucion();

    void saveResolucion(Resolucion resolucion, Usuario usuario, CicloAcademico cicloAcademico, Oficina oficina);

    List<Tramite> allTramitesByTipoEstadoTram(TipoTramiteEnum tipoTramiteEnum, EstadoTramiteEnum estadoTramiteEnum);

}
