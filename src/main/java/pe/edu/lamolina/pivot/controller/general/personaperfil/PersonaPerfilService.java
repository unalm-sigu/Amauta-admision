package pe.edu.lamolina.pivot.controller.general.personaperfil;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface PersonaPerfilService {

    List<PerfilCompania> allPerfilCompania();

    List<Compania> allCompania();

    List<PersonaCargo> allPersonasPefiles(DynatableFilter filter);

    PersonaCargo findPersonaPerfil(PersonaCargo personaPerfil);

    List<Persona> allPersonasByNombre(String nombre);

    void save(PersonaCargo personaPerfil, Usuario usuario);

    void update(PersonaCargo personaPerfil);

    void activate(Long idPersonaPerfil);

    void desactivar(Long idPersonaPerfil);

}
