package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.general.Persona;

@Repository
public class UsuarioDAOH extends AbstractDAO<Usuario> implements UsuarioDAO {

    public UsuarioDAOH() {
        super();
        setClazz(Usuario.class);
    }

    @Override
    public Usuario findByEmail(String email) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("u")
                .parents("persona pe")
                .filter("u.usuario", email);
        return this.find(sqlUtil);
    }

    @Override
    public Usuario findByPersona(Persona persona) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("u")
                .parents("persona pe")
                .filter("pe.id", persona);

        return this.find(sqlUtil);
    }

    @Override
    public List<Usuario> allByPersonas(List<Persona> personas) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("u")
                .parents("persona pe")
                .filterIn("pe.id", personas);

        return this.all(sqlUtil);
    }

}
