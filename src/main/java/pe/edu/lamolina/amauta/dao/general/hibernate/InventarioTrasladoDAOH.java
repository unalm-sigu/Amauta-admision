package pe.edu.lamolina.amauta.dao.general.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.general.InventarioTrasladoDAO;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.enums.TipoAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.InventarioTraslado;

import java.util.Arrays;
import java.util.List;

@Repository
public class InventarioTrasladoDAOH extends AbstractEasyDAO<InventarioTraslado> implements InventarioTrasladoDAO {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public InventarioTrasladoDAOH(){
        super();
        setClazz(InventarioTraslado.class);
    }

    @Override
    public List<InventarioTraslado> allByInventario(Inventario inventario) {
        Octavia sql = Octavia.query()
                .from(InventarioTraslado.class, "it")
                .filter("it.inventario", inventario)
                .orderBy("it.id desc");

        return all(sql);
    }
}
