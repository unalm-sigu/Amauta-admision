package pe.edu.lamolina.pivot.controller.academico.convenio;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConvenioService {

    public void delete(ConvenioBeca convenioBeca);

    public void update(ConvenioBeca convenioBeca, DataSessionPivot ds);

    public void save(ConvenioBeca convenioBeca, DataSessionPivot ds);

    public ConvenioBeca findConvenioBeca(Long idConvenioBeca);

    public List<ConvenioBeca> allByDynatable(DynatableFilter filter);

}
