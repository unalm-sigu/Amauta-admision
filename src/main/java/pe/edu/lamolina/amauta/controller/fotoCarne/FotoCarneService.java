package pe.edu.lamolina.amauta.controller.fotoCarne;

import javax.servlet.http.HttpServletResponse;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface FotoCarneService {

    public void descargarFotos(DataSessionPivot ds, String carrera, HttpServletResponse response);

}
