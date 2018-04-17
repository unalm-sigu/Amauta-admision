package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;

@Repository
public class ContenidoCartaDAOH extends AbstractEasyDAO<ContenidoCarta> implements ContenidoCartaDAO {

    public ContenidoCartaDAOH() {
        super();
        setClazz(ContenidoCarta.class);
    }

    @Override
    public List<ContenidoCarta> allByDynaTableBySistema(DynatableFilter filter, Sistema sistema) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ContenidoCarta.class, "coc")
                .join("sistema sis")
                .filter("sis.id", sistema)
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

}
