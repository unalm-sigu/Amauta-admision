package pe.edu.lamolina.pivot.zelper.pdf;

import java.util.HashMap;
import java.util.Map;

public enum TipoPdfEnum {

    ACTA_NOTAS("ActaNotas", "pdf/actaDeNotas", "Acta de Notas", "Acta de Notas"),
    RESULTADO_ENCUESTA("ResultadoEncuesta", "pdf/resultadoencuesta", "Resultado de Encuestas", "Resultado de Encuestas"),
    SUBVENCION_CARGA_ADICIONAL("SubvencionCargaAdicional", "pdf/subvencionCargaAdicional", "Subvención por carga Académica Adicional", "Subvención por carga Académica Adicional"),
    AVANCE_CURRICULAR("AvanceCurricular", "pdf/avanceCurricular", "Avance Curricular", "Avance Curricular"),
    HISTORIAL_ACADEMICO("HistorialAcademico", "pdf/historialAcademico", "Historial Académico", "Historial Académico"),
    HISTORIAL_ACADEMICO_CURDIR("HistorialAcademico", "pdf/historialAcademicoCurdir", "Historial Académico", "Historial Académico"),
    HISTORIAL_ACADEMICO_LISTADO("HistorialAcademicoListado", "pdf/historialAcademicoListado", "Historial Académico", "Historial Académico"),
    PLAN_CURRICULAR("SubvencionCargaAdicional", "pdf/planCurricular", "Plan Curricular", "Plan Curricular"),
    CURSOS_MATRICULADOS("CursosMatriculados", "pdf/cursosMatriculados", "Cursos Matriculados", "Cursos Matriculados"),
    LIST_CURSOS_DIRIGIDOS("ListCursosDirigidos", "pdf/listDetalleCursoDirigido", "Lista Cursos Dirigidos", "Lista Cursos Dirigidos"),
    DETALLE_CURSO_DIRIGIDO("DetalleCursoDirigido", "pdf/detalleCursoDirigido", "Curso Dirigido", "Curso Dirigido"),
    HORARIO("Horario", "pdf/horario", "Horario", "Horario"),
    PROGRAMACION_HORARIOS("ProgramacionHorarios", "pdf/programacionHorarios", "Programacion de Horarios", "Programacion de Horarios");

    private final String name;
    private final String fileTemplate;
    private final String title;
    private final String subject;

    private static final Map<String, TipoPdfEnum> lookup = new HashMap<>();

    static {
        for (TipoPdfEnum d : TipoPdfEnum.values()) {
            lookup.put(d.getName(), d);
        }
    }

    private TipoPdfEnum(String name, String fileTemplate, String title, String subject) {
        this.name = name;
        this.fileTemplate = fileTemplate;
        this.title = title;
        this.subject = subject;
    }

    public static TipoPdfEnum getEnum(String name) {
        for (TipoPdfEnum d : TipoPdfEnum.values()) {
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
