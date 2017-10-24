package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;

public interface PersonaDAO extends Crud<Persona> {

    List<Persona> allByNombre(String nombre);

    List<Persona> allByFilter(DynatableFilter filter);

    Persona findByDocIdentidad(TipoDocIdentidad tipoDocumento, String numeroDocIdentidad);

    List<Persona> allByEmailEmpresa(String email);

    List<Persona> allByEmailEmpresaWithoutPersona(Persona persona);

    List<Persona> allByEmail(String email);

    List<Persona> allByEmailWithoutPersona(Persona persona);

    List<Persona> allByApellidosNombres(Persona persona);

    List<Persona> allByEmailCompania(String email);

    List<Persona> allByEmailCompaniaWithoutPersona(Persona persona);

}
