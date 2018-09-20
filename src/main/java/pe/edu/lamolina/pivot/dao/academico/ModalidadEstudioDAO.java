package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;

public interface ModalidadEstudioDAO extends EasyDAO<ModalidadEstudio> {

    List<ModalidadEstudio> allActivoByCodesCompania(List<ModalidadEstudioEnum> codes, Compania compania);

    ModalidadEstudio findByCodigo(ModalidadEstudioEnum codigo);

    List<ModalidadEstudio> allByCompania(Compania compania);

    List<ModalidadEstudio> allRegularesActivas();

    List<ModalidadEstudio> allActivoByCompania(Compania compania);

    List<ModalidadEstudio> allByCodigos(List<String> codigos);

    List<ModalidadEstudio> allPrePostgrado(Compania cia);

}
