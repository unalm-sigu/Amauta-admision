package pe.edu.lamolina.amauta.controller.consejeria.derivartutorado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.controller.consejeria.plantutoria.PlanTutoriaService;
import pe.edu.lamolina.amauta.controller.medico.paciente.PacienteService;
import pe.edu.lamolina.amauta.controller.mensajeria.chatunalm.ChatUnalmService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoDerivadoAtencionDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoAtencionTutoradoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoRemitenteDerivacionDAO;
import pe.edu.lamolina.amauta.dao.medico.DerivacionPacienteDAO;
import pe.edu.lamolina.amauta.dao.medico.EspecialidadMedicaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.constantines.BienestarConstantine;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.consejeria.EstadoDerivacionEnum;
import pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum;
import static pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum.AAEE;
import static pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum.CENMED;
import static pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum.PSICOLOGO;
import static pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum.PSICOPEDAGOGO;
import static pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum.TRABAJADORA_SOCIAL;
import static pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum.TUTOR;
import static pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum.TUTORES;
import pe.edu.lamolina.model.enums.medico.ConsultorioEnum;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.medico.DerivacionPaciente;
import pe.edu.lamolina.model.medico.EspecialidadMedica;
import pe.edu.lamolina.model.medico.Paciente;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.MensajeSistema;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;
import pe.edu.lamolina.model.tutoria.TipoAtencionTutorado;
import pe.edu.lamolina.model.tutoria.TipoRemitenteDerivacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class DerivarTutoradoServiceImpl implements DerivarTutoradoService {

    private final AlumnoDAO alumnoDAO;
    private final AlumnoDerivadoAtencionDAO alumnoDerivadoAtencionDAO;
    private final ConsejeroDAO consejeroDAO;
    private final DerivacionPacienteDAO derivacionPacienteDAO;
    private final EspecialidadMedicaDAO especialidadMedicaDAO;
    private final MatriculaCursoDAO matriculaCursoDAO;
    private final TipoAtencionTutoradoDAO tipoAtencionTutoradoDAO;
    private final TipoRemitenteDerivacionDAO tipoRemitenteDerivacionDAO;

    private final ChatUnalmService chatUnalmService;
    private final PlanTutoriaService planTutoriaService;
    private final PacienteService pacienteService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public List<AlumnoDerivadoAtencion> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        boolean tienePermiso = planTutoriaService.tienePermiso(alumno, ciclo, ds);
        if (!tienePermiso) {
            return new ArrayList();
        }
        List<AlumnoDerivadoAtencion> derivaciones = alumnoDerivadoAtencionDAO.allByDynatable(filter, alumno, ciclo);
        return derivaciones;
    }

    @Override
    public List<TipoAtencionTutorado> allTiposAtenciones() {
        return tipoAtencionTutoradoDAO.all();
    }

    @Override
    public List<Curso> allCursosMatriculados(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allByAlumnoCiclo(alumno, ciclo);

        return cursosMatriculados.stream()
                .map(matCurso -> matCurso.getCurso())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveDerivacion(Alumno alumnoForm, AlumnoDerivadoAtencion derivacionForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        LocalDate hoy = new LocalDate();

        boolean esConsejero = planTutoriaService.verificarConsejero(alumnoForm, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso de crear derivaciones a este estudiante");
        Assert.isNotNull(derivacionForm.getTipoAtencionTutorado(), "No ha indicado el tipo de atención");
        Assert.isNotNull(derivacionForm.getMotivoDerivacion(), "No ha indicado el motivo");

        Alumno alumno = alumnoDAO.findAllInfo(alumnoForm.getId());
        Consejero consejero = this.findConsejero(alumno, ds);

        TipoRemitenteDerivacion tipoRemitente = tipoRemitenteDerivacionDAO.findByCodigoNodo(TUTOR);
        TipoAtencionTutorado tipoAtencion = tipoAtencionTutoradoDAO.find(derivacionForm.getTipoAtencionTutorado().getId());

        AlumnoDerivadoAtencion derivacionTutor = new AlumnoDerivadoAtencion();
        derivacionTutor.setEstadoEnum(EstadoDerivacionEnum.PENDIENTE);
        derivacionTutor.setConsejero(consejero);
        derivacionTutor.setAlumno(alumno);
        derivacionTutor.setCicloAcademico(ciclo);
        derivacionTutor.setPersonaRemitente(ds.getPersona());
        derivacionTutor.setTipoRemitenteDerivacion(tipoRemitente);
        derivacionTutor.setTipoAtencionTutorado(tipoAtencion);
        derivacionTutor.setCurso(derivacionForm.getCurso());
        derivacionTutor.setMotivoDerivacion(derivacionForm.getMotivoDerivacion());
        derivacionTutor.setUserRegistro(ds.getUsuario());
        derivacionTutor.setFechaRegistro(today.toDate());
        alumnoDerivadoAtencionDAO.save(derivacionTutor);

        List<NodoDerivacionEnum> nodosCentroMedico = Arrays.asList(PSICOLOGO, PSICOPEDAGOGO);
        if (nodosCentroMedico.contains(tipoAtencion.getCodigoNodoEnum())) {
            LocalDate fechaPropuesta = this.plusDays(hoy, 2);
            Paciente paciente = pacienteService.findPaciente(alumno.getPersona(), ds);

            EspecialidadMedica especialidad = null;
            if (tipoAtencion.getCodigoNodoEnum() == PSICOLOGO) {
                especialidad = especialidadMedicaDAO.findByCodigoEnum(ConsultorioEnum.PSICO);
            } else if (tipoAtencion.getCodigoNodoEnum() == PSICOPEDAGOGO) {
                especialidad = especialidadMedicaDAO.findByCodigoEnum(ConsultorioEnum.PSICOPE);
            }

            DerivacionPaciente derivacion = new DerivacionPaciente();
            derivacion.setTipoOrigenEnum(TUTORES);
            derivacion.setTipoDestinoEnum(CENMED);
            derivacion.setOficinaDestino(new Oficina(OficinaEnum.CENMED));
            derivacion.setDerivacionOrigen(derivacionTutor);
            derivacion.setPaciente(paciente);
            derivacion.setPersona(alumno.getPersona());
            derivacion.setAlumno(alumno);
            derivacion.setConsejero(consejero);
            derivacion.setColaborador(consejero.getColaborador());
            derivacion.setEspecialidadDestino(especialidad);
            derivacion.setMotivo(derivacionTutor.getMotivoDerivacion());
            derivacion.setEstadoEnum(EstadoDerivacionEnum.PENDIENTE);
            derivacion.setFecha(fechaPropuesta.toDate());
            derivacion.setFechaPropuesta(fechaPropuesta.toDate());
            derivacion.setUserRegistro(ds.getUsuario());
            derivacion.setFechaRegistro(today.toDate());
            derivacionPacienteDAO.save(derivacion);
        }

        if (tipoAtencion.getCodigoNodoEnum() == TRABAJADORA_SOCIAL) {
            LocalDate fechaPropuesta = this.plusDays(hoy, 2);

            DerivacionPaciente derivacion = new DerivacionPaciente();
            derivacion.setTipoOrigenEnum(TUTORES);
            derivacion.setTipoDestinoEnum(AAEE);
            derivacion.setOficinaDestino(new Oficina(OficinaEnum.ASUNEST));
            derivacion.setDerivacionOrigen(derivacionTutor);
            derivacion.setPersona(alumno.getPersona());
            derivacion.setAlumno(alumno);
            derivacion.setConsejero(consejero);
            derivacion.setColaborador(consejero.getColaborador());
            derivacion.setMotivo(derivacionTutor.getMotivoDerivacion());
            derivacion.setEstadoEnum(EstadoDerivacionEnum.PENDIENTE);
            derivacion.setFecha(fechaPropuesta.toDate());
            derivacion.setFechaPropuesta(fechaPropuesta.toDate());
            derivacion.setUserRegistro(ds.getUsuario());
            derivacion.setFechaRegistro(today.toDate());
            derivacionPacienteDAO.save(derivacion);
        }

        TipoAsuntoMensajeEnum tipoAsunto = this.getTipoAsunto(tipoAtencion);
        if (tipoAsunto == null) {
            return;
        }

        Assert.isNotNull(tipoAsunto, "No se pudo determinar el tipo de asunto del mensaje al alumno");
        String contenido = chatUnalmService.crearContenido(tipoAsunto, derivacionTutor);
        Assert.isNotNull(contenido, "No se pudo determinar el contenido del mensaje al alumno");

        AsuntoMensaje asunto = new AsuntoMensaje(
                tipoAsunto.getValue(),
                NombreTablasEnum.TUTO_ALUMNO_DERIVADO_ATENCION,
                derivacionTutor.getId());
        MensajeSistema mensaje = chatUnalmService.enviarMensaje(asunto, contenido, ds.getDocente(), alumno, ds);
        mensaje.setHoraPrefija(mensaje.getHora());

        String msg = JaneHelper
                .from(mensaje)
                .only("id,mensaje,fechaRegistro,horaPrefija")
                .join("asuntoMensaje", "id,asunto")
                .join("remitente", "id,userWebsocket")
                .join("remitente.persona", "nombres,paterno,materno")
                .join("destinatario", "id,userWebsocket")
                .join("destinatario.persona", "nombres,paterno,materno")
                .json().toString();

        rabbitTemplate.convertAndSend(BienestarConstantine.QUEUE_CHAT_MAIPI, msg);

    }

    private TipoAsuntoMensajeEnum getTipoAsunto(TipoAtencionTutorado tipoAtencion) {
        if (tipoAtencion.getCodigoNodoEnum() == TRABAJADORA_SOCIAL) {
            return TipoAsuntoMensajeEnum.DERIVA_TUTOR_AAEE;
        } else if (tipoAtencion.getCodigoNodoEnum() == PSICOLOGO) {
            return TipoAsuntoMensajeEnum.DERIVA_TUTOR_PSICOLOGIA;
        } else if (tipoAtencion.getCodigoNodoEnum() == PSICOPEDAGOGO) {
            return TipoAsuntoMensajeEnum.DERIVA_TUTOR_PSICOPEDAGOGIA;
        }

        return null;
    }

    private Consejero findConsejero(Alumno alumno, DataSessionPivot ds) {
        if (alumno.getCarrera() == null) {
            alumno = alumnoDAO.findAllInfo(alumno.getId());
        }

        return consejeroDAO.findByPersonaCarrera(ds.getPersona(), alumno.getCarrera());
    }

    private LocalDate plusDays(LocalDate fecha, int dias) {
        LocalDate siguiente = new LocalDate(fecha.toDate().getTime());
        for (int i = 0; i < dias; i++) {
            siguiente = this.siguienteFecha(siguiente);
        }
        return siguiente;
    }

    private LocalDate siguienteFecha(LocalDate fecha) {
        LocalDate siguiente = fecha.plusDays(1);
        int diaSemana = siguiente.getDayOfWeek();
        if (Arrays.asList(6, 7).contains(diaSemana)) {
            return siguienteFecha(siguiente);
        }
        return siguiente;
    }

}
