package pe.edu.lamolina.pivot.controller.general.persona;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PersonaService {

    List<Persona> allByDynatable(DynatableFilter filter);

    Persona find(Persona persona);

    List<TipoDocIdentidad> allDocumentos();

    void savePersona(Persona persona, DataSessionPivot ds);

    String validarEmailByPersona(String email, Persona persona);

    String validarEmailEmpresaByPersona(String email, Persona persona);

    Persona findPersona(Persona personaTmp);

    String validarEmailCompaniaByPersona(String email, Persona persona);

    void updatePersonaAlumno(Persona persona, Usuario usuario);

}
