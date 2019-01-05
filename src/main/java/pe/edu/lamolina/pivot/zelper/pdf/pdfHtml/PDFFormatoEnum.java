package pe.edu.lamolina.pivot.zelper.pdf.pdfHtml;

import java.util.HashMap;
import java.util.Map;

public enum PDFFormatoEnum {

    BOLETA_PAGO_ING("BoletaPago", "pdf/boletaPago", "Boleta Pago", "Boleta Pago"),
    BOLETA_PAGO_SOL("BoletaPagoSolicitudConstancia", "pdf/boletaPagoSolicitudConstancia", "Boleta Pago Solicitud Constancia", "Boleta Pago Solicitud Constancia"),
    PLANTILLA_CERTIFICADO("Plantilla Genérica", "pdf/contenidobase", "Plantilla Genérica", "Plantilla Genérica"),
    PROGRAMACION_HORARIOS("ProgramacionHorarios", "pdf/programacionHorarios", "Programacion de Horarios", "Programacion de Horarios");
    //REPORTE VERANO
    REPORTE_VERANO_PAGO_DOCENTE("Plantilla Genérica", "pdf/reporteVeranoPagoDocente", "Reporte Verano Pago Docente", "Reporte Verano Pago Docente"),
    REPORTE_VERANO_DOCENTE_DEPARTAMENTO("Plantilla Genérica", "pdf/reporteVeranoDocenteDepartamento", "reporteVeranoDocenteDepartamento", "reporteVeranoDocenteDepartamento"),
    REPORTE_VERANO_CURSO("Plantilla Genérica", "pdf/reporteVeranoCurso", "Reporte Verano Curso", "Reporte Verano Curso"),
    REPORTE_VERANO_PAGO_DOCENTE_FACULTAD("Plantilla Genérica", "pdf/reporteVeranoPagoDocenteFacultad", "Reporte Verano Pago Docente Facultad", "Reporte Verano Pago Docente Facultad");

    private final String name;
    private final String fileTemplate;
    private final String title;
    private final String subject;

    private static final Map<String, PDFFormatoEnum> lookup = new HashMap<>();

    static {
        for (PDFFormatoEnum d : PDFFormatoEnum.values()) {
            lookup.put(d.getName(), d);
        }
    }

    private PDFFormatoEnum(String name, String fileTemplate, String title, String subject) {
        this.name = name;
        this.fileTemplate = fileTemplate;
        this.title = title;
        this.subject = subject;
    }

    public static PDFFormatoEnum getEnum(String name) {
        for (PDFFormatoEnum d : PDFFormatoEnum.values()) {
            if (d.name().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getFileTemplate() {
        return fileTemplate;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

}
