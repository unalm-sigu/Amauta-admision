package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface TokenIngresanteDAO extends EasyDAO<TokenIngresante> {

    public List<TokenIngresante> allActivos(Persona persona, Usuario usuario);

}
