package pe.edu.lamolina.pivot.controller.general.personaperfil;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.PerfilCompania;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.PersonaPerfil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface PersonaPerfilService {

    List<PerfilCompania> allPerfilCompania();

    List<Compania> allCompania();

    List<PersonaPerfil> allPersonasPefiles(DynatableFilter filter);

    PersonaPerfil findPersonaPerfil(PersonaPerfil personaPerfil);

    List<Persona> allPersonasByNombre(String nombre);

    void save(PersonaPerfil personaPerfil, Usuario usuario);

    void update(PersonaPerfil personaPerfil);

    void activate(Long idPersonaPerfil);

    void desactivar(Long idPersonaPerfil);

}
