package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;

public interface ModalidadEstudioDAO extends Crud<ModalidadEstudio> {

    ModalidadEstudio findByCodigo(ModalidadEstudioEnum codigo);

}
