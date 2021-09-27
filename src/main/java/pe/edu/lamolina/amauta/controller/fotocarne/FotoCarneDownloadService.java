package pe.edu.lamolina.amauta.controller.fotocarne;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface FotoCarneDownloadService {

    void compilarInformacion(DataSessionPivot ds, String carrera);

    public String descargarLote(FotosCarneDto fotosCarneDto);

}
