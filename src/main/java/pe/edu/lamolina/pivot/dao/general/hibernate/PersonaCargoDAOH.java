package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.pivot.dao.general.PersonaCargoDAO;

@Repository
public class PersonaCargoDAOH extends AbstractEasyDAO<PersonaCargo> implements PersonaCargoDAO {

    public PersonaCargoDAOH() {
        super();
        setClazz(PersonaCargo.class);
    }

    @Override
    public PersonaCargo find(long id) {
        Octavia sql = Octavia.query()
                .from(PersonaCargo.class, "pp")
                .join("perfilCompania", "persona")
                .leftJoin("oficina")
                .filter("pp.id", id);

        return find(sql);

    }

    @Override
    public List<PersonaCargo> allByFiltersDynaTable(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(PersonaCargo.class, "pp")
                .join("perfilCompania peco", "persona per")
                .leftJoin("oficina ofi")
                .searchFields("peco.nombre", "ofi.tipoOficina", "ofi.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("pp.id DESC");

        return all(sql);
    }

    @Override
    public List<PersonaCargo> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(PersonaCargo.class, "pp")
                .join("perfilCompania", "persona per")
                .leftJoin("oficina")
                .filter("per.id", persona);

        return all(sql);
    }

    @Override
    public PersonaCargo findSinCerrar(Oficina oficina, Compania cia) {
        Octavia sql = Octavia.query()
                .from(PersonaCargo.class, "pp")
                .join("perfilCompania peco", "persona per", "compania cia", "oficina ofi")
                .filter("ofi.id", oficina)
                .filter("peco.id", oficina.getCargoJefe())
                .filter("per.id", oficina.getPersonaJefe())
                .filter("cia.id", cia)
                .filter("pp.estado", EstadoEnum.ACT)
                .isNull("pp.fechaFin");

        return find(sql);
    }

    @Override
    public PersonaCargo findCargoByPersona(Oficina oficina, Persona persona) {
        Octavia sql = Octavia.query()
                .from(PersonaCargo.class, "pp")
                .join("perfilCompania pc", "persona per")
                .leftJoin("oficina ofi")
                .filter("per.id", persona)
                .filter("ofi.id", oficina)
                .filter("estado", EstadoEnum.ACT);

        return find(sql);
    }

}
