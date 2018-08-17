package pe.edu.lamolina.pivot.dao.medico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.medico.Medico;

public interface MedicoDAO extends EasyDAO<Medico> {

    Medico findByColaborador(Colaborador colaboradorBD);

}
