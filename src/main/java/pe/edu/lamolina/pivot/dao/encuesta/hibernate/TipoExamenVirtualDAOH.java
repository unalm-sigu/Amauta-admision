package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import static pe.edu.lamolina.model.enums.TipoExamenVirtualEnum.ENC_CUR;
import static pe.edu.lamolina.model.enums.TipoExamenVirtualEnum.ENC_DOC;
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

    @Override
    public List<TipoExamenVirtual> allEncuestaEstudiantil() {
        Octavia sql = Octavia.query()
                .from(TipoExamenVirtual.class, "te")
                .in("te.codigo", Arrays.asList(ENC_CUR, ENC_DOC));
        return all(sql);
    }

}
