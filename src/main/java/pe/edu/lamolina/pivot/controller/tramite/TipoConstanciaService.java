package pe.edu.lamolina.pivot.controller.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface TipoConstanciaService {

    public void update(TipoDocumentoAcademico tipoDocumentoAcademico, Usuario usuario);

    public void save(TipoDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario);

    public List<TipoDocumentoAcademico> all(DynatableFilter filte);

    public TipoDocumentoAcademico findById(TipoDocumentoAcademico tipoDocumentoAcademico);

    public List<TipoDocumentoAcademico> all();

}
