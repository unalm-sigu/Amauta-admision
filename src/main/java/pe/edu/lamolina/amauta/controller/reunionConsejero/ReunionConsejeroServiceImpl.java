package pe.edu.lamolina.amauta.controller.reunionConsejero;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.consejeria.AgendaConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ReunionAlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.security.oauth.OAuthServiceConfig;
import pe.edu.lamolina.amauta.zelper.mail.MailerService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import pe.edu.lamolina.model.enums.AgendaConsejeroEstadoEnum;
import static pe.edu.lamolina.model.enums.AgendaConsejeroEstadoEnum.AGEN;
import pe.edu.lamolina.model.enums.ContenidoEmailEnum;
import pe.edu.lamolina.model.enums.ReunionAlumnoConsejeroEstadoEnum;
import pe.edu.lamolina.model.enums.TipoHoraEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;

@Service
@Transactional(readOnly = false)
public class ReunionConsejeroServiceImpl implements ReunionConsejeroService {

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    ConsejeroDAO consejeroDAO;

    @Autowired
    AgendaConsejeroDAO agendaConsejeroDAO;

    @Autowired
    ReunionAlumnoConsejeroDAO reunionAlumnoConsejeroDAO;

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;

    @Autowired
    MailerService mailerService;

    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;

    @Autowired
    OAuthServiceConfig config;

    @Override
    public List<Hora> allHora30() {
        return horaDAO.allByTipo(TipoHoraEnum.H30);

    }

    @Override
    @Transactional
    public void save(AgendaConsejero agendaConsejeroForm, DataSessionPivot ds) {

        this.verificarInfo(agendaConsejeroForm);

        AgendaConsejero agendaConsejero = new AgendaConsejero();
        agendaConsejero.setConsejero(agendaConsejeroForm.getConsejero());
        agendaConsejero.setEstadoEnum(AGEN);
        agendaConsejero.setFecha(agendaConsejeroForm.getFecha());
        agendaConsejero.setFechaRegistro(new Date());
        agendaConsejero.setHora(agendaConsejeroForm.getHora());
        agendaConsejero.setAsunto(agendaConsejeroForm.getAsunto());
        agendaConsejero.setCuerpo(agendaConsejeroForm.getCuerpo());
        agendaConsejero.setUserRegistro(ds.getUsuario());
        agendaConsejeroDAO.save(agendaConsejero);

        Assert.isFalse(agendaConsejeroForm.getReunionAlumnoConsejeros().isEmpty(), "Debe seleccionar como mínimo un alumno.");
        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = new ArrayList<>();
        for (ReunionAlumnoConsejero reunionAlumnoConsejeroForm : agendaConsejeroForm.getReunionAlumnoConsejeros()) {
            ReunionAlumnoConsejero reunionAlumnoConsejero = new ReunionAlumnoConsejero();
            reunionAlumnoConsejero.setAgendaConsejero(agendaConsejero);
            reunionAlumnoConsejero.setAlumnoConsejero(reunionAlumnoConsejeroForm.getAlumnoConsejero());
            reunionAlumnoConsejero.setEstadoEnum(ReunionAlumnoConsejeroEstadoEnum.AGEN);
            reunionAlumnoConsejero.setFechaRegistro(new Date());
            reunionAlumnoConsejero.setUserRegistro(ds.getUsuario());
            reunionAlumnoConsejeros.add(reunionAlumnoConsejero);
            this.enviarCorreo(reunionAlumnoConsejero, ContenidoEmailEnum.REUNIONCONSEJERO);
        }

        reunionAlumnoConsejeroDAO.saveList(reunionAlumnoConsejeros);

    }

    @Override
    @Transactional
    public void update(AgendaConsejero agendaConsejeroForm, DataSessionPivot ds) {

        AgendaConsejero agendaConsejero = agendaConsejeroDAO.find(agendaConsejeroForm.getId());
        Boolean actualizacionHora = false;
        if (agendaConsejero.getFecha().compareTo(agendaConsejeroForm.getFecha()) != 0
                || agendaConsejeroForm.getHora().getId().compareTo(agendaConsejero.getHora().getId()) != 0) {
            actualizacionHora = true;
        }

        this.verificarInfo(agendaConsejero);
        agendaConsejero.setAsunto(agendaConsejeroForm.getAsunto());
        agendaConsejero.setFecha(agendaConsejeroForm.getFecha());
        agendaConsejero.setHora(agendaConsejeroForm.getHora());
        agendaConsejero.setFechaModifica(new Date());
        agendaConsejero.setUserModifica(ds.getUsuario());
        agendaConsejeroDAO.updateColumns(agendaConsejero, "fechaModifica", "userModifica", "asunto", "hora", "fecha");

        Assert.isFalse(agendaConsejeroForm.getReunionAlumnoConsejeros().isEmpty(), "Debe seleccionar como mínimo un alumno.");

        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = reunionAlumnoConsejeroDAO.allByAgendaConsejero(agendaConsejeroForm);

        Map<Long, ReunionAlumnoConsejero> mapForm = TypesUtil.convertListToMap("alumnoConsejero.id", agendaConsejeroForm.getReunionAlumnoConsejeros());
        Map<Long, ReunionAlumnoConsejero> mapBD = TypesUtil.convertListToMap("alumnoConsejero.id", reunionAlumnoConsejeros);

        for (ReunionAlumnoConsejero reunionAlumnoConsejero : reunionAlumnoConsejeros) {
            if (mapForm.get(reunionAlumnoConsejero.getAlumnoConsejero().getId()) == null) {
                reunionAlumnoConsejero.setEstadoEnum(ReunionAlumnoConsejeroEstadoEnum.ANU);
                reunionAlumnoConsejero.setFechaModifica(new Date());
                reunionAlumnoConsejero.setUserModifica(ds.getUsuario());
                reunionAlumnoConsejeroDAO.updateColumns(reunionAlumnoConsejero, "estado", "fechaModifica", "userModifica");
                this.enviarCorreo(reunionAlumnoConsejero, ContenidoEmailEnum.DELETEREUNIONCONSEJERO);
            }
        }
        List<ReunionAlumnoConsejero> reunionAlumnoConsejerosSave = new ArrayList<>();
        for (ReunionAlumnoConsejero reunionAlumnoConsejeroForm : agendaConsejeroForm.getReunionAlumnoConsejeros()) {
            if (mapBD.get(reunionAlumnoConsejeroForm.getAlumnoConsejero().getId()) == null) {
                ReunionAlumnoConsejero reunionAlumnoConsejero = new ReunionAlumnoConsejero();
                reunionAlumnoConsejero.setAgendaConsejero(agendaConsejero);
                reunionAlumnoConsejero.setAlumnoConsejero(reunionAlumnoConsejeroForm.getAlumnoConsejero());
                reunionAlumnoConsejero.setComentario(reunionAlumnoConsejeroForm.getComentario());
                reunionAlumnoConsejero.setEstadoEnum(ReunionAlumnoConsejeroEstadoEnum.AGEN);
                reunionAlumnoConsejero.setFechaRegistro(new Date());
                reunionAlumnoConsejero.setUserRegistro(ds.getUsuario());
                reunionAlumnoConsejerosSave.add(reunionAlumnoConsejero);
                this.enviarCorreo(reunionAlumnoConsejero, ContenidoEmailEnum.REUNIONCONSEJERO);
            } else {
                ReunionAlumnoConsejero reunionAlumnoConsejeroDb = mapBD.get(reunionAlumnoConsejeroForm.getAlumnoConsejero().getId());
                if (actualizacionHora) {
                    this.enviarCorreo(reunionAlumnoConsejeroDb, ContenidoEmailEnum.UPDATEREUNIONCONSEJERO);
                }
            }
        }

        reunionAlumnoConsejeroDAO.saveList(reunionAlumnoConsejerosSave);

    }

    @Override
    @Transactional
    public void anularAgenda(AgendaConsejero agendaConsejeroForm, DataSessionPivot ds) {
        agendaConsejeroForm.setFechaModifica(new Date());
        agendaConsejeroForm.setUserModifica(ds.getUsuario());
        agendaConsejeroForm.setEstadoEnum(AgendaConsejeroEstadoEnum.ANU);
        agendaConsejeroDAO.updateColumns(agendaConsejeroForm, "fechaModifica", "userModifica", "estado");

        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = reunionAlumnoConsejeroDAO.allByAgendaConsejero(agendaConsejeroForm);

        for (ReunionAlumnoConsejero reunionAlumnoConsejero : reunionAlumnoConsejeros) {
            reunionAlumnoConsejero.setEstadoEnum(ReunionAlumnoConsejeroEstadoEnum.ANU);
            reunionAlumnoConsejero.setFechaModifica(new Date());
            reunionAlumnoConsejero.setUserModifica(ds.getUsuario());
            reunionAlumnoConsejeroDAO.updateColumns(reunionAlumnoConsejero, "estado", "fechaModifica", "userModifica");
            this.enviarCorreo(reunionAlumnoConsejero, ContenidoEmailEnum.DELETEREUNIONCONSEJERO);
        }

    }

    private void verificarInfo(AgendaConsejero agendaConsejero) {
        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            DateTime today = new DateTime();
            DateTime todayForm = new DateTime(agendaConsejero.getFecha());

            todayForm = new DateTime(todayForm.toString("yyyy-MM-dd") + "T" + agendaConsejero.getHora().getDescripcion2());

            Date dateToday = sdformat.parse(today.toString("yyyy-MM-dd HH:mm"));
            Date dateForm = sdformat.parse(todayForm.toString("yyyy-MM-dd HH:mm"));

            Assert.isTrue(dateToday.compareTo(dateForm) < 0,
                    "Fecha inferior a la fecha actual");

            List<AgendaConsejero> agendaConsejerosDb = agendaConsejeroDAO.allByConsejero(agendaConsejero.getConsejero());
            for (AgendaConsejero agendaCon : agendaConsejerosDb) {
                Assert.isFalse(agendaCon.getKey().equals(agendaConsejero.getKey()) && agendaCon.getId() != agendaConsejero.getId(), "Existe un cruce con la reunión con el asunto " + agendaCon.getAsunto());
            }
        } catch (ParseException ex) {
            Logger.getLogger(ReunionConsejeroServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Consejero findConsejeroCarrera(Long carreraId, Persona prsn) {
        return consejeroDAO.findByPersonaCarrera(prsn, new Carrera(carreraId));
    }

    @Override
    @Transactional
    public List<ReunionAlumnoConsejero> listDynatable(DynatableFilter filter, Consejero consejero, DataSessionPivot ds) {
        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = reunionAlumnoConsejeroDAO.allDynatableByConsejero(filter, consejero, ds.getCicloAcademico());

        return reunionAlumnoConsejeros;
    }

    @Override
    @Transactional
    public void verificarVencimiento(List<AgendaConsejero> agendasConsejero) {

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateTime today = new DateTime();

        for (AgendaConsejero agendaConsejero : agendasConsejero) {
            if (agendaConsejero.getEstadoEnum() == AGEN) {

                try {
                    DateTime todayForm = new DateTime(agendaConsejero.getFecha());

                    todayForm = new DateTime(todayForm.toString("yyyy-MM-dd") + "T" + agendaConsejero.getHora().getDescripcion2());
                    todayForm = todayForm.plusHours(2); // Se agregan 2 horas de tolerancia.
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
    public List<Consejero> allConsejeros(Persona persona) {

        return consejeroDAO.allByPersona(persona);
    }

    @Override
    @Transactional
    public void asistenciaReunion(ReunionAlumnoConsejero reunionAlumnoConsejeroForm, DataSessionPivot ds) {
        ReunionAlumnoConsejero reunionAlumnoConsejeroBD = reunionAlumnoConsejeroDAO.find(reunionAlumnoConsejeroForm.getId());
        reunionAlumnoConsejeroBD.setComentario(reunionAlumnoConsejeroForm.getComentario());
        reunionAlumnoConsejeroBD.setEsProblemaEconomico(reunionAlumnoConsejeroForm.getEsProblemaEconomico());
        reunionAlumnoConsejeroBD.setEsProblemaFamiliar(reunionAlumnoConsejeroForm.getEsProblemaFamiliar());
        reunionAlumnoConsejeroBD.setEsProblemaSicologico(reunionAlumnoConsejeroForm.getEsProblemaSicologico());
        reunionAlumnoConsejeroBD.setEsRiesgoAcademico(reunionAlumnoConsejeroForm.getEsRiesgoAcademico());
        reunionAlumnoConsejeroBD.setEstadoEnum(ReunionAlumnoConsejeroEstadoEnum.ASIS);
        reunionAlumnoConsejeroBD.setFechaModifica(new Date());
        reunionAlumnoConsejeroBD.setUserModifica(ds.getUsuario());
        reunionAlumnoConsejeroDAO.updateColumns(reunionAlumnoConsejeroBD, "estado", "fechaModifica", "userModifica", "comentario", "esRiesgoAcademico", "esProblemaSicologico", "esProblemaEconomico", "esProblemaFamiliar");

        AgendaConsejero agendaConsejero = reunionAlumnoConsejeroBD.getAgendaConsejero();
        agendaConsejero.setEstadoEnum(AgendaConsejeroEstadoEnum.ATEN);
        agendaConsejero.setFechaModifica(new Date());
        agendaConsejero.setUserModifica(ds.getUsuario());
        agendaConsejeroDAO.updateColumns(agendaConsejero, "estado", "fechaModifica", "userModifica");
    }

    @Override
    @Transactional
    public void inasistenciaReunion(ReunionAlumnoConsejero reunionAlumnoConsejeroForm, DataSessionPivot ds) {
        reunionAlumnoConsejeroForm.setEstadoEnum(ReunionAlumnoConsejeroEstadoEnum.NASIS);
        reunionAlumnoConsejeroForm.setFechaModifica(new Date());
        reunionAlumnoConsejeroForm.setUserModifica(ds.getUsuario());
        reunionAlumnoConsejeroDAO.updateColumns(reunionAlumnoConsejeroForm, "estado", "fechaModifica", "userModifica", "comentario");
    }

    @Override
    public void anularReunion(ReunionAlumnoConsejero reunionAlumnoConsejeroForm, DataSessionPivot ds) {
        ReunionAlumnoConsejero reunionAlumnoConsejeroDB = reunionAlumnoConsejeroDAO.find(reunionAlumnoConsejeroForm.getId());
        reunionAlumnoConsejeroDB.setEstadoEnum(ReunionAlumnoConsejeroEstadoEnum.ANU);
        reunionAlumnoConsejeroDB.setFechaModifica(new Date());
        reunionAlumnoConsejeroDB.setUserModifica(ds.getUsuario());
        reunionAlumnoConsejeroDAO.updateColumns(reunionAlumnoConsejeroDB, "estado", "fechaModifica", "userModifica", "comentario");
        enviarCorreo(reunionAlumnoConsejeroDB, ContenidoEmailEnum.DELETEREUNIONCONSEJERO);
    }

    @Override
    public List<AlumnoConsejero> list(Consejero consejero, DataSessionPivot ds) {
        return alumnoConsejeroDAO.allActivosByConsejeroCarreraCiclo(consejero, consejero.getCarrera(), ds.getCicloAcademico());
    }

    @Override
    public AgendaConsejero findAgenda(Long agendaId, CicloAcademico cicloAcademico) {
        AgendaConsejero agendaConsejero = agendaConsejeroDAO.find(agendaId);
        List<ReunionAlumnoConsejero> reunionAlumnoConsejeros = reunionAlumnoConsejeroDAO.allByAgendaConsejero(agendaConsejero);
        Map<Long, ReunionAlumnoConsejero> map = TypesUtil.convertListToMap("alumnoConsejero.id", reunionAlumnoConsejeros);

        List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.allActivosByConsejeroCarreraCiclo(agendaConsejero.getConsejero(), agendaConsejero.getConsejero().getCarrera(), cicloAcademico);

        for (AlumnoConsejero alumnoConsejero : alumnoConsejeros) {
            if (map.get(alumnoConsejero.getId()) != null) {
                alumnoConsejero.setSeleccionado(Boolean.TRUE);
            }
        }

        agendaConsejero.setAlumnoConsejeros(alumnoConsejeros);

        return agendaConsejero;
    }

    private void enviarCorreo(ReunionAlumnoConsejero reunionAlumnoConsejero, ContenidoEmailEnum contenidoEmailEnum) {

        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(contenidoEmailEnum.name());
        AgendaConsejero agendaConsejero = agendaConsejeroDAO.find(reunionAlumnoConsejero.getAgendaConsejero().getId());
        Consejero consejero = consejeroDAO.find(agendaConsejero.getConsejero().getId());
        mailerService.enviarNotificacionReunionConsejero(reunionAlumnoConsejero, consejero, contenidoCarta);

    }
}
