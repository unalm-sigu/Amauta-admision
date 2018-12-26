package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;

public interface TipoCursoCurriculaDAO extends EasyDAO<TipoCursoCurricula> {

    public TipoCursoCurricula findByCodigo(TipoCursoCurriculaEnum tipoCursoCurriculaEnum);

}
