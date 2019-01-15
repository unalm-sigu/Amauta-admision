package pe.edu.lamolina.pivot.controller.programacionhorarios.tramiteaula;

import com.google.common.base.Strings;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.bienestar.AulaReservada;
import pe.edu.lamolina.model.bienestar.ReservaAula;
import pe.edu.lamolina.model.enums.EstadoReservaAulaEnum;
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
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.almacen.ResumenInventarioDAO;
import pe.edu.lamolina.pivot.dao.bienestar.AulaReservadaDAO;
import pe.edu.lamolina.pivot.dao.bienestar.ReservaAulaDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
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

    @Override
    public List<ReservaAula> allDynatableFilter(DynatableFilter filter) {
        return reservaAulaDAO.allDynatableFilter(filter);
    }

    @Override
    public List<Aula> allByDynatableFilterAula(DynatableFilter filter) {
        return aulaDAO.allByDynatableFilterTramite(filter);
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

        ObjectUtil.eliminarAttrSinId(tramite, "alumno");
        ObjectUtil.eliminarAttrSinId(tramite, "docente");
        ObjectUtil.eliminarAttrSinId(tramite, "empresa");

        if (tramite.getAlumno() != null) {
            tramite.setTipoSolicitante(TipoSolicitanteEnum.ALU.name());
        }
        if (tramite.getDocente() != null) {
            tramite.setTipoSolicitante(TipoSolicitanteEnum.DOC.name());
        }
        if (tramite.getEmpresa() != null) {
            tramite.setTipoSolicitante(TipoSolicitanteEnum.EMP.name());
        }

        if (Strings.isNullOrEmpty(tramite.getTipoSolicitante())) {
            throw new PhobosException("Especifique un solicitante");
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
        //ojo tipo reserva tipo solicitud
        reservaAula.setTipoReserva("1");
        reservaAula.setTipoSolicitud("1");
        reservaAula.setTramite(tramite);
        reservaAula.setEstado(EstadoReservaAulaEnum.PEND.name());
        reservaAulaDAO.save(reservaAula);

        List<Aula> aulas = reservaAula.getReservados();
        for (Aula aula : aulas) {
            AulaReservada aulaReservada = new AulaReservada();
            aulaReservada.setAula(aula);
            aulaReservada.setReservaAula(reservaAula);
            aulaReservadaDAO.save(aulaReservada);
        }
    }

    @Override
    public void aceptartramite(ReservaAula reservaAula) {
        ReservaAula reservaAulaDb= reservaAulaDAO.find(reservaAula.getId());
        reservaAulaDb.setEstado(EstadoReservaAulaEnum.ACT.name());
        reservaAulaDAO.update(reservaAulaDb);
    }

    @Override
    public void rechazartramite(ReservaAula reservaAula) {
        ReservaAula reservaAulaDb= reservaAulaDAO.find(reservaAula.getId());
        reservaAulaDb.setEstado(EstadoReservaAulaEnum.ANU.name());
        reservaAulaDAO.update(reservaAulaDb);
    }
}
