package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaFotoDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaFoto;

@Repository
public class PersonaFotoDAOH extends AbstractEasyDAO<PersonaFoto> implements PersonaFotoDAO {

    public PersonaFotoDAOH() {
        super();
        setClazz(PersonaFoto.class);
    }

    @Override
    public PersonaFoto find(long id) {
        Octavia sql = Octavia.query()
                .from(PersonaFoto.class, "pf")
                .join("persona per", "archivo")
                .filter("pf.id", id);

        return find(sql);
    }

    @Override
    public List<PersonaFoto> allByTipo(Persona persona, String tipoFoto) {
        Octavia sql = Octavia.query()
                .from(PersonaFoto.class, "pf")
                .join("persona per", "archivo")
                .filter("per.id", persona)
                .filter("tipoFoto", tipoFoto)
                .orderBy("case pf.estado when 'ACT' then 1 else 2 end", "pf.id DESC");

        return all(sql);
    }

    @Override
    public PersonaFoto findActiva(Persona persona, String tipoFoto) {
        Octavia sql = Octavia.query()
                .from(PersonaFoto.class, "pf")
                .join("persona per", "archivo")
                .filter("pf.estado", EstadoEnum.ACT)
                .filter("per.id", persona)
                .filter("tipoFoto", tipoFoto);

        return find(sql);
    }

}
