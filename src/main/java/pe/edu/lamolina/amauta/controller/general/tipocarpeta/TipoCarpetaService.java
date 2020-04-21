package pe.edu.lamolina.amauta.controller.general.tipocarpeta;

import java.util.List;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface TipoCarpetaService {

    List<TipoCarpeta> allTipoCarpeta(DataSessionPivot ds);

    void save(TipoCarpeta tipoCarpeta, DataSessionPivot ds);

    void editar(TipoCarpeta tipoCarpeta, DataSessionPivot ds);

}
