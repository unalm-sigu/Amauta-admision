package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;

@Repository
public class TokenIngresanteDAOH extends AbstractEasyDAO<TokenIngresante> implements TokenIngresanteDAO {

    public TokenIngresanteDAOH() {
        super();
        setClazz(TokenIngresante.class);
    }

    @Override
    public List<TokenIngresante> allActivos(Persona persona, Usuario usuario) {
        Octavia sql = Octavia.query()
                .from(TokenIngresante.class, "too")
                .join("persona per", "userRegistro user")
                .filter("per.id", persona)
                .filter("user.id", usuario)
                .filter("too.estado", TokenEstadoEnum.ACT);

        return all(sql);
    }
}
