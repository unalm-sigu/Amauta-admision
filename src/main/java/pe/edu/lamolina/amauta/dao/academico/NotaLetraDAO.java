package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.NotaLetra;
import pe.edu.lamolina.model.academico.SistemaNotas;

public interface NotaLetraDAO extends EasyDAO<NotaLetra> {

    List<NotaLetra> allBySistemaNotas(SistemaNotas sistemaNotas);

}
