package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import java.util.HashMap;
import java.util.Map;

public enum TipoDocumentoAcademicoEnum {

    ALIANZAESTRATEGICAEMPRESARIAL(1L, "Alianza estratégica empresarial"),
    ALUMNO(2L, "Alumno"),
    ALUMNOESPECIAL(3L, "Alumno especial"),
    ALUMNOREGULAR(4L, "Alumno regular"),
    ALUMNOVISITANTE(5L, "Alumno visitante"),
    BACHILLER(6L, "Bachiller"),
    BACHILLERCONFECHAEGRESO(7L, "Bachiller con fecha egreso"),
    COLEGIATURA(8L, "Colegiatura"),
    COMBINANDOTERICIOYQUINTO(9L, "Combinando tericio y quinto"),
    COMPARATIVO(10L, "Comparativo"),
    //COMPARATIVO(11L,"Comparativo"),
    CUADRODEHORAS(12L, "Cuadro de horas"),
    ESCUELANACIONALDEAGRICULTURAESPECIAL(13L, "Escuela nacional de agricultura especial"),
    ESPECIALCOMPARATIVOYPORCENTAJE(14L, "Especial comparativo y porcentaje"),
    ESPECIALCONTINUARESTUDIOSENELEXTRANJERO(15L, "Especial continuar estudios en el extranjero"),
    ESPECIALCONVERSIONDESISTEMACALIFICACION(16L, "Especial conversión de sistema calificación"),
    ESPECIALDURACIONCICLO(17L, "Especial duración ciclo"),
    ESPECIALDURACIONDECICLO(18L, "Especial duración de ciclo"),
    ESPECIALPRIMERAMATRICULA(19L, "Especial primera matrícula"),
    ESPECIALPROMEDIOACUMULADODELOSCICLOS(20L, "Especial promedio acumulado deLos ciclos"),
    ESPECIALPROMEDIOVIGESIMAL(21L, "Especial promedio vigesimal"),
    ESTUDIOSININTERRUMPIDOSOCONTINUOS(22L, "Estudios ininterrumpidos ó continuos"),
    NIVELACADEMICO(23L, "Nivel Académico"),
    NIVELACADEMICODEEXALUMNOS(24L, "Nivel académico de ex-alumnos"),
    NOSEPARADO(25L, "No separado"),
    ORDENDEMERITO(26L, "Orden de merito"),
    ORDENDEMERITOALUMNO(27L, "Orden de merito alumno"),
    ORDENDEMERITOALUMNOSVARIOS(28L, "Orden de merito alumnos varios"),
    ORDENDEMERITOEGRESADOFACULTADESPECIALIDADPROMOCION(29L, "Orden de merito egresado facultad especialidad promoción"),
    ORDENDEMERITOEGRESADOVARIOS(30L, "Orden de merito egresado varios"),
    //ORDENDEMERITO(31L, "Orden de merito"),
    ORDENDEVARIOS(32L, "Orden de varios"),
    ORDENMERITOCONTERCIOYQUINTO(33L, "Orden merito con tercio y quinto"),
    QUINTOSUPERIORALUMNO(34L, "Quinto superior alumno"),
    QUINTOSUPERIORVARIOS(35L, "Quinto superior varios"),
    SISTEMACALIFICACION(36L, "Sistema calificacion"),
    TEORIAPRACTICACREDITO(37L, "Teoría Practica Crédito"),
    TERCIODELOSCICLOS(38L, "Tercio deLos ciclos"),
    TERCIOSUPERIOR(39L, "Tercio superior"),
    TERCIOQUINTOCOMBINADOS(40L, "Tercio quinto combinados"),
    TITULO(41L, "Titulo"),
    CURSOSDELPRIMERCICLO(42L, "Cursos del primer ciclo");

    private final Long value;

    private final String nombre;
    private static final Map<Long, TipoDocumentoAcademicoEnum> lookup = new HashMap<>();
    private static final Map<String, TipoDocumentoAcademicoEnum> lookname = new HashMap<>();

    static {
        for (TipoDocumentoAcademicoEnum d : TipoDocumentoAcademicoEnum.values()) {
            lookup.put(d.getValue(), d);
            lookname.put(d.getNombre(), d);
        }
    }

    private TipoDocumentoAcademicoEnum(Long value, String nombre) {
        this.value = value;
        this.nombre = nombre;
    }

    public Long getValue() {
        return this.value;
    }
    public String getNombre() {
        return this.nombre;
    }

    public static TipoDocumentoAcademicoEnum get(String valor) {
        return lookup.get(valor);
    }

    public static TipoDocumentoAcademicoEnum getValor(String uppername) {
        return lookname.get(uppername);
    }

}
