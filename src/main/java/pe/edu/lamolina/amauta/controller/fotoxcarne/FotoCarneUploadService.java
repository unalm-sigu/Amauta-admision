package pe.edu.lamolina.amauta.controller.fotoxcarne;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface FotoCarneUploadService {

    void procesarFotos(DataSessionPivot ds, String rutaFotos);

}
