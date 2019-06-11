package pe.edu.lamolina.pivot.controller.programacionhorarios.tramiteaula;

import com.google.common.base.Strings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.AulaReservada;
import pe.edu.lamolina.model.bienestar.DiaHora;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.EstadoReservaAulaEnum;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.almacen.ResumenInventarioDAO;
import pe.edu.lamolina.pivot.dao.bienestar.AulaReservadaDAO;
import pe.edu.lamolina.pivot.dao.bienestar.ReservaAulaDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.zelper.mail.MailerService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TramiteAulaServiceImp implements TramiteAulaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    ResumenInventarioDAO resumenInventarioDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    EmpresaDAO empresaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    ReservaAulaDAO reservaAulaDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    AulaReservadaDAO aulaReservadaDAO;

    @Autowired
    MailerService mailerService;

    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Override
    public List<ReservaAula> allDynatableFilter(DynatableFilter filter) {

        List<ReservaAula> reservaAulas = reservaAulaDAO.allDynatableFilter(filter);
        List<AulaReservada> aulass = aulaReservadaDAO.allByReservaAulas(reservaAulas);
        Map<Long, List<AulaReservada>> aulassMap = TypesUtil.convertListToMapList("reservaAula.id", aulass);

        for (ReservaAula reservaAula : reservaAulas) {
            List<AulaReservada> aulaReservada = aulassMap.get(reservaAula.getId());
            reservaAula.setAulaReservada(aulaReservada);
            if (aulaReservada != null) {
                Map<Long, Aula> aulassss = aulaReservada.stream().collect(Collectors.toMap(x -> x.getAula().getId(), x -> x.getAula(), (f, s) -> s));
                List<Aula> aulasss = aulassss.values().stream().collect(Collectors.toList());
                reservaAula.setReservados(aulasss);
            }
        }

        return reservaAulas;
    }

    @Override
    public List<Aula> allByDynatableFilterAula(DynatableFilter filter, DataSessionPivot ds) {

        Map<String, Object> queries = filter.getQueries();

        Date fechainicio = null;
        Date fechafin = null;

        String diashoraslist = null;
        boolean solodisponible = false;
        boolean rangofecha = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (queries != null) {
            for (String key : queries.keySet()) {
                if (key.equals("fechainicio")) {
                    String value = (String) queries.get(key);

                    if (!Strings.isNullOrEmpty(value)) {
                        LocalDate dateTime = LocalDate.parse(value, formatter);
                        fechainicio = java.sql.Date.valueOf(dateTime);
                    }
                }

                if (key.equals("fechafin")) {
                    String value = (String) queries.get(key);
                    if (!Strings.isNullOrEmpty(value)) {
                        LocalDate dateTime = LocalDate.parse(value, formatter);
                        fechafin = java.sql.Date.valueOf(dateTime);
                    }
                }

                if (key.equals("diahora")) {
                    diashoraslist = (String) queries.get(key);
                }

                if (key.equals("solodisponible")) {

                    String solodisponiblestr = (String) queries.get(key);
                    if (!Strings.isNullOrEmpty(solodisponiblestr)) {
                        solodisponible = Boolean.parseBoolean(solodisponiblestr);
                    }
                }

                if (key.equals("rangofecha")) {

                    String rangofechastr = (String) queries.get(key);
                    if (!Strings.isNullOrEmpty(rangofechastr)) {
                        rangofecha = Boolean.parseBoolean(rangofechastr);
                    }
                }
            }
        }

        List<String> diashoras = new ArrayList();
        if (!Strings.isNullOrEmpty(diashoraslist)) {
            String[] diahoras = diashoraslist.split(",");
            for (String diahora : diahoras) {
                diashoras.add(diahora);
            }
        }

        List<HorarioAula> horarios = new ArrayList();
        logger.debug("contiene dias hora {}", !diashoras.isEmpty());
        if (!diashoras.isEmpty()) {
            if (rangofecha) {
                logger.debug("rangofecha {} {} ", fechainicio, fechafin);
                horarios = horarioAulaDAO.allRangoDiaByDiasHoras(diashoras, fechainicio, fechafin);
                logger.debug("horarios {} ", horarios.size());
            } else {
                logger.debug("solo fecha {} {} ");
                horarios = horarioAulaDAO.allSoloDiaByDiasHoras(diashoras, fechainicio);
                logger.debug("horarios {} ", horarios.size());
            }
        }

        List<Aula> aulasNoIncluidas = horarios.stream().map(z -> z.getAula()).collect(Collectors.toList());
        logger.debug("aulasNoIncluidas {}", aulasNoIncluidas.size());
        for (Aula aulasNoIncluida : aulasNoIncluidas) {
            logger.debug("aulasNoIncluidas {} {} ", aulasNoIncluida.getId(), aulasNoIncluida.getCodigo());
        }
        List<Aula> aulas = null;

        if (solodisponible) {

            aulas = aulaDAO.allByDynatableFilterTramite(filter, aulasNoIncluidas, ds.getOficinaMain());

            for (Aula aula : aulas) {
                aula.setDisponible(Boolean.TRUE);
            }

        } else {

            Map<Long, Aula> aulasMap = aulasNoIncluidas.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (f, s) -> s));
            aulas = aulaDAO.allByDynatableFilterTramite(filter, null, ds.getOficinaMain());
            for (Aula aula : aulas) {
                Aula a = aulasMap.get(aula.getId());
                aula.setDisponible(Boolean.TRUE);
                if (a != null) {
                    aula.setDisponible(Boolean.FALSE);
                }
            }

        }
        return aulas;
    }

    @Override
    @Transactional
    public Empresa saveInstitucion(Empresa institucion) {

        TipoDocIdentidad doc = tipoDocIdentidadDAO.findBySimbolo(TipoDocIdentidadEnum.RUC.name());
        institucion.setTipoDocIdentidad(doc);
        empresaDAO.save(institucion);
        return institucion;
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        return alumnoDAO.allByName(nombre);
    }

    @Override
    public List<Docente> allDocenteByName(String nombre) {
        return docenteDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void save(ReservaAula reservaAula, DataSessionPivot ds) {

        TipoDocumentoCompania tdc = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serie = serieDocumentoService.getCorrelativo(tdc, Long.parseLong(ds.getCicloAcademico().getCodigo()), ds.getUsuario());
        TipoTramite tipoTramite = new TipoTramite(TipoTramiteEnum.RSVAULA.getId());

        Tramite tramite = reservaAula.getTramite();

        if (Strings.isNullOrEmpty(tramite.getTipoSolicitante())) {
            throw new PhobosException("Especifique un solicitante");
        }

        ObjectUtil.eliminarAttrSinId(tramite, "alumno");
        ObjectUtil.eliminarAttrSinId(tramite, "docente");
        ObjectUtil.eliminarAttrSinId(tramite, "empresa");
        ObjectUtil.eliminarAttrSinId(tramite, "oficina");

        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setDocente(null);
            tramite.setEmpresa(null);
            tramite.setOficina(null);
        }

        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setEmpresa(null);
            tramite.setOficina(null);
        }

        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setDocente(null);
            tramite.setOficina(null);
        }

        if (TipoSolicitanteEnum.OFI.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setEmpresa(null);
            tramite.setAlumno(null);
            tramite.setDocente(null);
        }

        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setCompania(ds.getCompania());
        tramite.setEstado(TramiteEstadoEnum.ACT.name());
        tramite.setFechaRegistro(new Date());
        tramite.setSerie(Long.parseLong(serie.getNumeroSerie()));
        tramite.setNumero(Long.parseLong(serie.getNumeroDocumento()));
        tramite.setTipoTramite(tipoTramite);
        tramiteDAO.save(tramite);

        reservaAula.setTipoReserva("PUNT");
        reservaAula.setTipoSolicitud("1");
        reservaAula.setTramite(tramite);
        reservaAula.setEstado(EstadoReservaAulaEnum.PEND.name());
        List<Aula> aulas = reservaAula.getReservados();
        if (aulas.isEmpty()) {
            reservaAula.setEstado(EstadoReservaAulaEnum.PEND.name());
        } else {
            reservaAula.setEstado(EstadoReservaAulaEnum.RES.name());
        }
        reservaAulaDAO.save(reservaAula);

        List<DiaHora> diaHoras = reservaAula.getDiahora();

        for (Aula aula : aulas) {
            if (diaHoras != null) {
                for (DiaHora diaHora : diaHoras) {
                    AulaReservada aulaReservada = new AulaReservada();
                    aulaReservada.setAula(aula);
                    aulaReservada.setDia(diaHora.getDia());
                    aulaReservada.setHora(diaHora.getHora());
                    aulaReservada.setReservaAula(reservaAula);
                    aulaReservadaDAO.save(aulaReservada);

                    HorarioAula horarioAula = new HorarioAula();
                    horarioAula.setFechaInicio(reservaAula.getFechaInicio());
                    horarioAula.setFechaFin(reservaAula.getFechaFin());
                    if (reservaAula.getFechaFin() == null) {
                        horarioAula.setFechaFin(reservaAula.getFechaInicio());
                    }
                    horarioAula.setAula(aula);
                    horarioAula.setDia(diaHora.getDia());
                    horarioAula.setHora(diaHora.getHora());
                    horarioAula.setReservaAula(reservaAula);
                    horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                    horarioAula.setTipoEnum(TipoHorarioAulaEnum.RESERV);
                    horarioAulaDAO.save(horarioAula);

                }
            }
        }
    }

    @Override
    @Transactional
    public void update(ReservaAula reservaAulaForm, DataSessionPivot ds) {

        ReservaAula reservaAula = reservaAulaDAO.find(reservaAulaForm);
        Tramite tramite = reservaAula.getTramite();
        Tramite tramiteForm = reservaAulaForm.getTramite();

        if (Strings.isNullOrEmpty(tramiteForm.getTipoSolicitante())) {
            throw new PhobosException("Especifique un solicitante");
        }

        ObjectUtil.eliminarAttrSinId(tramiteForm, "alumno");
        ObjectUtil.eliminarAttrSinId(tramiteForm, "docente");
        ObjectUtil.eliminarAttrSinId(tramiteForm, "empresa");
        ObjectUtil.eliminarAttrSinId(tramiteForm, "oficina");

        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setDocente(null);
            tramite.setEmpresa(null);
            tramite.setOficina(null);
            tramite.setAlumno(tramiteForm.getAlumno());
        }

        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setEmpresa(null);
            tramite.setOficina(null);
            tramite.setDocente(tramiteForm.getDocente());
        }

        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setDocente(null);
            tramite.setOficina(null);
            tramite.setEmpresa(tramiteForm.getEmpresa());
        }

        if (TipoSolicitanteEnum.OFI.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setDocente(null);
            tramite.setEmpresa(null);
            tramite.setOficina(tramiteForm.getOficina());
        }

        tramite.setUserModificacion(ds.getUsuario());
        tramite.setFechaModificacion(new Date());
        tramite.setTipoSolicitante(tramiteForm.getTipoSolicitante());
        tramiteDAO.update(tramite);

        List<Aula> aulas = reservaAulaForm.getReservados();
        if (aulas.isEmpty()) {
            reservaAulaForm.setEstado(EstadoReservaAulaEnum.PEND.name());
        } else {
            reservaAulaForm.setEstado(EstadoReservaAulaEnum.RES.name());
        }

        reservaAulaForm.setTipoReserva("PUNT");
        reservaAulaForm.setTipoSolicitud("1");
        reservaAulaForm.setTramite(tramite);
        reservaAulaDAO.update(reservaAulaForm);
        aulaReservadaDAO.deleteAllByReservaAula(reservaAulaForm);
        horarioAulaDAO.deleteAllByReservaAula(reservaAulaForm);

        List<DiaHora> diaHoras = reservaAula.getDiahora();
        for (Aula aula : aulas) {
            if (diaHoras != null) {
                for (DiaHora diaHora : diaHoras) {
                    AulaReservada aulaReservada = new AulaReservada();
                    aulaReservada.setAula(aula);
                    aulaReservada.setDia(diaHora.getDia());
                    aulaReservada.setHora(diaHora.getHora());
                    aulaReservada.setReservaAula(reservaAula);
                    aulaReservadaDAO.save(aulaReservada);

                    HorarioAula horarioAula = new HorarioAula();
                    horarioAula.setFechaInicio(reservaAulaForm.getFechaInicio());
                    horarioAula.setFechaFin(reservaAulaForm.getFechaFin());
                    if (reservaAulaForm.getFechaFin() == null) {
                        horarioAula.setFechaFin(reservaAulaForm.getFechaInicio());
                    }
                    horarioAula.setAula(aula);
                    horarioAula.setDia(diaHora.getDia());
                    horarioAula.setHora(diaHora.getHora());
                    horarioAula.setReservaAula(reservaAula);
                    horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                    horarioAulaDAO.save(horarioAula);
                }
            }
        }
    }

    @Override
    @Transactional
    public void aceptartramite(ReservaAula reservaAula) {
        ReservaAula reservaAulaDb = reservaAulaDAO.find(reservaAula);
        reservaAulaDb.setComentario(reservaAula.getComentario());
        reservaAulaDb.setEstado(EstadoReservaAulaEnum.ACT.name());
        reservaAulaDAO.update(reservaAulaDb);
        this.sendNotificacionAceptar(reservaAulaDb);
    }

    @Override
    @Transactional
    public void rechazartramite(ReservaAula reservaAula) {

        ReservaAula reservaAulaDb = reservaAulaDAO.find(reservaAula);
        reservaAulaDb.setComentario(reservaAula.getComentario());
        reservaAulaDb.setEstado(EstadoReservaAulaEnum.ANU.name());
        reservaAulaDAO.update(reservaAulaDb);
        this.sendNotificacionRechazar(reservaAulaDb);
        horarioAulaDAO.deleteAllByReservaAula(reservaAulaDb);

    }

    @Override
    public ReservaAula findReservaAula(Long idReservaAula) {
        ReservaAula reservaAulaDb = reservaAulaDAO.find(new ReservaAula(idReservaAula));
        List<AulaReservada> reservados = aulaReservadaDAO.allByReservaAula(reservaAulaDb);
        reservaAulaDb.setAulaReservada(reservados);
        Map<Long, Aula> aulass = reservados.stream().collect(Collectors.toMap(x -> x.getAula().getId(), x -> x.getAula(), (f, s) -> s));
        List<Aula> aulas = aulass.values().stream().collect(Collectors.toList());
        reservaAulaDb.setReservados(aulas);
        return reservaAulaDb;
    }

    private void sendNotificacionAceptar(ReservaAula reservaAulaDb) {
        ContenidoCarta contenido = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.RESERVA_AULA_ACEPTAR);
        Tramite tramite = reservaAulaDb.getTramite();
        String email = "";
        String nombre = "";
        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Alumno alumno = tramite.getAlumno();
            email = alumno.getEmail();
            nombre = alumno.getPersona().getNombreCompleto();
        }
        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Docente docente = tramite.getDocente();
            email = docente.getPersona().getEmailCompania();
            nombre = docente.getPersona().getNombreCompleto();
        }
        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Empresa empresa = tramite.getEmpresa();
            nombre = empresa.getRazonSocial();
            email = empresa.getEmail();
        }
        mailerService.enviarNotificacionAulaReservaAceptado(nombre, email, contenido);
    }

    private void sendNotificacionRechazar(ReservaAula reservaAulaDb) {
        ContenidoCarta contenido = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.RESERVA_AULA_RECHAZAR);
        Tramite tramite = reservaAulaDb.getTramite();
        String email = "";
        String nombre = "";
        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Alumno alumno = tramite.getAlumno();
            email = alumno.getEmail();
            nombre = alumno.getPersona().getNombreCompleto();
        }
        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Docente docente = tramite.getDocente();
            email = docente.getPersona().getEmailCompania();
            nombre = docente.getPersona().getNombreCompleto();
        }
        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Empresa empresa = tramite.getEmpresa();
            nombre = empresa.getRazonSocial();
            email = empresa.getEmail();
        }
        mailerService.enviarNotificacionAulaReservaRechazado(nombre, email, contenido);
    }

    @Override
    public List<Aula> allAulaModuloByName(String nombre, DataSessionPivot ds) {
        Oficina oficinaMain = ds.getOficinaMain();
        return aulaDAO.allAulaModuloByName(nombre, 10, oficinaMain);
    }

    @Override
    public List<Dia> allDia() {
        return diaDAO.all();
    }

    @Override
    public List<Hora> allHora() {
        return horaDAO.all();
    }

    @Override
    public List<Oficina> allOficinaByName(String nombre, DataSessionPivot ds) {
        Compania compania = ds.getCompania();
        return oficinaDAO.allOficinaByName(nombre, compania);
    }

    @Override
    public List<AulaReservada> allAulaReservada(ReservaAula reservaAula) {
        return aulaReservadaDAO.allByReservaAula(reservaAula);
    }

}
