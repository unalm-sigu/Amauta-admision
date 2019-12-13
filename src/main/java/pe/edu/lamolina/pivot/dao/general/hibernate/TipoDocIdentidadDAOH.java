package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

@Repository
public class TipoDocIdentidadDAOH extends AbstractEasyDAO<TipoDocIdentidad> implements TipoDocIdentidadDAO {

    public TipoDocIdentidadDAOH() {
        super();
        setClazz(TipoDocIdentidad.class);
    }

    @Override
    public List<TipoDocIdentidad> allForPersonaNatural() {
        Octavia sql = Octavia.query()
                .from(TipoDocIdentidad.class, "td")
                .in("td.simbolo", Arrays.asList("DNI", "CEX", "CE", "PAS"));
        return sql.all(getCurrentSession());
    }

    @Override
    public TipoDocIdentidad findBySimbolo(String simbolo) {
        Octavia sql = Octavia.query()
                .from(TipoDocIdentidad.class, "td")
                .filter("td.simbolo", simbolo);
        return find(sql);
    }

    @Override
    public TipoDocIdentidad findBySimboloAndPais(String simbolo, Pais pais) {
        Octavia sql = Octavia.query()
                .from(TipoDocIdentidad.class, "td")
                .join("pais p")
                .filter("td.simbolo", simbolo)
                .filter("p.id", pais.getId());
        return find(sql);
    }
}
