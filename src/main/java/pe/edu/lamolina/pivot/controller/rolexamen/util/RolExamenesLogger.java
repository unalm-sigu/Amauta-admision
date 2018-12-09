package pe.edu.lamolina.pivot.controller.rolexamen.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;

@Component
public class RolExamenesLogger {

    private String tipo;
    private String message;
    private boolean running;

    private List<RolExamenesLogger> logDetails;

    public void iniciarCursoMasivo() {
        this.tipo = TipoRolExamenesLoggerEnum.CUR_MAS.name();
        this.message = "Calculo de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList<>();
    }

    public void iniciarTrasladoToCursoMasivo() {
        this.tipo = TipoRolExamenesLoggerEnum.TRAS_TO_CUR_MAS.name();
        this.message = "Proceso de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList<>();
    }

    public void iniciarTrasladoToGrupoRegular() {
        this.tipo = TipoRolExamenesLoggerEnum.TRAS_TO_GPO_REG.name();
        this.message = "Proceso de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList<>();
    }

    public void iniciarGrupoEspecial() {
        this.tipo = TipoRolExamenesLoggerEnum.GPO_ESP.name();
        this.message = "Calculo de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList<>();
    }

    public void iniciarGrupoRegular() {
        this.tipo = TipoRolExamenesLoggerEnum.GPO_REG.name();
        this.message = "Calculo de " + this.getTipoEnum().getValue();
        this.running = true;
        this.logDetails = new ArrayList<>();
    }

    public RolExamenesLogger() {
    }

    public void cruceDocente(Docente docente, Curso curso) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_DOC);

        String msg = "Conflicto del docente %s - %s, con el curso masivo %s - %s";
        String complexMsg = String.format(msg, docente.getCodigo(), docente.getPersona().getApellidosNombres(), curso.getCodigo(), curso.getNombre());
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceDocente(Docente docente, SeccionGrupoEspecial seccionGrupoEspecial) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_DOC);

        Seccion seccion = seccionGrupoEspecial.getSeccion();

        String msg = "Conflicto del docente %s - %s, con la seccion especial %s";
        String complexMsg = String.format(msg, docente.getCodigo(), docente.getPersona().getApellidosNombres(), seccion.getCodigo2());
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceAlumno(Alumno alumno, Curso curso) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_ALU);

        String msg = "Conflicto del Alumno %s - %s, con el curso masivo %s - %s";
        String complexMsg = String.format(msg, alumno.getCodigo(), alumno.getPersona().getApellidosNombres(), curso.getCodigo(), curso.getNombre());
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceAlumno(Alumno alumno, SeccionGrupoEspecial seccionGrupoEspecial) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_ALU);

        Seccion seccion = seccionGrupoEspecial.getSeccion();

        String msg = "Conflicto del Alumno %s - %s, con la seccion especial %s";
        String complexMsg = String.format(msg, alumno.getCodigo(), alumno.getPersona().getApellidosNombres(), seccion.getCodigo2());
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceAlumno(Alumno alumno, LetraGrupoRegular letraGrupoRegular, Seccion seccion) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_ALU);

        String msg = "Conflicto del Alumno %s - %s, con el grupo letra %s y la seccion %s";
        String complexMsg = String.format(
                msg, alumno.getCodigo(), alumno.getPersona().getApellidosNombres(),
                letraGrupoRegular.getLetra(),
                seccion.getCodigo2()
        );
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceDocente(Docente docente, LetraGrupoRegular letraGrupoRegular, Seccion seccion) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_DOC);

        String msg = "Conflicto del docente %s - %s, con el grupo letra %s y la seccion %s";
        String complexMsg = String.format(
                msg, docente.getCodigo(), docente.getPersona().getApellidosNombres(),
                letraGrupoRegular.getLetra(),
                seccion.getCodigo2()
        );
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceAula(Aula aula, Curso curso) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_AUL);

        String msg = "Conflicto del Aula %s, con el curso masivo %s - %s";
        String complexMsg = String.format(msg, aula.getCodigo(), curso.getCodigo(), curso.getNombre());
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceAula(Aula aula, LetraGrupoRegular letraGrupoRegular, Seccion seccion) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_AUL);

        String msg = "Conflicto del aula %s, con el grupo letra %s y la seccion %s";
        String complexMsg = String.format(
                msg, aula.getCodigo(),
                letraGrupoRegular.getLetra(),
                seccion.getCodigo2()
        );
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void cruceAula(Aula aula, SeccionGrupoEspecial seccionGrupoEspecial) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.setTipoEnum(TipoRolExamenesLoggerEnum.CRU_AUL);

        Seccion seccion = seccionGrupoEspecial.getSeccion();

        String msg = "Conflicto del aula %s, con la seccion especial %s";
        String complexMsg = String.format(
                msg, aula.getCodigo(),
                seccion.getCodigo2()
        );
        rolExamenesLogger.setMessage(complexMsg);
        this.logDetails.add(rolExamenesLogger);
    }

    public void finalizeLog() {
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

}
