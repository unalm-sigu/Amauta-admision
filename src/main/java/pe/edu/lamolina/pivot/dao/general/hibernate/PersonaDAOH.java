package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

@Repository
public class PersonaDAOH extends AbstractEasyDAO<Persona> implements PersonaDAO {

    public PersonaDAOH() {
        super();
        setClazz(Persona.class);
    }

    @Override
    public List<Persona> allByNombre(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";

        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("estado", PersonaEstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("numeroDocIdentidad", "like", nombre)
                .endBlock()
                .limit(15);

        return all(sql);
    }

    @Override
    public List<Persona> allByFilter(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Persona.class, "per")
                .leftJoin("per.tipoDocumento td")
                .searchFields("td.simbolo", "per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("per.id desc");

        return all(sql);

    }

    @Override
    public Persona findByDocIdentidad(TipoDocIdentidad tipoDocumento, String numeroDocIdentidad) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("numeroDocIdentidad", numeroDocIdentidad)
                .filter("td.id", tipoDocumento);

        return find(sql);
    }

    @Override
    public List<Persona> allByEmail(String email) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.email", email);

        return all(sql);
    }

    @Override
    public List<Persona> allByEmailWithoutPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.email", persona.getEmail())
                .filter("per.id", "<>", persona);

        return all(sql);
    }

    @Override
    public List<Persona> allByEmailEmpresa(String email) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.emailCompania", email);

        return all(sql);
    }

    @Override
    public List<Persona> allByEmailEmpresaWithoutPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.emailCompania", persona.getEmailCompania())
                .filter("per.id", "<>", persona);

        return all(sql);
    }

    @Override
    public List<Persona> allByApellidosNombres(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.nombres", "like", persona.getNombres());

        if (!StringUtils.isEmpty(persona.getPaterno())) {
            sql.filter("per.paterno", "like", persona.getPaterno());
        }
        if (!StringUtils.isEmpty(persona.getMaterno())) {
            sql.filter("per.materno", "like", persona.getMaterno());
        }

        return all(sql);
    }

    @Override
    public List<Persona> allByEmailCompania(String email) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("emailCompania", email);

        return all(sql);
    }

    @Override
    public List<Persona> allByEmailCompaniaWithoutPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("emailCompania", persona.getEmailCompania())
                .filter("per.id", "<>", persona.getId());

        return all(sql);
    }

    @Override
    public Persona findPersona(Long id) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .leftJoin("ubicacionDomicilio ud")
                .filter("per.id", id);

        return find(sql);
    }

}
