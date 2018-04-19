package pe.edu.lamolina.pivot.controller.tramite.costoDocumento;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PrecioDocumento;

public interface CostoDocumentoService {

    public PrecioDocumento findById(PrecioDocumento tramiteDocumentoAcademico);

    public List<PrecioDocumento> all(DynatableFilter filter);

    public void save(PrecioDocumento tramiteDocumentoAcademico, Usuario usuario);

    public void update(PrecioDocumento tramiteDocumentoAcademico, Usuario usuario);

    public List<Idioma> allIdioma();

}
