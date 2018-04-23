package pe.edu.lamolina.pivot.controller.tramite.tipoConstancia;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;

public interface TipoConstanciaService {

    void update(TipoDocumentoAcademico tipoDocumentoAcademico, Usuario usuario);

    void save(TipoDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario);

    List<TipoDocumentoAcademico> all(DynatableFilter filte);

    TipoDocumentoAcademico findById(TipoDocumentoAcademico tipoDocumentoAcademico);

    List<TipoDocumentoAcademico> all();

    void delete(TipoDocumentoAcademico tipoDocumento);

    List<Oficina> allOficina(String nombre);

    List<TipoOficina> allTipoOficina(String nombre);

}
