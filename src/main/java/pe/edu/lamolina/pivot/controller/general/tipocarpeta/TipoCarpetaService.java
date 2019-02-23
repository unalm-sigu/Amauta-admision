package pe.edu.lamolina.pivot.controller.general.tipocarpeta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TipoCarpetaService {

    List<TipoCarpeta> allByDynatable(DynatableFilter filter, DataSessionPivot ds);

}
