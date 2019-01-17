package pe.edu.lamolina.pivot.controller.programacionhorarios.tramiteaula;

import com.google.common.base.Strings;
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
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoReservaAulaEnum;
import pe.edu.lamolina.model.enums.TipoAulaEnum;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
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
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
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

    @Override
    public List<ReservaAula> allDynatableFilter(DynatableFilter filter) {

        List<ReservaAula> reservaAulas = reservaAulaDAO.allDynatableFilter(filter);
        List<AulaReservada> aulass = aulaReservadaDAO.allByReservaAulas(reservaAulas);
        Map<Long, List<AulaReservada>> aulassMap = TypesUtil.convertListToMapList("reservaAula.id", aulass);

        for (ReservaAula reservaAula : reservaAulas) {
            List<AulaReservada> aulaReservada = aulassMap.get(reservaAula.getId());
            reservaAula.setAulaReservada(aulaReservada);
            if (aulaReservada != null) {
                List<Aula> aulasss = aulaReservada.stream().map(x -> x.getAula()).collect(Collectors.toList());
                reservaAula.setReservados(aulasss);
            }
        }

        return reservaAulas;
    }

    @Override
    public List<Aula> allByDynatableFilterAula(DynatableFilter filter) {
        return aulaDAO.allByDynatableFilterTramite(filter, TipoAulaEnum.MOD);
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

        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setDocente(null);
            tramite.setEmpresa(null);
        }

        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setEmpresa(null);
        }

        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
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

        for (Aula aula : aulas) {
            AulaReservada aulaReservada = new AulaReservada();
            aulaReservada.setAula(aula);
            aulaReservada.setReservaAula(reservaAula);
            aulaReservadaDAO.save(aulaReservada);
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

        if (TipoSolicitanteEnum.ALU.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setDocente(null);
            tramite.setEmpresa(null);
            tramite.setAlumno(tramiteForm.getAlumno());
        }

        if (TipoSolicitanteEnum.DOC.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setEmpresa(null);
            tramite.setDocente(tramiteForm.getDocente());
        }

        if (TipoSolicitanteEnum.EMP.name().equalsIgnoreCase(tramite.getTipoSolicitante())) {
            tramite.setAlumno(null);
            tramite.setDocente(null);
            tramite.setEmpresa(tramiteForm.getEmpresa());
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

        for (Aula aula : aulas) {
            AulaReservada aulaReservada = new AulaReservada();
            aulaReservada.setAula(aula);
            aulaReservada.setReservaAula(reservaAula);
            aulaReservadaDAO.save(aulaReservada);
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
    }

    @Override
    public ReservaAula findReservaAula(Long idReservaAula) {
        ReservaAula reservaAulaDb = reservaAulaDAO.find(new ReservaAula(idReservaAula));
        List<AulaReservada> reservados = aulaReservadaDAO.allByReservaAula(reservaAulaDb);
        reservaAulaDb.setAulaReservada(reservados);
        List<Aula> aulas = reservados.stream().map(x -> x.getAula()).collect(Collectors.toList());
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
    public List<Aula> allAulaModuloByName(String nombre) {
        return aulaDAO.allAulaModuloByName(nombre, 10, TipoAulaEnum.MOD);
    }
}
