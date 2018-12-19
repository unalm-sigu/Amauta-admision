package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.pivot.dao.academico.TipoActividadIngresanteDAO;

@Repository
public class TipoActividadIngresanteDAOH extends AbstractEasyDAO<TipoActividadIngresante> implements TipoActividadIngresanteDAO {

    public TipoActividadIngresanteDAOH() {
        super();
        setClazz(TipoActividadIngresante.class);
    }

    @Override
    public TipoActividadIngresante findCodigo(TipoActividadIngresanteEnum tipoActividadIngresanteEnum) {
       Octavia sql = new  Octavia()
               .from(TipoActividadIngresante.class,"ta")
               .filter("codigo", tipoActividadIngresanteEnum);
       return find(sql);
    }

}
