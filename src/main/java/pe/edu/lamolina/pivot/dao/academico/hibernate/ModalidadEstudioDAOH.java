package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.general.Compania;

@Repository
public class ModalidadEstudioDAOH extends AbstractEasyDAO<ModalidadEstudio> implements ModalidadEstudioDAO {

    public ModalidadEstudioDAOH() {
        super();
        setClazz(ModalidadEstudio.class);
    }

    @Override
    public ModalidadEstudio findByCodigo(ModalidadEstudioEnum codigo) {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "me")
                .filter("me.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<ModalidadEstudio> allByCompania(Compania compania) {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "me")
                .join("compania co")
                .filter("co.id", compania);

        return all(sql);
    }

    @Override
    public List<ModalidadEstudio> allActivos() {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "mo")
                .join("compania")
                .filter("estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<ModalidadEstudio> allActivoByCompania(Compania compania) {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "mo")
                .join("compania")
                .filter("compania", compania)
                .filter("estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<ModalidadEstudio> allPrePostgrado(Compania cia) {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class, "me")
                .join("compania cia")
                .filter("cia.id", cia.getId())
                .filter("me.estado", EstadoEnum.ACT)
                .in("me.codigo", Arrays.asList(PRE, EPG));

        return all(sql);
    }

    @Override
    public List<ModalidadEstudio> allByCodigos(List<String> codigos) {
        Octavia sql = Octavia.query()
                .from(ModalidadEstudio.class)
                .in("codigo", codigos);

        return all(sql);
    }
}
