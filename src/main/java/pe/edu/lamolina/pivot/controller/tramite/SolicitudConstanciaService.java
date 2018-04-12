package pe.edu.lamolina.pivot.controller.tramite;

import java.util.List;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface SolicitudConstanciaService {

    public TramiteDocumentoAcademico findById(TramiteDocumentoAcademico tramiteDocumentoAcademico);

    public List<TramiteDocumentoAcademico> all();

    public void save(TramiteDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario);

    public void update(TramiteDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario);

}
