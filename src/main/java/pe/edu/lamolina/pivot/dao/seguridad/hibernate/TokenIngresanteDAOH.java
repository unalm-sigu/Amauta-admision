package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.pivot.dao.seguridad.TokenIngresanteDAO;

@Repository
public class TokenIngresanteDAOH extends AbstractEasyDAO<TokenIngresante> implements TokenIngresanteDAO {

    public TokenIngresanteDAOH() {
        super();
        setClazz(TokenIngresante.class);
    }

}
