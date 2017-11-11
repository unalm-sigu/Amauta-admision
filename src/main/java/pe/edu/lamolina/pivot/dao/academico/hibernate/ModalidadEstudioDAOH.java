package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum.PRE;

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

    @Override
    public List<ModalidadEstudio> allByCompania(Compania compania) {
        SqlUtil sqlUtil = new SqlUtil("mo")
                .parents("compania co")
                .filter("co.id", compania);
        return all(sqlUtil);
    }

    @Override
    public List<ModalidadEstudio> allActivos() {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "mo")
                .join("compania")
                .filter("estado", EstadoEnum.ACT.name());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<ModalidadEstudio> allActivoByCompania(Compania compania) {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "mo")
                .join("compania")
                .filter("compania", compania)
                .filter("estado", EstadoEnum.ACT.name());
        return sql.all(getCurrentSession());
    }

    @Override
    public List<ModalidadEstudio> allPrePostgrado(Compania cia) {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "me")
                .join("compania cia")
                .filter("cia.id", cia.getId())
                .filter("me.estado", EstadoEnum.ACT.name())
                .in("me.codigo", Arrays.asList(PRE.name(), EPG.name()));
        return sql.all(getCurrentSession());
    }
}
