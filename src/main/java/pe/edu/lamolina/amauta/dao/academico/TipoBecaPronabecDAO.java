package pe.edu.lamolina.amauta.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.pronabec.TipoBeca;

import java.util.List;

public interface TipoBecaPronabecDAO extends EasyDAO<TipoBeca> {
    List<TipoBeca> allTiposBecas();
    TipoBeca findByCodigo(String codigoTipoBeca);
}
