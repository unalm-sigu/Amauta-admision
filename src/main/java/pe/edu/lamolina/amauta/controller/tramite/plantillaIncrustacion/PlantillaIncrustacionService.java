package pe.edu.lamolina.amauta.controller.tramite.plantillaIncrustacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;

public interface PlantillaIncrustacionService {

    public List<PlantillaDocumentoAcademico> all(DynatableFilter filter);

    public List<Idioma> allIdioma();

    public void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);

    public void save(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);

    public void delete(Long id);


}
