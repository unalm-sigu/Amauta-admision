package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.UniversidadDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Repository
public class UniversidadDAOH extends AbstractEasyDAO<Universidad> implements UniversidadDAO {

    public UniversidadDAOH() {
        super();
        setClazz(Universidad.class);
    }

    @Override
    public List<Universidad> allUniversidadByName(String nombre) {
        Octavia sql = Octavia.query()
                .from(Universidad.class, "uni")
                .beginBlock()
                .__().filter("uni.nombre", "like", nombre)
                .__().filter("uni.siglas", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Universidad> allUniversidadByNamePais(String nombre, Pais pais) {
        Octavia sql = Octavia.query()
                .from(Universidad.class, "uni")
                .join("pais pa");

                if (pais.getId() != null) {
                    sql.filter("pa.id", pais.getId());
                }

                sql.beginBlock()
                .__().filter("uni.nombre", "like", nombre)
                .__().filter("uni.siglas", "like", nombre)
                .endBlock()
                .limit(15);
        return sql.all(getCurrentSession());
    }

    @Override
    public Universidad findNombrePais(String nombre, Pais pais) {
        Octavia sql = Octavia.query()
                .from(Universidad.class, "uni")
                .join("pais pa")
                .filter("pa.id", pais)
                .filter("nombre", nombre)
                .limit(1);
        return find(sql);
    }

    @Override
    public Universidad findLastCodigoEntranjero() {
        Octavia sql = Octavia.query()
                .from(Universidad.class, "uni")
                .join("pais pa")
                .filter("pa.id", "<>", Constantine.ID_PERU)
                .like("uni.codigo", "EXT%")
                .orderBy("uni.codigo desc")
                .limit(1);
        return find(sql);
    }
}
