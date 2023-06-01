package pe.edu.lamolina.amauta.controller.consejeria.agendartutorado;

import java.util.ArrayList;
import java.util.Arrays;
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
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.CitaConsejeroAlumnoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ObjetivoCitaConsejeroDAO;
import pe.edu.lamolina.amauta.zelper.misc.Acumulador;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.CANCELADA;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.NO_ASISTIO;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.PENDIENTE;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.REALIZADA;
import static pe.edu.lamolina.model.enums.consejeria.EstadoCitaTutorEnum.REPROGRAMADA;
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
    private final ObjetivoCitaConsejeroDAO objetivoCitaConsejeroDAO;

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

        this.crearCita(citaForm, null, alumno, ciclo, alumnoConsejero.getConsejero(), citasPasadas, ds);

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
            return;
        }

        cita.setEstadoEnum(CANCELADA);
        cita.setUserModificacion(ds.getUsuario());
        cita.setFechaModificacion(today.toDate());
        citaConsejeroAlumnoDAO.update(cita);
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

        CitaConsejeroAlumno cita = citaConsejeroAlumnoDAO.find(citaForm.getId());
        Assert.isNotNull(cita, "No se ha ubicado la cita que desea postergar");
        Assert.isTrue(estados.contains(cita.getEstadoEnum()), "Esta cita no puede ser reprogramada");

        Alumno alumno = cita.getAlumno();
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
                .filter(otraCita -> !otraCita.getId().equals(cita.getId()))
                .collect(Collectors.toList());
        Assert.isTrue(citas.isEmpty(), "Este tutorado ya tiene programada una cita para esta fecha");

        String fechaAntes = this.getFechaHora(cita);
        String fechaAhora = this.getFechaHora(citaForm);
        Assert.isFalse(fechaAhora.equals(fechaAntes), "La nueva fecha y hora es la misma que la cita anterior");

        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ciclo);
        List<CitaConsejeroAlumno> citasPasadas = citaConsejeroAlumnoDAO.allUltimosByAlumnoCiclo(alumno, ciclo);

        cita.setMotivoPostergacion(citaForm.getMotivoPostergacion());
        cita.setEstadoEnum(REPROGRAMADA);
        cita.setPostergado(true);
        cita.setUserModificacion(ds.getUsuario());
        cita.setFechaModificacion(today.toDate());
        citaConsejeroAlumnoDAO.update(cita);

        this.crearCita(citaForm, citaForm, alumno, ciclo, alumnoConsejero.getConsejero(), citasPasadas, ds);

    }

    private void crearCita(
            CitaConsejeroAlumno citaForm,
            CitaConsejeroAlumno citaPostergada,
            Alumno alumno,
            CicloAcademico ciclo,
            Consejero consejero,
            List<CitaConsejeroAlumno> citasPasadas,
            DataSessionPivot ds) {

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

        cita.setEstadoEnum(citaForm.getEstadoEnum());
        cita.setUserModificacion(ds.getUsuario());
        cita.setFechaModificacion(today.toDate());
        citaConsejeroAlumnoDAO.update(cita);
    }

}
