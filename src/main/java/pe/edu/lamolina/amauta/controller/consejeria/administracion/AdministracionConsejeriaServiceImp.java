package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.ClonarConsejerosDTO;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.FiltroReporteAgendaDTO;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.VerificadorClonacionConsejero;
import pe.edu.lamolina.amauta.controller.consejeria.consejeros.Aconsejado;
import pe.edu.lamolina.amauta.controller.consejeria.consejeros.ConsejeroEstado;
import pe.edu.lamolina.amauta.controller.reunionConsejero.ReunionConsejeroServiceImpl;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AgendaConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaHistorialDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaResumenDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ReunionAlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TutorSolicitudDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial.ConsejeriaHistorialEstado;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.AgendaConsejeroEstadoEnum;
import static pe.edu.lamolina.model.enums.AgendaConsejeroEstadoEnum.AGEN;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Colaborador;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class AdministracionConsejeriaServiceImp implements AdministracionConsejeriaService {

    private ConsejeriaHistorialDAO consejeriaHistorialDAO;
    private CicloAcademicoDAO cicloAcademicoDAO;
    private AlumnoConsejeroDAO alumnoConsejeroDAO;
    private ConsejeriaResumenDAO consejeriaResumenDAO;
    private ConsejeroDAO consejeroDAO;
    private TutorSolicitudDAO tutorSolicitudDAO;
    private ReunionAlumnoConsejeroDAO reunionAlumnoConsejeroDAO;
    private AgendaConsejeroDAO agendaConsejeroDAO;
    private CarreraDAO carreraDAO;
    private AlumnoDAO alumnoDAO;
    private VerificadorClonacionConsejero verificadorClonacionConsejero;
    private ColaboradorDAO colaboradorDAO;
    private DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<ConsejeriaHistorial> allConsejeriaHistorialByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {

        return consejeriaHistorialDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    public List<CicloAcademico> allCiclo() {
        return cicloAcademicoDAO.allPregradoByRangeCode(201510, 220000);
    }

    @Override
    @Transactional
    public void clonar(ClonarConsejerosDTO clonarDTO, DataSessionPivot ds) {

        if (verificadorClonacionConsejero == null) {
            verificadorClonacionConsejero = new VerificadorClonacionConsejero();
        }

        verificadorClonacionConsejero.setOcupado(true);

        int modeloCodigo = Integer.parseInt(clonarDTO.getModelo().getCodigo());
        int destinoCodigo = Integer.parseInt(clonarDTO.getDestino().getCodigo());
        log.debug("modeloCodigo {}", modeloCodigo);
        log.debug("destinoCodigo {}", destinoCodigo);
        if (modeloCodigo >= destinoCodigo) {
            throw new PhobosException("El ciclo modelo no puede ser mayor o igual al ciclo destino");
        }

        List<ConsejeriaResumen> resumenes = consejeriaResumenDAO.allByCiclo(clonarDTO.getModelo());

        if (resumenes.isEmpty()) {
            throw new PhobosException("No hay registros en el ciclo de modelo");
        }

        log.debug("deleteConsejeriaResumenByCiclo");
        consejeriaResumenDAO.deleteByCiclo(clonarDTO.getDestino());

        log.debug("deleteAlumnoConsejeroByCiclo");
        alumnoConsejeroDAO.deleteByCiclo(clonarDTO.getDestino());

        for (ConsejeriaResumen resumen : resumenes) {

            ConsejeriaResumen consejeriaResumen = new ConsejeriaResumen();
            consejeriaResumen.setCarrera(resumen.getCarrera());
            consejeriaResumen.setCicloAcademico(clonarDTO.getDestino());
            consejeriaResumen.setFechaActualizacion(new Date());
            consejeriaResumen.setFechaActualizacion(new Date());
            consejeriaResumenDAO.save(consejeriaResumen);

            List<AlumnoConsejero> alumnoConsejerosModelo = alumnoConsejeroDAO.allByCarreraCiclo(resumen.getCarrera(), clonarDTO.getModelo());
            List<AlumnoConsejero> alumnoConsejerosDestino = alumnoConsejeroDAO.allByCarreraCiclo(resumen.getCarrera(), clonarDTO.getDestino());

            Map<Long, AlumnoConsejero> alumnoConsejerosModeloMap = alumnoConsejerosModelo.stream().
                    collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));

            Map<Long, AlumnoConsejero> alumnoConsejerosDestinoMap = alumnoConsejerosDestino.stream().
                    collect(Collectors.toMap(x -> x.getAlumno().getId(), y -> y, (f, s) -> f));

            for (AlumnoConsejero alumnoConsejeroModelo : alumnoConsejerosModeloMap.values()) {

                AlumnoConsejero alumnoTutor = alumnoConsejerosDestinoMap.getOrDefault(alumnoConsejeroModelo.getAlumno().getId(), new AlumnoConsejero());
                if (alumnoConsejeroModelo.getAlumno().getCodigo().equals("20191006")) {
                    System.out.println("ALUMNO:: " + alumnoConsejeroModelo.getAlumno().getCodigo());
                }
                if (alumnoTutor.getId() != null) {

                    if (alumnoTutor.getConsejero() == null) {
                        if (alumnoConsejeroModelo.getConsejero() != null) {
                            alumnoTutor.setConsejero(alumnoConsejeroModelo.getConsejero());
                        } else {
                            alumnoTutor.setConsejero(new Consejero(GlobalConstantine.ID_CONSEJERO_NN));
                        }
                    }

                    if (alumnoTutor.getConsejero().getId().longValue() == GlobalConstantine.ID_CONSEJERO_NN) {
                        if (alumnoConsejeroModelo.getConsejero() != null) {
                            alumnoTutor.setConsejero(alumnoConsejeroModelo.getConsejero());
                        }
                    }

                    alumnoTutor.setEstadoEnum(ACT);
                    alumnoTutor.setFechaAsigna(new Date());
                    alumnoTutor.setUserAsigna(ds.getUsuario());
                    alumnoConsejeroDAO.update(alumnoTutor);

                } else {

                    if (alumnoConsejeroModelo.getConsejero() != null) {
                        alumnoTutor.setConsejero(alumnoConsejeroModelo.getConsejero());
                    } else {
                        alumnoTutor.setConsejero(new Consejero(GlobalConstantine.ID_CONSEJERO_NN));
                    }

                    alumnoTutor.setAlumno(alumnoConsejeroModelo.getAlumno());
                    alumnoTutor.setCicloAcademico(clonarDTO.getDestino());
                    alumnoTutor.setEstadoEnum(ACT);
                    alumnoTutor.setFechaAsigna(new Date());
                    alumnoTutor.setUserAsigna(ds.getUsuario());
                    alumnoConsejeroDAO.save(alumnoTutor);
                }
            }

        }
////////////
        List<Alumno> ingresantesCicloDestino = alumnoDAO.allIngresantePregradoByCicloIngreso(new ModalidadEstudio(1L), clonarDTO.getDestino());

        log.debug("save IngresantesCicloDestino ");
        for (Alumno alumno : ingresantesCicloDestino) {
            AlumnoConsejero alumnoConsejer = new AlumnoConsejero();
            alumnoConsejer.setAlumno(alumno);
            alumnoConsejer.setCicloAcademico(clonarDTO.getDestino());
            alumnoConsejer.setEstadoEnum(ACT);
            alumnoConsejer.setFechaAsigna(new Date());
            alumnoConsejer.setUserAsigna(ds.getUsuario());
            alumnoConsejer.setConsejero(new Consejero(GlobalConstantine.ID_CONSEJERO_NN));
            alumnoConsejeroDAO.save(alumnoConsejer);
        }

        List<MatriculaResumen> mtrblesNoRegistrados = matriculaResumenDAO.allByCicloSinConsejeria(clonarDTO.getDestino());

        log.debug("save Matriculables que no estan por su suspención del ciclo pasado ");
        for (MatriculaResumen mtble : mtrblesNoRegistrados) {
            AlumnoConsejero alumnoTutor = new AlumnoConsejero();
            alumnoTutor.setAlumno(mtble.getAlumno());
            alumnoTutor.setCicloAcademico(clonarDTO.getDestino());
            alumnoTutor.setConsejero(new Consejero(GlobalConstantine.ID_CONSEJERO_NN));
            alumnoTutor.setEstadoEnum(ACT);
            alumnoTutor.setFechaAsigna(new Date());
            alumnoTutor.setUserAsigna(ds.getUsuario());
            alumnoConsejeroDAO.save(alumnoTutor);
        }

        List<ConsejeriaResumen> resumenesGeneral = consejeriaResumenDAO.allByCiclo(clonarDTO.getDestino());

        log.debug("calculando ConsejeriaResumen ");
        for (ConsejeriaResumen consejeriaResumen : resumenesGeneral) {
            Aconsejado aconsejadoMtbles = alumnoConsejeroDAO.countAconsejadosMatriculables(consejeriaResumen.getCarrera(), clonarDTO.getDestino());
            aconsejadoMtbles = (aconsejadoMtbles == null) ? new Aconsejado() : aconsejadoMtbles;

            consejeriaResumen.setAconsejadosActivos(aconsejadoMtbles.getMatriculadosConConsejeros().intValue());
            consejeriaResumen.setAconsejadosInactivos(aconsejadoMtbles.getNoMatriculadosConConsejeros().intValue());
            consejeriaResumen.setSinconsejeroActivos(aconsejadoMtbles.getMatriculadosSinConsejeros().intValue());
            consejeriaResumen.setSinconsejeroInactivos(aconsejadoMtbles.getNoMatriculadosSinConsejeros().intValue());

            Aconsejado aconsejadoNoMtbles = alumnoConsejeroDAO.countAconsejadosNoMatriculables(consejeriaResumen.getCarrera(), clonarDTO.getDestino());
            aconsejadoNoMtbles = (aconsejadoNoMtbles == null) ? new Aconsejado() : aconsejadoNoMtbles;
            consejeriaResumen.setInhabilitados(aconsejadoNoMtbles.getInhabilitados().intValue());

            ConsejeroEstado cont = consejeroDAO.countConsejerosByCarrera(consejeriaResumen.getCarrera());
            cont = (cont == null) ? new ConsejeroEstado() : cont;
            consejeriaResumen.setConsejerosActivos(cont.getActivos().intValue());
            consejeriaResumen.setConsejerosInactivos(cont.getInactivos().intValue());

            consejeriaResumenDAO.update(consejeriaResumen);
            
        }////////////

        log.debug("save ConsejeriaHistorial ");
        ConsejeriaHistorial consejeriaHistorial = new ConsejeriaHistorial();
        consejeriaHistorial.setCicloAcademico(clonarDTO.getDestino());
        consejeriaHistorial.setEstadoEnum(ConsejeriaHistorialEstado.ACTIVO);
        consejeriaHistorial.setFechaCreacion(new Date());
        consejeriaHistorial.setFechaActualizacion(new Date());
        consejeriaHistorialDAO.save(consejeriaHistorial);

        if (verificadorClonacionConsejero == null) {
            verificadorClonacionConsejero = new VerificadorClonacionConsejero();
        }

        verificadorClonacionConsejero.setOcupado(false);

    }

    @Override
    @Transactional
    public void eliminar(Long idConsejeriaHistorial, DataSessionPivot ds) {

        ConsejeriaHistorial consejeriaHistorial = consejeriaHistorialDAO.find(idConsejeriaHistorial);
        consejeriaHistorial.setEstadoEnum(ConsejeriaHistorialEstado.ELIMINADO);
        consejeriaHistorial.setFechaActualizacion(new Date());
        consejeriaHistorialDAO.update(consejeriaHistorial);

        reunionAlumnoConsejeroDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
        tutorSolicitudDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
        consejeriaResumenDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
        alumnoConsejeroDAO.deleteByCiclo(consejeriaHistorial.getCicloAcademico());
    }

    @Override
    public List<ReunionAlumnoConsejero> allReunionAlumnoConsejeroReporte(FiltroReporteAgendaDTO filtroReporteAgendaDTO) {

        return reunionAlumnoConsejeroDAO.allReunionAlumnoConsejeroReporte(filtroReporteAgendaDTO);

    }

    @Override
    public List<AgendaConsejero> agendaDynatable(DynatableFilter filter) {

        List<AgendaConsejero> agendaConsejeros = agendaConsejeroDAO.allDynatableByCicloAcademico(filter);

        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = reunionAlumnoConsejeroDAO.allByAgendaConsejeros(agendaConsejeros);

        Map<Long, List<ReunionAlumnoConsejero>> reunionAlumnoConsejerosMap = reunionAlumnoConsejeros.stream()
                .collect(Collectors.groupingBy(x -> x.getAgendaConsejero().getId()));

        for (AgendaConsejero agendaConsejero : agendaConsejeros) {
            agendaConsejero.setReunionAlumnoConsejeros(reunionAlumnoConsejerosMap.getOrDefault(agendaConsejero.getId(), new ArrayList()));
        }

        this.verificarVencimiento(agendaConsejeros);

        return agendaConsejeros;

    }

    public void verificarVencimiento(List<AgendaConsejero> agendaConsejeros) {

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateTime today = new DateTime();

        for (AgendaConsejero agendaConsejero : agendaConsejeros) {
            if (agendaConsejero.getEstadoEnum() == AGEN) {

                try {

                    DateTime todayForm = new DateTime(agendaConsejero.getFecha());

                    todayForm = new DateTime(todayForm.toString("yyyy-MM-dd") + "T" + agendaConsejero.getHora().getDescripcion2());
                    todayForm = todayForm.plusHours(2);

                    Date dateToday = sdformat.parse(today.toString("yyyy-MM-dd HH:mm"));
                    Date dateForm = sdformat.parse(todayForm.toString("yyyy-MM-dd HH:mm"));

                    if (dateToday.compareTo(dateForm) > 0) {

                        agendaConsejero.setEstadoEnum(AgendaConsejeroEstadoEnum.VEN);
                        agendaConsejeroDAO.updateColumns(agendaConsejero, "estado");

                    }

                } catch (ParseException ex) {
                    Logger.getLogger(ReunionConsejeroServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public List<Carrera> buscarCarrera(String nombre) {
        nombre = forLike(nombre);
        return carreraDAO.allCarrerasPregradoActivaByNombre(nombre);
    }

    @Override
    public List<Consejero> buscarConsejero(String nombre) {
        nombre = forLike(nombre);
        return consejeroDAO.allByNombre(nombre);
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<Alumno> buscarAlumno(String nombre) {
        nombre = forLike(nombre);
        return alumnoDAO.allActivoPregradoByNombre(nombre);
    }

    @Override
    public List<Colaborador> coordinadores(DynatableFilter filter) {
        return colaboradorDAO.allCoordinadorByDynatable(filter);
    }

}
