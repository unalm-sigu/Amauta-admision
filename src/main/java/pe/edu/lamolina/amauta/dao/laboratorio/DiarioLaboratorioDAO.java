package pe.edu.lamolina.amauta.dao.laboratorio;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.medico.DiarioLaboratorio;

public interface DiarioLaboratorioDAO extends EasyDAO<DiarioLaboratorio> {

    List<DiarioLaboratorio> allFechaDesc();
}
