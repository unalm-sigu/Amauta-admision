package pe.edu.lamolina.amauta.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;

@Repository
public class ContenidoCartaDAOH extends AbstractEasyDAO<ContenidoCarta> implements ContenidoCartaDAO {

    public ContenidoCartaDAOH() {
        super();
        setClazz(ContenidoCarta.class);
    }

    @Override
    public List<ContenidoCarta> allByDynaTableBySistema(DynatableFilter filter, List<Sistema> sistemas) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ContenidoCarta.class, "coc")
                .join("sistema sis")
                .in("sis.id", sistemas)
                .searchFields("coc.nombre", "coc.codigo")
                .orderBy("coc.id desc");

        return all(sql);
    }

    @Override
    public ContenidoCarta findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(ContenidoCarta.class, "cc")
                .filter("cc.codigo", codigo);
        return (ContenidoCarta) sql.find(getCurrentSession());
    }

    @Override
    public ContenidoCarta findByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum) {
        Octavia sql = Octavia.query()
                .from(ContenidoCarta.class, "cc")
                .filter("cc.codigo", contenidoCartaEnum.name());
        return find(sql);
    }

}
