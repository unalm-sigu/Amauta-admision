package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.SistemaNotas;

public interface SistemaNotasDAO extends EasyDAO<SistemaNotas> {

    SistemaNotas find(Long id);

}
