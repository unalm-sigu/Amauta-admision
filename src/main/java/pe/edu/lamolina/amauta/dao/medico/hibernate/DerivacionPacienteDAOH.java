package pe.edu.lamolina.amauta.dao.medico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.medico.DerivacionPacienteDAO;
import pe.edu.lamolina.model.medico.DerivacionPaciente;

@Repository
public class DerivacionPacienteDAOH extends AbstractEasyDAO<DerivacionPaciente> implements DerivacionPacienteDAO {

    public DerivacionPacienteDAOH() {
        super();
        setClazz(DerivacionPaciente.class);
    }

}
