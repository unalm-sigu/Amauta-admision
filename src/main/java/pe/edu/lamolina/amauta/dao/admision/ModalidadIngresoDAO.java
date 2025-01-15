package pe.edu.lamolina.amauta.dao.admision;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

public interface ModalidadIngresoDAO extends EasyDAO<ModalidadIngreso> {

    ModalidadIngreso findByCode(String codigo);

}
