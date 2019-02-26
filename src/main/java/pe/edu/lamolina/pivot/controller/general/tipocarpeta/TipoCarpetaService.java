package pe.edu.lamolina.pivot.controller.general.tipocarpeta;

import java.util.List;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TipoCarpetaService {

    List<TipoCarpeta> allTipoCarpeta(DataSessionPivot ds);

}
