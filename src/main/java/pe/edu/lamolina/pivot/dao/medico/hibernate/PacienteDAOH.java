package pe.edu.lamolina.pivot.dao.medico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.Paciente;
import pe.edu.lamolina.pivot.dao.medico.PacienteDAO;

@Repository
public class PacienteDAOH extends AbstractEasyDAO<Paciente> implements PacienteDAO {

    public PacienteDAOH() {
        super();
        setClazz(Paciente.class);
    }

    @Override
    public Paciente findByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Paciente.class, "pac")
                .join("persona per")
                .filter("per.id", persona);

        return find(sql);
    }

}
