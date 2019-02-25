package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.TipoCarpeta;

public interface TipoCarpetaDAO extends EasyDAO<TipoCarpeta> {

    public List<TipoCarpeta> allByDynatable(DynatableFilter filter);

    public List<TipoCarpeta> allByTipoCarpetas(List<TipoCarpeta> tipoCarpetas);

}
