package pe.edu.lamolina.amauta.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.general.ValidacionPersonaDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ValidacionPersona;

@Repository
public class ValidacionPersonaDAOH extends AbstractEasyDAO<ValidacionPersona> implements ValidacionPersonaDAO {

    public ValidacionPersonaDAOH() {
        super();
        setClazz(ValidacionPersona.class);
    }

    @Override
    public ValidacionPersona findAnterior(Persona persona) {
        Octavia sql = Octavia.query()
                .from(ValidacionPersona.class, "vp")
                .join("persona per")
                .filter("per.id", persona)
                .orderBy("vp.id desc")
                .limit(1);

        return find(sql);
    }

}
