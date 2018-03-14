package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;

@Repository
public class TipoExamenVirtualDAOH extends AbstractEasyDAO<TipoExamenVirtual> implements TipoExamenVirtualDAO {

    public TipoExamenVirtualDAOH() {
        super();
        setClazz(TipoExamenVirtual.class);
    }

    @Override
    public TipoExamenVirtual findByEnum(TipoExamenVirtualEnum tipoEnum) {
        Octavia sql = Octavia.query()
                .from(TipoExamenVirtual.class, "te")
                .filter("te.codigo", tipoEnum);

        return find(sql);
    }

}
