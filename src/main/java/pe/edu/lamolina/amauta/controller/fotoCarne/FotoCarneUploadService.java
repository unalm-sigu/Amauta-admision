package pe.edu.lamolina.amauta.controller.fotocarne;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface FotoCarneUploadService {

    public void procesarFotos(DataSessionPivot ds, String rutaFotos);

}
