package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaCuentaBancariaDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCuentaBancaria;

@Repository
public class PersonaCuentaBancariaDAOH extends AbstractEasyDAO<PersonaCuentaBancaria> implements PersonaCuentaBancariaDAO {

    public PersonaCuentaBancariaDAOH() {
        super();
        setClazz(PersonaCuentaBancaria.class);
    }

    @Override
    public List<PersonaCuentaBancaria> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(PersonaCuentaBancaria.class, "pcb")
                .join("persona per", "banco ban", "ban.empresa", "ban.etiqueta")
                .filter("per.id", persona)
                .orderBy("pcb.id DESC");
        return all(sql);
    }

    @Override
    public PersonaCuentaBancaria findActivo(Persona persona) {
        Octavia sql = Octavia.query()
                .from(PersonaCuentaBancaria.class, "pcb")
                .join("persona per", "banco ban", "ban.empresa", "ban.etiqueta")
                .filter("pcb.estado", EstadoEnum.ACT)
                .filter("per.id", persona);

        return find(sql);
    }

}
