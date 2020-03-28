package pe.edu.lamolina.pivot.zelper.constant;

import java.util.Arrays;
import java.util.List;
import pe.albatross.zelpers.miscelanea.OSValidator;

public interface Constantine {

    Integer YEAR_ALL_APPROVE = 1987;

    String SESSION_USUARIO = "SESSION_USUARIO";

    String DOCENTE_INDETERMINADO = "N.N.";

    String TMP_DIR = OSValidator.isWindows() ? "C:/tmp/" : "/tmp/";

    String[] FLAT_COLOR = {"#1abc9c", "#3498db", "#e74c3c", "#9b59b6", "#2ecc71", "#f39c12", "#A59275", "#434b51"};
    String[] MORE_FLAT_COLOR = {"#3498db", "#2ecc71", "#9b59b6", "#34495e", "#d35400", "#16a085", "#2980b9", "#8e44ad", "#2c3e50", "#f1c40f", "#e67e22", "#e74c3c", "#ecf0f1", "#27ae60", "#95a5a6", "#f39c12", "#c0392b", "#bdc3c7", "#7f8c8d", "#1abc9c"};
    Integer DEFAULT_BUFFER_SIZE_DOWNLOAD = 1024;

    //String ADMISION_DIR = OSValidator.isWindows() ? "D:/dxtr/tmp/" : "/PIVOT/notas/";
    String ADMISION_DIR = OSValidator.isWindows() ? "C:/tmp/" : "/tmp/";

    String AVATAR_DIR = OSValidator.isWindows() ? "C:/avatar/" : "/avatar/";

    Long ID_OFICINA_OERA = 50L;

    String PIVOT_DIR = "lamolina-pivot/";

    String COD_CARRERA_ALUMNO_VISITANTE = "001";

    String COD_CARRERA_ALUMNO_ESPECIAL = "000";

    String CODE_POSTULANTE_DUMMY = "00000000";

    String APP_ERROR_MESSAGE = "Error Desconocido: por favor reporte el problema.";

    //restriccion encuesta docente
    String REQ_MAX_DOCENTE = "No cumple requisito de máximo docentes";
    String REQ_MIN_ALUMNO = "No cumple requisito de mínimo alumnos";
    String REQ_CUR_TEORIA = "Encuestado en el curso de  teoría";
    String REQ_EVENTO = "Evento académico no configurado";
    //html to pdf
    String PDF_CSS = "public/app/pdf/css/pdf.css";
    String PDF_IMG = "/public/app/pdf/img/";
    String PDF_IMG2 = "/public/pdf/img/";

    //code idioma
    String CODE_IDIOMA_ESPANOL = "es";
    String CODE_IDIOMA_INGLES = "en";

    //formato imagen
    int IMAGE_DPIHEIGHT = 72;
    String IMAGE_DPIHEIGHT_MSG = "la imagen debe de tener un dpi mayor o igual a 72";
    int IMAGE_DPIWIDTH = 72;
    String IMAGE_DPIWIDTH_MSG = "la imagen debe de tener un dpi mayor o igual a 72";
    int IMAGE_HEIGHT = 508;
    String IMAGE_HEIGHT_MSG = "la imagen debe de tener una altura aproximada a 508px";
    int IMAGE_WIDTH = 338;
    int IMAGE_DELTA_SIZE = 5;
    String IMAGE_WIDTH_MSG = "la imagen debe de tener un ancho aproximada a 338px";
    String[] IMAGE_FORMAT = {"JPEG", "PNG"};
    String IMAGE_FORMAT_MSG = "la imagen debe de ser formato jpeg";
    String IMAGE_COLORTYPE = "RGB";
    String IMAGE_COLORTYPE_MSG = "la imagen debe de tener el sistema de color  RGB";
    int IMAGE_BITSPERPIXEL = 24;
    String IMAGE_BITSPERPIXEL_MSG = "la imagen debe de tener la cantidad de bits por pixel mayor a  24";
    boolean IMAGE_TRANSPARENT = Boolean.FALSE;
    String IMAGE_TRANSPARENT_MSG = "la imagen no debe de ser transparente";

    //ruta imagen
    String LOGOUNALM = PDF_IMG2 + "escudoUnalmSm.jpg";

    String S3_BUCKET = "lamolina-academico";
    String S3_DIR_FOTO_TMP = "fotos/temporal/";
    String S3_DIR_FOTO_DOCUMENTO = "fotos/documentos/";
    String S3_DIR_FOTO_CARNET = "fotos/carnet/";

    String S3_RUTA = "http://lamolina-academico.s3.amazonaws.com/";
    String S3_FOLDER = "public-unalm/profile/";
    String S3_DIR_SILABUS = "silabus/";

    String S3_LINK = "http://lamolina-academico.s3.amazonaws.com/";
    String S3_TMP = "tmp/";
    String S3_PUBLIC_DIR = "publico/";
    //String S3_DIR = "albatross-pruebas/lamolina-pivot";
    String S3_DIR = "lamolina-academico";
    String S3_RESOLUCIONES_DIR = "resoluciones/";
    //folder of ticket helpdesk 
    String S3_DIR_ARCHIVO_ATENCION = "archivo/atencion/";
    //folder of inventario 
    String S3_DIR_INVENTARIO = "inventario/";
    //folder of convenio 
    String S3_DIR_CONVENIO = "convenio/";
    String S3_TRASH = "trash/";

    // 
    Long ID_TIPO_SUBVENCION_INVESTIGACION = 5L;
    Long ID_TIPO_TRAMITE_SUBVENCION = 1L;

    String CICLO_ORDEN_MERITO = "CICLO_ORDEN_MERITO";
    String CICLO_ANEXO_BOLETIN = "CICLO_ANEXO_BOLETIN";

    final static Integer CANTIDAD_HORAS_POR_EXAMEN = 2;

    Long ID_PERU = 178l;

    Long BOLS_TRABAJO_ID = 1l;
    Long ID_CONSEJERO_NN = 1l;

    String COLOR_WARNING = "warning";
    String COLOR_SUCCESS = "success";
    String COLOR_DANGER = "danger";

    // planes
    Integer CREDITOS_ADIC_ELC = 4;
    String CODIGO_CURSO_DEP = "EG1006";
    String GRUPO_ZTEO = "Z";
    String GRUPO_ZPRA = "Z*";

    List<Long> GRUPOS_HORAS_UNUSED = Arrays.asList(23L, 24L);

    // Anexo Superior
    Long ANEXO_SUP_DEP_ACAD = 2l;

    String HTML_PRE = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta charset='UTF-8'/>\n"
            + "	<title></title>\n"
            + "</head>\n"
            + "<body>";

    String HTML_SUB = "</body>\n"
            + "</html>";

    String CODIGO_ALIANZA_ESTRATEGICA = "003";
    String VARIABLE_TABLE = "albLoop";
    String VARIABLE_INCRUSTACION = "albIncrustacion";
    
    
    Integer CANT_MINIMA_MATRICULA_CACHIMBOS = 10;
}
