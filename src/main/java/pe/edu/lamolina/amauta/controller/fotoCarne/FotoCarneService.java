package pe.edu.lamolina.amauta.controller.fotoCarne;

import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface FotoCarneService {

    public void descargarFotos(DataSessionPivot ds);

    public FotosCarneComponent info(DataSessionPivot ds);

    FotosCarneComponent activar(DataSessionPivot ds);

}
