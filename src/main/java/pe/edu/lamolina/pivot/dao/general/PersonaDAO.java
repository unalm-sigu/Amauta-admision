package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;

public interface PersonaDAO extends Crud<Persona> {

    List<Persona> allByNombre(String nombre);

    public List<Persona> allByFilter(DynatableFilter filter);

    public Persona findByDocIdentidad(TipoDocIdentidad tipoDocumento, String numeroDocIdentidad);

    public List<Persona> allByEmailEmpresa(String email);

    public List<Persona> allByEmailEmpresaWithoutPersona(Persona persona);

    public List<Persona> allByEmail(String email);

    public List<Persona> allByEmailWithoutPersona(Persona persona);

}
