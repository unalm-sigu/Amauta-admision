package pe.edu.lamolina.amauta.dao.medico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.medico.ConsultorioEnum;
import pe.edu.lamolina.model.medico.EspecialidadMedica;

public interface EspecialidadMedicaDAO extends EasyDAO<EspecialidadMedica> {

    EspecialidadMedica findByCodigoEnum(ConsultorioEnum codigo);

}
