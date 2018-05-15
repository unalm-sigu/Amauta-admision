package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TipoResolucion;

public interface ResolucionService {

    List<Resolucion> allTramitesByFilter(DynatableFilter filter);

    List<TipoResolucion> allTiposResolucion();

    void saveResolucion(Resolucion resolucion, Usuario usuario, CicloAcademico cicloAcademico, Oficina oficina);

}
