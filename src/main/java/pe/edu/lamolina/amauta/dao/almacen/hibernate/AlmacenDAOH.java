package pe.edu.lamolina.amauta.dao.almacen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.amauta.dao.almacen.AlmacenDAO;

@Repository
public class AlmacenDAOH extends AbstractEasyDAO<Almacen> implements AlmacenDAO {

    public AlmacenDAOH() {
        super();
        setClazz(Almacen.class);
    }

    @Override
    public Almacen findByAula(Aula aula) {
        Octavia sql = Octavia.query()
                .from(Almacen.class, "al")
                .leftJoin("aula au","oficina ofi")
                .filter("au.id", aula);
        return find(sql);
    }

}
