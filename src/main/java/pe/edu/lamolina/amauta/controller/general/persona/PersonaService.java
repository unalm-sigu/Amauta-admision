package pe.edu.lamolina.amauta.controller.general.persona;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.general.EmpresaEtiquetada;
import pe.edu.lamolina.model.general.PersonaCuentaBancaria;
import pe.edu.lamolina.model.general.PersonaFoto;

public interface PersonaService {

    List<Persona> allByDynatable(DynatableFilter filter);

    Persona find(Persona persona);

    List<PersonaCuentaBancaria> allCtasBancarias(Persona persona);

    List<PersonaFoto> allFotosPersonaByTipo(Persona persona, String tipo);

    List<EmpresaEtiquetada> allBancos();

    List<TipoDocIdentidad> allDocumentos();

    void savePersona(Persona persona, DataSessionPivot ds);

    String validarEmailByPersona(String email, Persona persona);

    String validarEmailEmpresaByPersona(String email, Persona persona);

    Persona findPersona(Persona personaTmp);

    String validarEmailCompaniaByPersona(String email, Persona persona);

    void updatePersonaAlumno(Persona persona, Usuario usuario);

    void saveCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds);

    void deleteCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds);

    void activarCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds);

    void saveFirma(PersonaFoto personaFirma, DataSessionPivot ds);

    void anularFirma(PersonaFoto personaFoto, DataSessionPivot ds);

    String getPersonaJsonValidacion(Persona persona);

    void registrarValidacionDocente(Persona persona, Docente docente, DataSessionPivot ds);

    void registrarValidacionDocente2(Persona persona, Docente docente, String personJsonInicial, DataSessionPivot ds);

}
