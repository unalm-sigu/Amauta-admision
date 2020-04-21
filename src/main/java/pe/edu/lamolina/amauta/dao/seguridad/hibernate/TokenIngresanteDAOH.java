package pe.edu.lamolina.amauta.dao.seguridad.hibernate;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.seguridad.TokenIngresanteDAO;

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

    @Override
    public TokenIngresante findUltimoVigente(Persona persona) {
        Octavia sql = Octavia.query()
                .from(TokenIngresante.class, "ti")
                .join("persona per")
                .isNull("ti.fechaUso")
                .filter("fechaVencimiento", ">", new Date())
                .filter("ti.estado", "ACT")
                .filter("per.id", persona.getId());
        return (TokenIngresante) sql.find(getCurrentSession());
    }

    @Override
    public TokenIngresante findByToken(String token) {
        Octavia sql = Octavia.query()
                .from(TokenIngresante.class, "tok")
                .leftJoin("persona per", "userRegistro use")
                .isNull("tok.fechaUso")
                .filter("tok.fechaVencimiento", ">", new Date())
                .filter("tok.valor", token)
                .filter("tok.estado", TokenEstadoEnum.ACT);
        return find(sql);
    }
}
