package pe.edu.lamolina.amauta.controller.matricula.matriculable;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface MatriculableLoteService {

    public void executeAporteCarnetLote(DataSessionPivot ds);

    public void eliminarAporteCarnetLote(DataSessionPivot ds);
    
}
