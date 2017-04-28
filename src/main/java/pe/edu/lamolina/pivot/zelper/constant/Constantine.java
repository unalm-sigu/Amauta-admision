package pe.edu.lamolina.pivot.zelper.constant;

import pe.albatross.zelpers.miscelanea.OSValidator;

public interface Constantine {

    String SESSION_USUARIO = "SESSION_USUARIO";
    
    String DOCENTE_INDETERMINADO = "N.N.";

    String TMP_DIR = OSValidator.isWindows() ? "D:/dxtr/tmp/" : "/tmp/";

    String[] FLAT_COLOR = {"#1abc9c", "#3498db", "#e74c3c", "#9b59b6", "#2ecc71", "#f39c12", "#A59275", "#434b51"};

    Integer DEFAULT_BUFFER_SIZE_DOWNLOAD = 1024;

    //String ADMISION_DIR = OSValidator.isWindows() ? "D:/dxtr/tmp/" : "/PIVOT/notas/";
    String ADMISION_DIR = OSValidator.isWindows() ? "D:/dxtr/tmp/" : "/tmp/";

}
