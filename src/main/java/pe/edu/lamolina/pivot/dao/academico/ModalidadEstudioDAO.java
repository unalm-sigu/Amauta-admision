package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;

public interface ModalidadEstudioDAO extends Crud<ModalidadEstudio> {

    ModalidadEstudio findByCodigo(ModalidadEstudioEnum codigo);

    List<ModalidadEstudio> allByCompania(Compania compania);

    List<ModalidadEstudio> allActivos();

    List<ModalidadEstudio> allActivoByCompania(Compania compania);

    List<ModalidadEstudio> allByCodigos(List<String> codigos);

}
