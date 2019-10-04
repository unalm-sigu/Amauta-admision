package pe.edu.lamolina.pivot.controller.rolexamen.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

@Component
public class RolExamenesLogger {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Boolean SHOW_LOG = Boolean.FALSE;

    private Long rolExamenes;
    private String letra;
    private String tipo;
    private Integer levelMessage;
    private String message;
    private boolean running;
    private boolean cruce;
    private Integer maximoAforoAula;

    private List<Aula> aulas;
    private List<Aula> aulasOera;
    private Map<Long, List<HorarioAula>> horarioAulas;

    private List<RolExamenesLogger> logDetails;

    private List<GrupoHorasExamen> gruposHorasExamenes;

    public void iniciarGeneric() {
        this.running = true;
        this.logDetails = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public void iniciarCursoMasivo() {
        this.cruce = false;
        this.tipo = TipoRolExamenesLoggerEnum.CUR_MAS.name();
        this.message = "Calculo de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList();
        this.aulasOera = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public void activarCursoMasivo() {
        this.tipo = TipoRolExamenesLoggerEnum.ACT_CUR_MAS.name();
        this.message = "Proceso: " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList();
        this.aulasOera = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public void activarGrupoRegular() {
        this.tipo = TipoRolExamenesLoggerEnum.ACT_GPO_REG.name();
        this.message = "Proceso: " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList();
        this.aulasOera = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public void iniciarTrasladoToCursoMasivo() {
        this.tipo = TipoRolExamenesLoggerEnum.TRAS_TO_CUR_MAS.name();
        this.message = "Proceso de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public void iniciarTrasladoToGrupoRegular() {
        this.tipo = TipoRolExamenesLoggerEnum.TRAS_TO_GPO_REG.name();
        this.message = "Proceso de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public void iniciarGrupoEspecial() {
        this.tipo = TipoRolExamenesLoggerEnum.GPO_ESP.name();
        this.message = "Calculo de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public void iniciarGrupoRegular() {
        this.tipo = TipoRolExamenesLoggerEnum.GPO_REG.name();
        this.message = "Calculo de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList();
        this.aulasOera = new ArrayList();
        this.horarioAulas = new LinkedHashMap();
    }

    public RolExamenesLogger() {
    }

    public void addMessageLevel1(String message) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setLevelMessage(BigInteger.ONE.intValue());
        rolExamenesLogger.setMessage(message);
        this.addLogDetails(rolExamenesLogger);
    }

    public void addMessageLevel2(String message, String... params) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setLevelMessage(Integer.valueOf(2));
        if (params == null) {
            rolExamenesLogger.setMessage(message);
        } else {
            rolExamenesLogger.setMessage(String.format(message, params));
        }
        this.addLogDetails(rolExamenesLogger);
    }

    public void addMessageLevel3(String message, String... params) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setLevelMessage(Integer.valueOf(3));
        if (params == null) {
            rolExamenesLogger.setMessage(message);
        } else {
            rolExamenesLogger.setMessage(String.format(message, params));
        }
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceDocente(Docente docente, Curso curso) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_DOC);

        String msg = "Conflicto del docente %s - %s, con el curso masivo %s - %s";
        String complexMsg = String.format(msg, docente.getCodigo(), docente.getPersona().getApellidosNombres(), curso.getCodigo(), curso.getNombre());
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceDocente(Docente docente, SeccionGrupoEspecial seccionGrupoEspecial) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_DOC);

        Seccion seccion = seccionGrupoEspecial.getSeccion();

        String msg = "Conflicto del docente %s - %s, con la seccion especial %s";
        String complexMsg = String.format(msg, docente.getCodigo(), docente.getPersona().getApellidosNombres(), seccion.getCodigo2());
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceAlumno(Alumno alumno, Curso curso) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_ALU);

        String msg = "Conflicto del Alumno %s - %s, con el curso masivo %s - %s";
        String complexMsg = String.format(msg, alumno.getCodigo(), alumno.getPersona().getApellidosNombres(), curso.getCodigo(), curso.getNombre());
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceAlumno(Alumno alumno, SeccionGrupoEspecial seccionGrupoEspecial) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_ALU);

        Seccion seccion = seccionGrupoEspecial.getSeccion();

        String msg = "Conflicto del Alumno %s - %s, con la seccion especial %s";
        String complexMsg = String.format(msg, alumno.getCodigo(), alumno.getPersona().getApellidosNombres(), seccion.getCodigo2());
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceAlumno(Alumno alumno, LetraGrupoRegular letraGrupoRegular, Seccion seccion) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_ALU);

        String msg = "Conflicto del Alumno %s - %s, con el grupo letra %s y la seccion %s";
        String complexMsg = String.format(
                msg, alumno.getCodigo(), alumno.getPersona().getApellidosNombres(),
                letraGrupoRegular.getLetra(),
                seccion.getCodigo2()
        );
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceDocente(Docente docente, LetraGrupoRegular letraGrupoRegular, Seccion seccion) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_DOC);

        String msg = "Conflicto del docente %s - %s, con el grupo letra %s y la seccion %s";
        String complexMsg = String.format(
                msg,
                docente.getCodigo(),
                ObjectUtil.getParentTree(docente, "persona.apellidosNombres"),
                letraGrupoRegular.getLetra(),
                seccion.getCodigo2()
        );
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceAula(Aula aula, Curso curso) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_AUL);

        String msg = "Conflicto del Aula %s, con el curso masivo %s - %s";
        String complexMsg = String.format(msg, aula.getCodigo(), curso.getCodigo(), curso.getNombre());
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void aulaOcupada(Aula aula, GrupoHorasExamen grupoHorasExamen) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.AUL_OCUP);

        String msg = "El Aula %s, se encuentra ocupada para los horarios del grupo %s";
        String complexMsg = String.format(msg, aula.getCodigo(), grupoHorasExamen.getGrupoHoras().getCodigo());
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceAula(Aula aula, LetraGrupoRegular letraGrupoRegular, Seccion seccion) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_AUL);

        String msg = "Conflicto del aula %s, con el grupo letra %s y la seccion %s";
        String complexMsg = String.format(
                msg, aula.getCodigo(),
                letraGrupoRegular.getLetra(),
                seccion.getCodigo2()
        );

        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    public void cruceAula(Aula aula, SeccionGrupoEspecial seccionGrupoEspecial) {
        this.cruce = true;
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_AUL);

        Seccion seccion = seccionGrupoEspecial.getSeccion();

        String msg = "Conflicto del aula %s, con la seccion especial %s";
        String complexMsg = String.format(
                msg, aula.getCodigo(),
                seccion.getCodigo2()
        );
        rolExamenesLogger.setMessage(complexMsg);
        this.addLogDetails(rolExamenesLogger);
    }

    void addLogDetails(RolExamenesLogger rolExamenesLogger) {
        if (SHOW_LOG) {
            logger.debug(rolExamenesLogger.getMessage());
        }
        this.logDetails.add(rolExamenesLogger);
    }

    public void finalizeLog() {
        this.aulasOera = new ArrayList<>();
        this.running = Boolean.FALSE;
        this.logDetails = null;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public TipoRolExamenesLoggerEnum getTipoEnum() {
        if (this.tipo == null) {
            return null;
        }
        return TipoRolExamenesLoggerEnum.valueOf(this.tipo);
    }

    public void setTipoEnum(TipoRolExamenesLoggerEnum tipoRolExamenesLoggerEnum) {
        this.tipo = tipoRolExamenesLoggerEnum.name();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RolExamenesLogger> getLogDetails() {
        return this.logDetails;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isCruce() {
        return cruce;
    }

    public void setCruce(boolean cruce) {
        this.cruce = cruce;
    }

    public List<Aula> getAulasOera() {
        return aulasOera;
    }

    public void setAulasOera(List<Aula> aulasOera) {
        this.aulasOera = aulasOera;
    }

    public List<GrupoHorasExamen> getGruposHorasExamenes() {
        return gruposHorasExamenes;
    }

    public void setGruposHorasExamenes(List<GrupoHorasExamen> gruposHorasExamenes) {
        this.gruposHorasExamenes = gruposHorasExamenes;
    }

    public Integer getMaximoAforoAula() {
        return maximoAforoAula;
    }

    public void setMaximoAforoAula(Integer maximoAforoAula) {
        this.maximoAforoAula = maximoAforoAula;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public Long getRolExamenes() {
        return rolExamenes;
    }

    public void setRolExamenes(Long rolExamenes) {
        this.rolExamenes = rolExamenes;
    }

    public Integer getLevelMessage() {
        return levelMessage;
    }

    public void setLevelMessage(Integer levelMessage) {
        this.levelMessage = levelMessage;
    }

    public static Boolean getSHOW_LOG() {
        return SHOW_LOG;
    }

//    public static void setSHOW_LOG(Boolean SHOW_LOG) {
//        RolExamenesLogger.SHOW_LOG = SHOW_LOG;
//    }
    public List<Aula> getAulas() {
        return aulas;
    }

    public void setAulas(List<Aula> aulas) {
        this.aulas = aulas;
    }

    public Map<Long, List<HorarioAula>> getHorarioAulas() {
        return horarioAulas;
    }

    public void setHorarioAulas(Map<Long, List<HorarioAula>> horarioAulas) {
        this.horarioAulas = horarioAulas;
    }

}
