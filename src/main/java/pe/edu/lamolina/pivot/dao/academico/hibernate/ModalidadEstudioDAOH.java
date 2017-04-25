package pe.edu.lamolina.pivot.dao.academico.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;

@Repository
public class ModalidadEstudioDAOH extends AbstractDAO<ModalidadEstudio> implements ModalidadEstudioDAO {

    public ModalidadEstudioDAOH() {
        super();
        setClazz(ModalidadEstudio.class);
    }

    @Override
    public ModalidadEstudio findByCodigo(ModalidadEstudioEnum codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .filter("me.codigo", codigo.name());
        return this.find(sqlUtil);
    }
}
