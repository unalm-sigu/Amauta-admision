package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

public interface PersonaDAO extends EasyDAO<Persona> {

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

//    Persona findPersona(Long id);
    Persona findByDocumento(TipoDocIdentidad tipoDocumento, String numeroDocIdentidad);

    Persona findByDoc(Persona persona);

    Persona findByEmailCompania(Persona persona);

    List<Persona> allResponsableAulas(DynatableFilter filter, EstadoEnum... estados);

    List<Persona> allPersonaColaboradorByNombre(String nombre, OficinaEnum... oficinaEnum);

    void updateColumns(Persona persona, String... columns);

}
