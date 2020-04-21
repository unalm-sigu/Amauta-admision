package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;

public interface TipoCursoCurriculaDAO extends EasyDAO<TipoCursoCurricula> {

    public TipoCursoCurricula findByCodigo(TipoCursoCurriculaEnum tipoCursoCurriculaEnum);

    public List<TipoCursoCurricula> allByCodigos(List<String> list);

}
