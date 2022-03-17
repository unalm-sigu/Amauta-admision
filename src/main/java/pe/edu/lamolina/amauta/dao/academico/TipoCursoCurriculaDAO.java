package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;

public interface TipoCursoCurriculaDAO extends EasyDAO<TipoCursoCurricula> {

    TipoCursoCurricula findByCodigo(TipoCursoCurriculaEnum tipoCursoCurriculaEnum);

    List<TipoCursoCurricula> allByCodigos(List<String> list);

}
