package pe.edu.lamolina.amauta.controller.consejeria.agendartutorado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.consejeria.plantutoria.PlanTutoriaService;
import pe.edu.lamolina.amauta.controller.mensajeria.chatunalm.ChatUnalmService;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.CitaConsejeroAlumnoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ObjetivoCitaConsejeroDAO;
import pe.edu.lamolina.amauta.dao.mensajeria.MensajeSistemaDAO;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.enums.NombreTablasEnum.TUTO_CITA_CONSEJERO_ALUMNO;
import pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.CANCELADA;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.NO_ASISTIO;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.PENDIENTE;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.REALIZADA;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.REPROGRAMADA;
import pe.edu.lamolina.model.enums.mensajeria.EstadoMensajeEnum;
import pe.edu.lamolina.model.enums.mensajeria.TipoAsuntoMensajeEnum;
import pe.edu.lamolina.model.social.AsuntoMensaje;
import pe.edu.lamolina.model.social.MensajeSistema;
import pe.edu.lamolina.model.tutoria.CitaConsejeroAlumno;
import pe.edu.lamolina.model.tutoria.ObjetivoCitaConsejero;
import pe.edu.lamolina.model.tutoria.PlanTutorial;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AgendarTutoradoServiceImpl implements AgendarTutoradoService {

    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final CitaConsejeroAlumnoDAO citaConsejeroAlumnoDAO;
    private final MensajeSistemaDAO mensajeSistemaDAO;
    private final ObjetivoCitaConsejeroDAO objetivoCitaConsejeroDAO;

    private final ChatUnalmService chatUnalmService;
    private final PlanTutoriaService planTutoriaService;

    @Override
    public List<CitaConsejeroAlumno> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        boolean tienePermiso = planTutoriaService.tienePermiso(alumno, ciclo, ds);
        if (!tienePermiso) {
            return new ArrayList();
        }

        List<CitaConsejeroAlumno> citas = citaConsejeroAlumnoDAO.allByDynatable(filter, alumno, ciclo);
        List<ObjetivoCitaConsejero> objetivosAll = objetivoCitaConsejeroDAO.allByCitas(citas);
        Map<Long, List<ObjetivoCitaConsejero>> mapObjetivos = TypesUtil.convertListToMapList("citaConsejeroAlumno.id", objetivosAll);

        citas.forEach(cita -> {
            List<ObjetivoCitaConsejero> objetivos = TypesUtil.getListNotNull(mapObjetivos.get(cita.getId()));
            List<PlanTutorial> planes = objetivos.stream()
                    .map(obj -> obj.getObjetivoTutorial())
                    .collect(Collectors.toList());

            cita.setObjetivos(objetivos);
            cita.setPlanesTutoriales(planes);
        });

        return citas;
    }

    @Override
    @Transactional
    public void saveCitaTutorizada(CitaConsejeroAlumno citaForm, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {
        LocalDate hoy = new LocalDate();

        boolean esConsejero = planTutoriaService.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso de crear citas con el tutorado");

        Assert.isNotNull(citaForm.getFecha(), "No ha indicado la fecha");
        Assert.isNotNull(citaForm.getHora(), "No ha indicado la hora");
        Assert.isNotNull(citaForm.getAsunto(), "No ha indicado el asunto");
        Assert.isNotNull(citaForm.getPlanesTutoriales(), "No ha indicado los objetivos");
        Assert.isFalse(citaForm.getPlanesTutoriales().isEmpty(), "No ha indicado los objetivos");
        Assert.isTrue(citaForm.getFecha().compareTo(hoy.toDate()) >= 0, "No se puede programar citas en fechas pasadas");

        List<CitaConsejeroAlumno> citas = citaConsejeroAlumnoDAO.allByAlumnoFecha(alumno, citaForm.getFecha());
        Assert.isTrue(citas.isEmpty(), "Este tutorado ya tiene programada una cita para esta fecha");

        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ciclo);
        List<CitaConsejeroAlumno> citasPasadas = citaConsejeroAlumnoDAO.allUltimosByAlumnoCiclo(alumno, ciclo);

        boolean esCitaNueva = true;
        this.crearCita(citaForm, null, alumno, ciclo, alumnoConsejero.getConsejero(), citasPasadas, ds, esCitaNueva);

    }

    @Override
    @Transactional
    public void cancelarCitaTutorado(CitaConsejeroAlumno citaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        CitaConsejeroAlumno cita = citaConsejeroAlumnoDAO.find(citaForm.getId());
        Assert.isNotNull(cita, "No se ha ubicado el registro que desea cancelar");
        Assert.isTrue(cita.getEstadoEnum() == PENDIENTE, "Solo citas pendientes se pueden cancelar");

        Alumno alumno = cita.getAlumno();
        boolean esConsejero = planTutoriaService.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso de cancelar las citas de este tutorado");

        long time = System.currentTimeMillis();
        long pasado = cita.getFechaRegistro().getTime();
        long diff = time - pasado;

        if (diff < 1000 * 60 * 5) {
            List<ObjetivoCitaConsejero> objetivos = objetivoCitaConsejeroDAO.allByCita(cita);
            objetivos.forEach(objetivo -> objetivoCitaConsejeroDAO.delete(objetivo));

            citaConsejeroAlumnoDAO.delete(cita);

            CitaConsejeroAlumno ultimaCita = citaConsejeroAlumnoDAO.findUltimoByAlumnoCiclo(alumno, ciclo);
            if (ultimaCita != null) {
                ultimaCita.setUltimoMensaje(true);
                citaConsejeroAlumnoDAO.update(ultimaCita);
            }

            this.anularMensaje(cita, alumno, ds);
            return;
        }

        cita.setEstadoEnum(CANCELADA);
        cita.setUserModificacion(ds.getUsuario());
        cita.setFechaModificacion(today.toDate());
        citaConsejeroAlumnoDAO.update(cita);

        this.anularMensaje(cita, alumno, ds);

    }

    private void anularMensaje(CitaConsejeroAlumno cita, Alumno alumno, DataSessionPivot ds) {
        MensajeSistema mensaje = mensajeSistemaDAO.findByTablaInstancia(TUTO_CITA_CONSEJERO_ALUMNO, cita.getId());
        if (mensaje == null) {
            return;
        }

        if (mensaje.getEstadoEnum() == EstadoMensajeEnum.ENVIADO) {
            mensaje.setEstadoEnum(EstadoMensajeEnum.ANULADO);
            mensaje.setUserAnulacion(ds.getUsuario());
            mensaje.setFechaAnulacion(new Date());
            mensajeSistemaDAO.update(mensaje);

            chatUnalmService.enviarMensajeChat(mensaje);
            return;
        }

        if (mensaje.getEstadoEnum() == EstadoMensajeEnum.LEIDO) {
            TipoAsuntoMensajeEnum tipoAsunto = TipoAsuntoMensajeEnum.CITA_TUTOR_ANULADA;
            String contenido = chatUnalmService.crearContenido(tipoAsunto, cita);

            AsuntoMensaje asunto = new AsuntoMensaje(
                    tipoAsunto.getValue(),
                    TUTO_CITA_CONSEJERO_ALUMNO,
                    cita.getId());
            chatUnalmService.crearMensaje(asunto, contenido, ds.getDocente(), alumno, ds);

            MensajeSistema mensajeDos = chatUnalmService.crearMensaje(asunto, contenido, ds.getDocente(), alumno, ds);
            chatUnalmService.enviarMensajeChat(mensajeDos);
        }
    }

    @Override
    @Transactional
    public void updateCitaTutorado(CitaConsejeroAlumno citaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        CitaConsejeroAlumno cita = citaConsejeroAlumnoDAO.find(citaForm.getId());
        Assert.isNotNull(cita, "No se ha ubicado el registro que desea modificar");
        Assert.isTrue(cita.getEstadoEnum() == PENDIENTE, "Solo citas pendientes se pueden modificar");

        Alumno alumno = cita.getAlumno();
        boolean esConsejero = planTutoriaService.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso para modificar las citas de este tutorado");

        List<ObjetivoCitaConsejero> objetivos = objetivoCitaConsejeroDAO.allByCita(cita);
        Map<Long, ObjetivoCitaConsejero> mapObjetivos = objetivos.stream()
                .collect(Collectors.toMap(obj -> obj.getObjetivoTutorial().getId(), Function.identity()));

        List<PlanTutorial> planesBD = objetivos.stream()
                .map(obj -> obj.getObjetivoTutorial())
                .collect(Collectors.toList());

        Acumulador acumulador = new Acumulador();
        ListsInspector inspector = TypesUtil.analizeLists(planesBD, citaForm.getPlanesTutoriales(), "id");

        List<PlanTutorial> nuevos = inspector.getNewList();
        nuevos.forEach(plan -> {
            ObjetivoCitaConsejero objetivo = new ObjetivoCitaConsejero();
            objetivo.setCitaConsejeroAlumno(cita);
            objetivo.setObjetivoTutorial(plan);
            objetivo.setUserRegistro(ds.getUsuario());
            objetivo.setFechaRegistro(today.toDate());
            objetivoCitaConsejeroDAO.save(objetivo);
            acumulador.incrementar();
        });

        List<PlanTutorial> eliminables = inspector.getDeadList();
        eliminables.forEach(plan -> {
            ObjetivoCitaConsejero objetivo = mapObjetivos.get(plan.getId());
            objetivoCitaConsejeroDAO.delete(objetivo);
            acumulador.incrementar();
        });

        if (!citaForm.getAsunto().equals(cita.getAsunto())) {
            acumulador.incrementar();
        }

        Assert.isTrue(acumulador.getValor() > 0, "No ha enviado cambios que registrar");
        cita.setAsunto(citaForm.getAsunto());
        cita.setUserModificacion(ds.getUsuario());
        cita.setFechaModificacion(today.toDate());
        citaConsejeroAlumnoDAO.update(cita);

    }

    @Override
    @Transactional
    public void postergarCitaTutorado(CitaConsejeroAlumno citaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        LocalDate hoy = new LocalDate();
        List<EstadoCitaTutorEnum> estados = Arrays.asList(PENDIENTE, CANCELADA);

        CitaConsejeroAlumno citaAntes = citaConsejeroAlumnoDAO.find(citaForm.getId());
        Assert.isNotNull(citaAntes, "No se ha ubicado la cita que desea postergar");
        Assert.isTrue(estados.contains(citaAntes.getEstadoEnum()), "Esta cita no puede ser reprogramada");

        Alumno alumno = citaAntes.getAlumno();
        boolean esConsejero = planTutoriaService.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso para modificar las citas de este tutorado");

        Assert.isNotNull(citaForm.getFecha(), "No ha indicado la fecha");
        Assert.isNotNull(citaForm.getHora(), "No ha indicado la hora");
        Assert.isNotNull(citaForm.getAsunto(), "No ha indicado el asunto");
        Assert.isNotNull(citaForm.getMotivoPostergacion(), "No ha indicado el motivo de la postergación");
        Assert.isNotNull(citaForm.getPlanesTutoriales(), "No ha indicado los objetivos");
        Assert.isFalse(citaForm.getPlanesTutoriales().isEmpty(), "No ha indicado los objetivos");
        Assert.isTrue(citaForm.getFecha().compareTo(hoy.toDate()) >= 0, "No se puede programar citas en fechas pasadas");

        List<CitaConsejeroAlumno> citasAll = citaConsejeroAlumnoDAO.allByAlumnoFecha(alumno, citaForm.getFecha());
        List<CitaConsejeroAlumno> citas = citasAll.stream()
                .filter(otraCita -> !otraCita.getId().equals(citaAntes.getId()))
                .collect(Collectors.toList());
        Assert.isTrue(citas.isEmpty(), "Este tutorado ya tiene programada una cita para esta fecha");

        String fechaAntes = this.getFechaHora(citaAntes);
        String fechaAhora = this.getFechaHora(citaForm);
        Assert.isFalse(fechaAhora.equals(fechaAntes), "La nueva fecha y hora es la misma que la cita anterior");

        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ciclo);
        List<CitaConsejeroAlumno> citasPasadas = citaConsejeroAlumnoDAO.allUltimosByAlumnoCiclo(alumno, ciclo);

        citaAntes.setMotivoPostergacion(citaForm.getMotivoPostergacion());
        citaAntes.setEstadoEnum(REPROGRAMADA);
        citaAntes.setPostergado(true);
        citaAntes.setUserModificacion(ds.getUsuario());
        citaAntes.setFechaModificacion(today.toDate());
        citaConsejeroAlumnoDAO.update(citaAntes);

        boolean esCitaPostergada = false;
        CitaConsejeroAlumno citaNueva = this.crearCita(citaForm, citaForm, alumno, ciclo, alumnoConsejero.getConsejero(), citasPasadas, ds, esCitaPostergada);
        this.notificarReprogramacion(citaAntes, citaNueva, alumno, ds);
    }

    private CitaConsejeroAlumno crearCita(
            CitaConsejeroAlumno citaForm,
            CitaConsejeroAlumno citaPostergada,
            Alumno alumno,
            CicloAcademico ciclo,
            Consejero consejero,
            List<CitaConsejeroAlumno> citasPasadas,
            DataSessionPivot ds,
            boolean esCitaNueva) {

        DateTime today = new DateTime();
        CitaConsejeroAlumno newCita = new CitaConsejeroAlumno();
        newCita.setAlumno(alumno);
        newCita.setCicloAcademico(ciclo);
        newCita.setConsejero(consejero);
        newCita.setEstadoEnum(PENDIENTE);
        newCita.setPostergado(false);
        newCita.setUltimoMensaje(true);
        newCita.setCitaPostergada(citaPostergada);
        newCita.setFecha(citaForm.getFecha());
        newCita.setHora(citaForm.getHora());
        newCita.setAsunto(citaForm.getAsunto());
        newCita.setUserRegistro(ds.getUsuario());
        newCita.setFechaRegistro(today.toDate());
        citaConsejeroAlumnoDAO.save(newCita);

        citaForm.getPlanesTutoriales().forEach(plan -> {
            ObjetivoCitaConsejero objetivo = new ObjetivoCitaConsejero();
            objetivo.setCitaConsejeroAlumno(newCita);
            objetivo.setObjetivoTutorial(plan);
            objetivo.setUserRegistro(ds.getUsuario());
            objetivo.setFechaRegistro(today.toDate());
            objetivoCitaConsejeroDAO.save(objetivo);
        });

        citasPasadas.forEach(citaPasada -> {
            citaPasada.setUltimoMensaje(false);
            citaConsejeroAlumnoDAO.update(citaPasada);
        });

        if (esCitaNueva) {
            TipoAsuntoMensajeEnum tipoAsunto = TipoAsuntoMensajeEnum.CITA_TUTOR;
            String contenido = chatUnalmService.crearContenido(tipoAsunto, newCita);

            AsuntoMensaje asunto = new AsuntoMensaje(
                    tipoAsunto.getValue(),
                    TUTO_CITA_CONSEJERO_ALUMNO,
                    newCita.getId());

            MensajeSistema mensaje = chatUnalmService.crearMensaje(asunto, contenido, ds.getDocente(), alumno, ds);
            chatUnalmService.enviarMensajeChat(mensaje);
        }
        return newCita;
    }

    private void notificarReprogramacion(CitaConsejeroAlumno citaCancelada, CitaConsejeroAlumno citaNueva, Alumno alumno, DataSessionPivot ds) {
        MensajeSistema mensajeCitaCancelada = mensajeSistemaDAO.findByTablaInstancia(TUTO_CITA_CONSEJERO_ALUMNO, citaCancelada.getId());

        if (mensajeCitaCancelada == null) {
            TipoAsuntoMensajeEnum tipoAsunto = TipoAsuntoMensajeEnum.CITA_TUTOR;
            String contenido = chatUnalmService.crearContenido(tipoAsunto, citaNueva);

            AsuntoMensaje asunto = new AsuntoMensaje(
                    tipoAsunto.getValue(),
                    TUTO_CITA_CONSEJERO_ALUMNO,
                    citaNueva.getId());

            MensajeSistema mensaje = chatUnalmService.crearMensaje(asunto, contenido, ds.getDocente(), alumno, ds);
            chatUnalmService.enviarMensajeChat(mensaje);
            return;
        }

        if (mensajeCitaCancelada.getEstadoEnum() == EstadoMensajeEnum.ENVIADO) {
            {
                mensajeCitaCancelada.setEstadoEnum(EstadoMensajeEnum.ANULADO);
                mensajeCitaCancelada.setUserAnulacion(ds.getUsuario());
                mensajeCitaCancelada.setFechaAnulacion(new Date());
                mensajeSistemaDAO.update(mensajeCitaCancelada);

                chatUnalmService.enviarMensajeChat(mensajeCitaCancelada);
            }

            {
                TipoAsuntoMensajeEnum tipoAsunto = TipoAsuntoMensajeEnum.CITA_TUTOR;
                String contenido = chatUnalmService.crearContenido(tipoAsunto, citaNueva);

                AsuntoMensaje asunto = new AsuntoMensaje(
                        tipoAsunto.getValue(),
                        TUTO_CITA_CONSEJERO_ALUMNO,
                        citaNueva.getId());

                MensajeSistema mensaje = chatUnalmService.crearMensaje(asunto, contenido, ds.getDocente(), alumno, ds);
                chatUnalmService.enviarMensajeChatDelay(mensaje, 2000);
            }
            return;
        }

        if (mensajeCitaCancelada.getEstadoEnum() == EstadoMensajeEnum.LEIDO) {
            TipoAsuntoMensajeEnum tipoAsunto = TipoAsuntoMensajeEnum.CITA_TUTOR_POSTERGADA;
            String contenido = chatUnalmService.crearContenido(tipoAsunto, citaNueva);

            AsuntoMensaje asunto = new AsuntoMensaje(
                    tipoAsunto.getValue(),
                    TUTO_CITA_CONSEJERO_ALUMNO,
                    citaNueva.getId());

            MensajeSistema mensaje = chatUnalmService.crearMensaje(asunto, contenido, ds.getDocente(), alumno, ds);
            chatUnalmService.enviarMensajeChat(mensaje);
        }
    }

    private String getFechaHora(CitaConsejeroAlumno cita) {
        LocalDate fecha = new LocalDate(cita.getFecha());
        return fecha.toString("dd/MM/yyyy") + "-" + cita.getHora();
    }

    @Override
    @Transactional
    public void marcarAsistenciaCita(CitaConsejeroAlumno citaForm, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        CitaConsejeroAlumno cita = citaConsejeroAlumnoDAO.find(citaForm.getId());
        Assert.isNotNull(cita, "No se ha ubicado el registro de la cita");
        Assert.isTrue(cita.getEstadoEnum() == PENDIENTE, "Esta opción solo aplica a citas pendientes");

        Alumno alumno = cita.getAlumno();
        boolean esConsejero = planTutoriaService.verificarConsejero(alumno, ciclo, ds);
        Assert.isTrue(esConsejero, "Usted no tiene permiso para marcar la asistencia de esta cita");

        Assert.isNotNull(citaForm.getEstado(), "No ha indicado si asistió o no a la cita");
        List<EstadoCitaTutorEnum> estados = Arrays.asList(REALIZADA, NO_ASISTIO);
        Assert.isTrue(estados.contains(citaForm.getEstadoEnum()), "No ha indicado si asistió o no a la cita");

        if (citaForm.getEstadoEnum() == REALIZADA) {
            Assert.isNotNull(citaForm.getFechaRealizada(), "No ha indicado la fecha cuando se realizó la cita");
            Assert.isNotNull(citaForm.getHoraInicio(), "No ha indicado la hora de inicio de la cita");
            Assert.isNotNull(citaForm.getHoraFin(), "No ha indicado la hora fina de la cita");

        } else {
            citaForm.setFechaRealizada(null);
            citaForm.setHoraInicio(null);
            citaForm.setHoraFin(null);
        }

        cita.setEstadoEnum(citaForm.getEstadoEnum());
        cita.setConclusiones(citaForm.getConclusiones());
        cita.setFechaRealizada(citaForm.getFechaRealizada());
        cita.setHoraInicio(citaForm.getHoraInicio());
        cita.setHoraFin(citaForm.getHoraFin());
        cita.setUserModificacion(ds.getUsuario());
        cita.setFechaModificacion(today.toDate());
        citaConsejeroAlumnoDAO.update(cita);
    }

}
