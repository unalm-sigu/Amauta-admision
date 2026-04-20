package pe.edu.lamolina.amauta.controller.programacionhorarios.tramiteaula;

import com.google.common.base.Strings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.tramite.AulaReservada;
import pe.edu.lamolina.model.bienestar.DiaHora;
import pe.edu.lamolina.model.tramite.ReservaAula;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.dao.almacen.ResumenInventarioDAO;
import pe.edu.lamolina.amauta.dao.bienestar.AulaReservadaDAO;
import pe.edu.lamolina.amauta.dao.bienestar.ReservaAulaDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.general.EmpresaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.general.PaisDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.mail.MailerService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.ReservaAulaEstadoEnum;
import static pe.edu.lamolina.model.enums.SexoEnum.F;
import pe.edu.lamolina.model.enums.tramite.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;

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

    @Autowired
    PaisDAO paisDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Override
    public List<ReservaAula> allDynatableFilter(DynatableFilter filter) {

        List<ReservaAula> reservaAulas = reservaAulaDAO.allDynatableFilter(filter);

        List<AulaReservada> aulaReservadas = aulaReservadaDAO.allByReservaAulas(reservaAulas);

        Map<Long, List<AulaReservada>> mapAulaReservada = aulaReservadas
                .stream()
                .collect(Collectors.groupingBy(x -> x.getReservaAula().getId()));

        for (ReservaAula reservaAula : reservaAulas) {

            reservaAula.setAulaReservada(mapAulaReservada.getOrDefault(reservaAula.getId(), new ArrayList()));

            logger.debug("ReservaAula  {} aulas {}", reservaAula.getId(), reservaAula.getAulaReservada().size());

            Map<Long, Aula> mapAula = reservaAula.getAulaReservada()
                    .stream()
                    .collect(Collectors.toMap(x -> x.getAula().getId(), x -> x.getAula(), (f, s) -> s));

            List<Aula> aulas = mapAula.values()
                    .stream()
                    .collect(Collectors.toList());

            reservaAula.setReservados(aulas);

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
    public Empresa saveInstitucion(Empresa institucion, DataSessionPivot ds) {

        TipoDocIdentidad doc = tipoDocIdentidadDAO.findBySimboloAndPais(TipoDocIdentidadEnum.RUC.name(), institucion.getPaisUbicacion());

        Assert.isTrue(doc != null, "No tiene tipo documento identificado, comunicarse con el área de sistemas.");

        institucion.setTipoDocIdentidad(doc);
        institucion.setNumeroDocIdentidad("RESERVA_AULA_" + RandomStringUtils.randomNumeric(6));
        institucion.setUserRegistro(ds.getUsuario());
        institucion.setFechaRegistro(new Date());
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
            Assert.isNotNull(tramite.getAlumno(), "Error con el alumno seleccionado.");
        }

        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setEmpresa(null);
            tramite.setOficina(null);
            Assert.isNotNull(tramite.getDocente(), "Error con el docente seleccionado.");
        }

        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setDocente(null);
            tramite.setOficina(null);
            Assert.isNotNull(tramite.getEmpresa(), "Error con la empresa seleccionada.");
        }

        if (TipoSolicitanteEnum.OFI.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setEmpresa(null);
            tramite.setAlumno(null);
            tramite.setDocente(null);
            Assert.isNotNull(tramite.getOficina(), "Error con la oficina seleccionada.");
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

        TipoReservaAmbienteEnum tipoEnum;

        switch (reservaAula.getTipoReservaAmbiente().trim()) {
            case "Pregrado":
                tipoEnum = TipoReservaAmbienteEnum.PRE;
                break;
            case "Posgrado":
                tipoEnum = TipoReservaAmbienteEnum.EPG;
                break;
            default:
                tipoEnum = TipoReservaAmbienteEnum.LIB;
                break;
        }

        reservaAula.setTipoReservaAmbiente(tipoEnum.getId());

        reservaAula.setTipoReserva("PUNT");
        reservaAula.setTipoSolicitud("1");
        reservaAula.setTramite(tramite);

        List<Aula> aulas = reservaAula.getReservados();


        if (ds.getDocente() != null) {
            reservaAula.setEstadoEnum(ReservaAulaEstadoEnum.PEND);
        } else {
            if (aulas == null || aulas.isEmpty()) {
                reservaAula.setEstadoEnum(ReservaAulaEstadoEnum.PEND);
            } else {
                reservaAula.setEstadoEnum(ReservaAulaEstadoEnum.RES);
            }
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
        reservaAula.setDiahora(reservaAulaForm.getDiahora());

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
            Assert.isNotNull(tramite.getAlumno(), "Error con el alumno seleccionado.");
        }

        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setEmpresa(null);
            tramite.setOficina(null);
            tramite.setDocente(tramiteForm.getDocente());
            Assert.isNotNull(tramite.getDocente(), "Error con el docente seleccionado.");
        }

        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setDocente(null);
            tramite.setOficina(null);
            tramite.setEmpresa(tramiteForm.getEmpresa());
            Assert.isNotNull(tramite.getEmpresa(), "Error con la empresa seleccionada.");
        }

        if (TipoSolicitanteEnum.OFI.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setDocente(null);
            tramite.setEmpresa(null);
            tramite.setOficina(tramiteForm.getOficina());
            Assert.isNotNull(tramite.getOficina(), "Error con la oficina seleccionada.");
        }

        tramite.setUserModificacion(ds.getUsuario());
        tramite.setFechaModificacion(new Date());
        tramite.setTipoSolicitante(tramiteForm.getTipoSolicitante());
        tramiteDAO.update(tramite);

        List<Aula> aulas = reservaAulaForm.getReservados();
        if (ds.getDocente() != null || aulas.isEmpty()) {
            reservaAulaForm.setEstadoEnum(ReservaAulaEstadoEnum.PEND);
        } else {
            reservaAulaForm.setEstadoEnum(ReservaAulaEstadoEnum.RES);
        }

//        if(reservaAula.getTipoReservaAmbiente().equalsIgnoreCase("Libre")){
//            reservaAula.setTipoReservaAmbiente(TipoReservaAmbienteEnum.LIB.name());
//        }else if(reservaAula.getTipoReservaAmbiente().equalsIgnoreCase("Pregrado")){
//            reservaAula.setTipoReservaAmbiente(TipoReservaAmbienteEnum.PRE.name());
//        }else {
//            reservaAula.setTipoReservaAmbiente(TipoReservaAmbienteEnum.EPG.name());
//        }
        //reservaAulaForm.setTipoReservaAmbiente();

        reservaAulaForm.setTipoReserva("PUNT");
        reservaAulaForm.setTipoSolicitud("1");
        reservaAulaForm.setTramite(tramite);
        reservaAulaDAO.update(reservaAulaForm);
        aulaReservadaDAO.deleteAllByReservaAula(reservaAulaForm);
        horarioAulaDAO.deleteAllByReservaAula(reservaAulaForm);

        List<DiaHora> diaHoras = reservaAula.getDiahora();
        for (Aula aula : aulas) {
            Assert.isTrue(diaHoras != null && !diaHoras.isEmpty(), "Error, seleccione el horario de la reserva.");
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
                    horarioAula.setTipoEnum(TipoHorarioAulaEnum.RESERV);
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
        reservaAulaDb.setEstadoEnum(ReservaAulaEstadoEnum.ACT);
        reservaAulaDAO.update(reservaAulaDb);

        List<AulaReservada> aulasReservadas = aulaReservadaDAO.allByReservaAula(reservaAula);
        Assert.isTrue(aulasReservadas != null && !aulasReservadas.isEmpty(), "Error. Verificar el horario de la reserva.");

//        this.sendNotificacionAceptar(reservaAulaDb);  para que no envie aun correo al docente revisar para darle mejor tratamiento
    }

    @Override
    @Transactional
    public void rechazarTramite(ReservaAula reservaAula) {

        ReservaAula reservaAulaDb = reservaAulaDAO.find(reservaAula);
        reservaAulaDb.setComentario(reservaAula.getComentario());
        reservaAulaDb.setEstadoEnum(ReservaAulaEstadoEnum.ANU);
        reservaAulaDAO.update(reservaAulaDb);
//        this.sendNotificacionRechazar(reservaAulaDb);  para que no envie aun correo al docente revisar para darle mejor tratamiento
        horarioAulaDAO.deleteAllByReservaAula(reservaAulaDb);

    }

    @Override
    @Transactional
    public void regularizarTramite(ReservaAula reservaAula) {

        ReservaAula reservaAulaDb = reservaAulaDAO.find(reservaAula);
        reservaAulaDb.setComentario(reservaAula.getComentario());
        reservaAulaDb.setEstadoEnum(ReservaAulaEstadoEnum.REG);
        reservaAulaDAO.update(reservaAulaDb);
//        this.sendNotificacionRechazar(reservaAulaDb);  para que no envie aun correo al docente revisar para darle mejor tratamiento
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
        String estimado = "";
        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Alumno alumno = tramite.getAlumno();
            email = alumno.getEmail();
            nombre = alumno.getPersona().getNombreCompleto();
            estimado = alumno.getPersona().getSexoEnum() == F ? "Estimada" : "Estimado";
        }
        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Docente docente = tramite.getDocente();
            email = docente.getPersona().getEmailCompania();
            nombre = docente.getPersona().getNombreCompleto();
            estimado = docente.getPersona().getSexoEnum() == F ? "Estimada" : "Estimado";
        }
        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Empresa empresa = tramite.getEmpresa();
            nombre = empresa.getRazonSocial();
            email = empresa.getEmail();
        }
        if (TipoSolicitanteEnum.OFI.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Oficina oficina = tramite.getOficina();
            nombre = oficina.getNombre();
            email = oficina.getEmail();
        }
        mailerService.enviarNotificacionAulaReservaAceptado(estimado, nombre, email, contenido);
    }

    private void sendNotificacionRechazar(ReservaAula reservaAulaDb) {
        ContenidoCarta contenido = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.RESERVA_AULA_RECHAZAR);
        Tramite tramite = reservaAulaDb.getTramite();
        String email = "";
        String nombre = "";
        String estimado = "";
        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Alumno alumno = tramite.getAlumno();
            email = alumno.getEmail();
            nombre = alumno.getPersona().getNombreCompleto();
            estimado = alumno.getPersona().getSexoEnum() == F ? "Estimada" : "Estimado";
        }
        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Docente docente = tramite.getDocente();
            email = docente.getPersona().getEmailCompania();
            nombre = docente.getPersona().getNombreCompleto();
            estimado = docente.getPersona().getSexoEnum() == F ? "Estimada" : "Estimado";
        }
        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Empresa empresa = tramite.getEmpresa();
            nombre = empresa.getRazonSocial();
            email = empresa.getEmail();
        }
        if (TipoSolicitanteEnum.OFI.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            Oficina oficina = tramite.getOficina();
            nombre = oficina.getNombre();
            email = oficina.getEmail();
        }
        mailerService.enviarNotificacionAulaReservaRechazado(estimado, nombre, email, contenido);
    }

    @Override
    public List<Aula> allAulaModuloByName(String nombre, DataSessionPivot ds) {
        Oficina oficinaMain = ds.getOficinaMain();
        return aulaDAO.allAulaModuloByName(nombre, 10, oficinaMain);
    }

    @Override
    public List<Aula> allAulaModuloByOficina(Oficina oficina) {
        return aulaDAO.allPabellonesByOficina(oficina);
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

    @Override
    public void cambiarVisibilidadReserva(ReservaAula reservaAulaForm) {
        reservaAulaDAO.updateColumns(reservaAulaForm, "visibleHorario");
    }

    @Override
    public List<ReservaAulaBean> filterByTipoReserva() {
        List<ReservaAulaBean> list = new ArrayList<>();
        list = reservaAulaDAO.allReservaTipoAmbiente();
        return list;
    }

    @Override
    public List<Aula> allAulaFiltro(String nombre) {
        return aulaDAO.allAulaModuloByName(nombre);
    }

    @Override
    public List<Empresa> allEmpresaByName(Pais pais, String nombre) {
        return empresaDAO.allEmpresaByName(pais, this.forLike(nombre));
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<ReservaAula> allByDocente(DynatableFilter filter, Docente docente) {
        List<ReservaAula> reservaAulas = reservaAulaDAO.allByDocente(filter, docente);

        List<AulaReservada> aulaReservadas = aulaReservadaDAO.allByReservaAulas(reservaAulas);

        Map<Long, List<AulaReservada>> mapAulaReservada = aulaReservadas.stream()
                .collect(Collectors.groupingBy(x -> x.getReservaAula().getId()));

        for (ReservaAula reservaAula : reservaAulas) {
            reservaAula.setAulaReservada(mapAulaReservada.getOrDefault(reservaAula.getId(), new ArrayList<>()));

            logger.debug("ReservaAula {} aulas {}", reservaAula.getId(), reservaAula.getAulaReservada().size());

            Map<Long, Aula> mapAula = reservaAula.getAulaReservada().stream()
                    .collect(Collectors.toMap(x -> x.getAula().getId(), x -> x.getAula(), (f, s) -> s));

            reservaAula.setReservados(new ArrayList<>(mapAula.values()));
        }

        return reservaAulas;
    }

    @Override
    public List<Map<String, Object>> filtrarAulasConDisponibilidad(
            DynatableFilter filter,
            String fechaInicio,
            String fechaFin,
            String horariosJson,
            Boolean soloDisponibles,
            Boolean soloOera,
            DataSessionPivot ds) {

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            Date dateInicio = null;
            Date dateFin = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (!Strings.isNullOrEmpty(fechaInicio)) {
                LocalDate dateTime = LocalDate.parse(fechaInicio, formatter);
                dateInicio = java.sql.Date.valueOf(dateTime);
            }

            if (!Strings.isNullOrEmpty(fechaFin)) {
                LocalDate dateTime = LocalDate.parse(fechaFin, formatter);
                dateFin = java.sql.Date.valueOf(dateTime);
            }

            List<String> diasHoras = new ArrayList<>();
            if (!Strings.isNullOrEmpty(horariosJson)) {
                try {

                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    List<Map<String, Object>> horarios = mapper.readValue(horariosJson, List.class);

                    for (Map<String, Object> horario : horarios) {
                        Object diaObj = horario.get("dia");
                        Object horaObj = horario.get("hora");

                        if (diaObj != null && horaObj != null) {
                            String diaId = diaObj.toString();
                            String horaId = horaObj.toString();
                            diasHoras.add(diaId + "-" + horaId);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error al parsear horarios JSON", e);
                }
            }

            List<Aula> aulasOcupadas = new ArrayList<>();
            if (!diasHoras.isEmpty() && dateInicio != null) {
                List<HorarioAula> horariosOcupados;

                if (dateFin != null && !dateFin.equals(dateInicio)) {
                    horariosOcupados = horarioAulaDAO.allRangoDiaByDiasHoras(diasHoras, dateInicio, dateFin);
                } else {
                    horariosOcupados = horarioAulaDAO.allSoloDiaByDiasHoras(diasHoras, dateInicio);
                }

                aulasOcupadas = horariosOcupados.stream()
                        .map(HorarioAula::getAula)
                        .distinct()
                        .collect(Collectors.toList());

                logger.debug("Aulas ocupadas encontradas: {}", aulasOcupadas.size());
            }

            List<Aula> aulas;

            Oficina oficina;
            if (Boolean.TRUE.equals(soloOera)) {
                oficina = oficinaDAO.findByCode(OficinaEnum.OERA.name());
            } else {
                oficina = ds.getOficinaMain();
                if (oficina == null) {
                    oficina = oficinaDAO.findByCode(OficinaEnum.OERA.name());
                    if (oficina == null) {
                        List<Oficina> oficinas = oficinaDAO.all();
                        if (!oficinas.isEmpty()) {
                            oficina = oficinas.get(0);
                        }
                    }
                }
            }

            if (soloDisponibles != null && soloDisponibles) {
                aulas = aulaDAO.allByDynatableFilterTramite(filter, aulasOcupadas, oficina);
            } else {
                aulas = aulaDAO.allByDynatableFilterTramite(filter, null, oficina);
            }

            Map<Long, Aula> aulasOcupadasMap = aulasOcupadas.stream()
                    .collect(Collectors.toMap(Aula::getId, a -> a, (f, s) -> s));

            for (Aula aula : aulas) {
                Map<String, Object> aulaMap = new HashMap<>();
                aulaMap.put("aula", aula);

                boolean isDisponible = !aulasOcupadasMap.containsKey(aula.getId());
                aulaMap.put("isDisponible", isDisponible);

                result.add(aulaMap);
            }

        } catch (Exception e) {
            logger.error("Error al filtrar aulas con disponibilidad", e);
        }

        return result;
    }

    @Override
    public List<Curso> allCursosByDocente(Docente docente, DataSessionPivot ds) {
        List<Seccion> secciones = seccionDAO.allSeccionByCicloDocente(docente, ds.getCicloAcademico());
        Map<Long, Curso> mapCursos = new LinkedHashMap<>();
        for (Seccion seccion : secciones) {
            Curso curso = seccion.getGrupoSeccion().getCurso();
            mapCursos.putIfAbsent(curso.getId(), curso);
        }
        return new ArrayList<>(mapCursos.values());
    }

    @Override
    public List<Map<String, Object>> verificarCruceAlumnos(Long cursoId, List<DiaHora> diahoras, DataSessionPivot ds) {
        Curso curso = new Curso(cursoId);

        // 1. Secciones del curso en el ciclo actual
        List<Seccion> seccionesCurso = seccionDAO.allByCicloAndCurso(ds.getCicloAcademico(), curso);
        if (seccionesCurso.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Alumnos matriculados en esas secciones
        List<MatriculaSeccion> matriculasCurso = matriculaSeccionDAO.allMatriculadosBySecciones(seccionesCurso);
        if (matriculasCurso.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Alumno> mapAlumnos = new LinkedHashMap<>();
        for (MatriculaSeccion ms : matriculasCurso) {
            Alumno alumno = ms.getMatriculaResumen().getAlumno();
            mapAlumnos.putIfAbsent(alumno.getId(), alumno);
        }

        // 3. Todos los horarios de esos alumnos en el ciclo
        List<Alumno> alumnos = new ArrayList<>(mapAlumnos.values());
        List<MatriculaSeccion> todasMatriculas = matriculaSeccionDAO.allMatriculadosByAlumnosCiclo(alumnos, ds.getCicloAcademico());

        // 4. Excluir las secciones del propio curso
        Set<Long> idsCurso = seccionesCurso.stream().map(Seccion::getId).collect(Collectors.toSet());
        List<MatriculaSeccion> otrasMatriculas = todasMatriculas.stream()
                .filter(m -> !idsCurso.contains(m.getSeccion().getId()))
                .collect(Collectors.toList());

        // 5. Horarios de las otras secciones
        Map<Long, Seccion> mapOtrasSecciones = new LinkedHashMap<>();
        for (MatriculaSeccion ms : otrasMatriculas) {
            Seccion s = ms.getSeccion();
            mapOtrasSecciones.putIfAbsent(s.getId(), s);
        }

        List<HorarioSeccion> horarios = new ArrayList<>();
        if (!mapOtrasSecciones.isEmpty()) {
            horarios = horarioSeccionDAO.allBySecciones(new ArrayList<>(mapOtrasSecciones.values()));
        }
        Map<Long, List<HorarioSeccion>> mapHorPorSeccion = new HashMap<>();
        for (HorarioSeccion hs : horarios) {
            mapHorPorSeccion.computeIfAbsent(hs.getSeccion().getId(), k -> new ArrayList<>()).add(hs);
        }

        // 6. Mapa alumno.id -> horarios de sus otras materias
        Map<Long, List<HorarioSeccion>> mapHorPorAlumno = new HashMap<>();
        for (MatriculaSeccion ms : otrasMatriculas) {
            Long alumnoId = ms.getMatriculaResumen().getAlumno().getId();
            List<HorarioSeccion> hs = mapHorPorSeccion.getOrDefault(ms.getSeccion().getId(), Collections.emptyList());
            mapHorPorAlumno.computeIfAbsent(alumnoId, k -> new ArrayList<>()).addAll(hs);
        }

        // 7. Dias-horas seleccionadas para la reserva
        Set<String> selectedDiaHoras = diahoras.stream()
                .map(dh -> dh.getDia().getId() + "_" + dh.getHora().getId())
                .collect(Collectors.toSet());

        // 8. Detectar cruces por alumno
        List<Map<String, Object>> result = new ArrayList<>();
        Set<Long> procesados = new HashSet<>();

        // Cargar alumnos con nombre completo
        Long[] ids = mapAlumnos.keySet().toArray(new Long[0]);
        List<Alumno> alumnosConPersona = alumnoDAO.allByIds(ids);
        Map<Long, Alumno> mapConPersona = new HashMap<>();
        for (Alumno a : alumnosConPersona) {
            mapConPersona.put(a.getId(), a);
        }

        for (MatriculaSeccion ms : matriculasCurso) {
            Alumno alumno = ms.getMatriculaResumen().getAlumno();
            if (procesados.contains(alumno.getId())) continue;
            procesados.add(alumno.getId());

            List<HorarioSeccion> alumnoHorarios = mapHorPorAlumno.getOrDefault(alumno.getId(), Collections.emptyList());
            List<Map<String, Object>> cruces = new ArrayList<>();

            for (HorarioSeccion hs : alumnoHorarios) {
                String key = hs.getDia().getId() + "_" + hs.getHora().getId();
                if (selectedDiaHoras.contains(key)) {
                    Map<String, Object> cruce = new HashMap<>();
                    cruce.put("dia", hs.getDia().getNombre());
                    cruce.put("hora", hs.getHora().getDescripcion());
                    cruces.add(cruce);
                }
            }

            if (!cruces.isEmpty()) {
                Alumno alumnoConNombre = mapConPersona.getOrDefault(alumno.getId(), alumno);
                Map<String, Object> entry = new HashMap<>();
                entry.put("codigo", alumnoConNombre.getCodigo());
                entry.put("nombre", alumnoConNombre.getPersona() != null ? alumnoConNombre.getPersona().getNombreCompleto() : "");
                entry.put("cruces", cruces);
                result.add(entry);
            }
        }

        return result;
    }

}
