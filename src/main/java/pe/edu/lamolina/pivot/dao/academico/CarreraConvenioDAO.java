package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CarreraConvenio;
import pe.edu.lamolina.model.academico.ConvenioBeca;

public interface CarreraConvenioDAO extends EasyDAO<CarreraConvenio> {

    public void deleteByConvenioBeca(ConvenioBeca convenioBeca);

    public List<CarreraConvenio> allByCarreraConvenio(List<ConvenioBeca> convenios);

    public List<CarreraConvenio> allByConvenioBeca(ConvenioBeca convenioBeca);

}
