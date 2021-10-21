package pe.edu.lamolina.amauta.dao.contabilidad;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.ViajeCurso;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;

public interface JustificacionGastoDAO extends EasyDAO<JustificacionGasto> {

    JustificacionGasto findByViajeCurso(ViajeCurso viajeCurso);

}
