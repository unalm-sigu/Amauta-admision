package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

@Repository
public class UsuarioDAOH extends AbstractEasyDAO<Usuario> implements UsuarioDAO {

    public UsuarioDAOH() {
        super();
        setClazz(Usuario.class);
    }

    @Override
    public Usuario findByEmail(String email) {
        Octavia sql = Octavia.query()
                .from(Usuario.class, "u")
                .join("persona per")
                .filter("u.google", email);

        return find(sql);
    }

    @Override
    public Usuario findByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Usuario.class, "u")
                .join("persona per")
                .filter("per.id", persona);

        return find(sql);
    }

    @Override
    public List<Usuario> allByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(Usuario.class, "u")
                .join("persona per")
                .in("per.id", personas);

        return all(sql);
    }

    @Override
    public List<Usuario> allByFilter(DynatableFilter filter) {
        Octavia subquery = Octavia.query()
                .from(UsuarioRol.class, "u")
                .join("usuario usr", "rol rol");

        DynatableSql sql = new DynatableSql(filter)
                .from(Usuario.class, "u")
                .join("persona per")
                .leftJoin("per.tipoDocumento tdoc")
                .searchSubquery(subquery)
                .subqueryLinkedBy("u.id", "usr.id")
                .searchSubqueryFields("rol.nombre", "rol.codigo")
                .searchFields("tdoc.simbolo", "per.numeroDocIdentidad", "per.telefono", "per.celular", "per.email", "per.emailCompania", "u.estado")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("u.id desc");

        Map<String, Object> queries = filter.getQueries();
        if (queries != null) {
            for (String key : queries.keySet()) {
                if (!key.equals("rol")) {
                    continue;
                }
                Long rol = Long.valueOf((String) queries.get(key));
                Octavia subqueryRol = Octavia.query()
                        .from(UsuarioRol.class, "uurr")
                        .join("usuario uu", "rol rr")
                        .filter("rr.id", rol);

                sql.beginRelativeFilters()
                        .exists(subqueryRol)
                        .linkedBy("u.id", "uu.id");

            }
        }

        return sql.all(getCurrentSession());
    }

    @Override
    public Usuario find(Usuario user) {
        Octavia sql = Octavia.query()
                .from(Usuario.class, "u")
                .join("persona per")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("u.id", user);

        return find(sql);
    }

}
