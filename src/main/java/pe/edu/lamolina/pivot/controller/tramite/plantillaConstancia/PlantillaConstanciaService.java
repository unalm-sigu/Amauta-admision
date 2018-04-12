package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;

public interface PlantillaConstanciaService {

    void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);

    void save(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);

    List<PlantillaDocumentoAcademico> all(DynatableFilter filter);

    PlantillaDocumentoAcademico findById(PlantillaDocumentoAcademico plantillaDocumentoAcademico);

    List<Idioma> allIdioma();
    
    void updateContenido(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);
}
