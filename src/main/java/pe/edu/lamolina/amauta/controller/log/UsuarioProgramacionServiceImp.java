package pe.edu.lamolina.amauta.controller.log;

import java.util.List;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.log.UsuarioProgramacionDAO;
import pe.edu.lamolina.amauta.zelper.enums.TipoRestriccionEnum;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.log.UsuarioProgramacionLogger;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UsuarioProgramacionServiceImp implements UsuarioProgramacionService {

    @Autowired
    UsuarioProgramacionDAO usuarioProgramacionDAO;

    public static String UPDATED = "Registro actualizado";
    public static String CREATED = "Registro creado";
    public static String DELETED = "Registro eliminado";

    //
    public static String ERROR = "Error al registrar error";
    public static String CREATED_SECCION = "Creación de sección";
    public static String CREATED_DOCENTE_SECCION = "Creación de docente sección";
    public static String ASIGNACION_LETRA_GRUPO = "Asignacion grupo horas a la sección";
    public static String ASIGNACION_ADOCENTE = "Asignacion de docente a la sección";
    public static String ASIGNACION_AULA = "Asignacion de aula a la sección";
    public static String UPDATED_VACANTE_SECCION = "Se asigno %d vacantes a la sección";
    public static String ESTADO_SECCION = "Se %s la sección";
    public static String RESTRICCION_CAPE = "Actualizo la restriccion CAPE a %s";
    public static String RESTRICCION_REPIETENCIA = "Actualizo la restriccion repitencia a %s";
    public static String RESTRICCION_MODALIDAD = "Actualizo la restriccion de modalida del tipo %s";
    public static String CANCELAR_SECCION = "Se canceló la sección: %s";
    public static String BLOQUEAR_SECCION = "Se bloqueó la sección";

    @Override
    @Transactional
    public void creacionSeccion(Seccion seccion, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(CREATED_SECCION);
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void creacionDocenteSeccion(DocenteSeccion docSecc, Usuario usuario) {
        if (docSecc == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setDocenteSeccion(docSecc.getId());
        usuarioProgramacion.setObservacion(CREATED_DOCENTE_SECCION);
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    public void asignacionGrupoHoras(Seccion seccionDB, GrupoHoras gpoHoras, Usuario usuario) {
        if (seccionDB == null) {
            log.debug("{}", ERROR);
            return;
        }
        if (gpoHoras == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccionDB.getId());
        usuarioProgramacion.setGrupoHoras(gpoHoras.getId());
        usuarioProgramacion.setObservacion(ASIGNACION_LETRA_GRUPO);
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void asignacionDocente(Docente docente, DocenteSeccion docenteSeccion, Usuario usuario) {
        if (docente == null) {
            log.debug("{}", ERROR);
            return;
        }
        if (docenteSeccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setDocenteSeccion(docenteSeccion.getId());
        usuarioProgramacion.setDocente(docente.getId());
        usuarioProgramacion.setObservacion(ASIGNACION_ADOCENTE);
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    public void asignacionAula(Seccion seccion, Aula aula, Usuario usuario) {
        if (aula == null) {
            log.debug("{}", ERROR);
            return;
        }
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setAula(aula.getId());
        usuarioProgramacion.setObservacion(ASIGNACION_AULA);
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void asignarVacanteSeccion(Seccion seccion, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(String.format(UPDATED_VACANTE_SECCION, seccion.getVacantes()));
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void activarSeccion(Seccion seccion, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(String.format(ESTADO_SECCION, SeccionEstadoEnum.ACT.getValue()));
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void anularSeccion(Seccion seccion, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(String.format(ESTADO_SECCION, SeccionEstadoEnum.ANU.getValue()));
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void updateRestriccionCapa(Seccion seccionForm, Usuario usuario) {
        if (seccionForm == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccionForm.getId());
        usuarioProgramacion.setObservacion(String.format(RESTRICCION_CAPE, seccionForm.getRestriccionCapa()));
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void restriccionRepitencia(Seccion seccion, List<TipoRepitencia> tiposRestriccionesSeleccionados, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        StringJoiner sj = new StringJoiner(",");
        for (TipoRepitencia tiposRestriccionesSeleccionado : tiposRestriccionesSeleccionados) {
            sj.add(tiposRestriccionesSeleccionado.getNombre());

        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(String.format(RESTRICCION_REPIETENCIA, sj.toString()));
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void restriccionModalidad(Seccion seccion, TipoRestriccionEnum tipoRestriccionEnum, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(String.format(RESTRICCION_MODALIDAD, tipoRestriccionEnum.getValue()));
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void cancelarSeccion(Seccion seccion, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(String.format(CANCELAR_SECCION, seccion.getMotivoCancelacion()));
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

    @Override
    @Transactional
    public void bloquearSeccion(Seccion seccion, Usuario usuario) {
        if (seccion == null) {
            log.debug("{}", ERROR);
            return;
        }
        UsuarioProgramacionLogger usuarioProgramacion = new UsuarioProgramacionLogger(usuario);
        usuarioProgramacion.setSeccion(seccion.getId());
        usuarioProgramacion.setObservacion(BLOQUEAR_SECCION);
        usuarioProgramacionDAO.save(usuarioProgramacion);
    }

}
