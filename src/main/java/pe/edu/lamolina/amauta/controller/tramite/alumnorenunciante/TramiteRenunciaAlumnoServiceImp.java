package pe.edu.lamolina.amauta.controller.tramite.alumnorenunciante;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteRenunciaAlumnoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.enums.tramite.TipoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteRenunciaAlumno;

@Service
@Transactional(readOnly = true)
public class TramiteRenunciaAlumnoServiceImp implements TramiteRenunciaAlumnoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DateTime today = new DateTime();

    @Autowired
    TramiteRenunciaAlumnoDAO tramiteRenunciaAlumnoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Override
    public List<TramiteRenunciaAlumno> allTramitesRenuciaByFilter(DynatableFilter filter) {
        return tramiteRenunciaAlumnoDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void saveAlumnoRenuncia(TramiteRenunciaAlumno tramiteRenunciaAlumno, DataSessionPivot ds) {

        LocalDate today = new LocalDate();

        Alumno alumnoDB = alumnoDAO.find(tramiteRenunciaAlumno.getAlumno());
        if (!alumnoDB.getModalidadEstudio().isOperativePRE()) {
            throw new PhobosException("El trámite es solo para alumnos de pre grado");
        }

        Integer creditosAprobados = Objects.nonNull(alumnoDB.getCreditosAprobados()) ? alumnoDB.getCreditosAprobados() : 0;
        Integer creditosConvalidados = Objects.nonNull(alumnoDB.getCreditosConvalidados()) ? alumnoDB.getCreditosConvalidados() : 0;
        Integer totalCreditos = creditosAprobados + creditosConvalidados;

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_RENUN_ALUMNO);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());

        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.ALUMREN.name());
        Tramite tramite = tramiteDAO.findByAlumnoTipoTramEstado(alumnoDB, tipoTramite);

        if (tramite != null) {
            throw new PhobosException(String.format(" Alumno %s ya cuenta con tramite renuncia en el ciclo %s", alumnoDB.getCodigo(), tramite.getCicloAcademico().getDescripcion2()));
        }

        tramite = new Tramite();
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setCompania(ds.getCompania());
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setTipoSolicitante(TipoSolicitanteEnum.ALU.name());
        tramite.setPersona(alumnoDB.getPersona());
        tramite.setAlumno(alumnoDB);
        tramite.setTipoTramite(tipoTramite);
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setOficina(oficina);
        tramite.setEstadoEnum(TramiteEstadoEnum.SOL);
        tramite.setFechaRegistro(new Date());
        tramite.setNumeroVisible(tramite.getDescripcion());
        tramiteDAO.save(tramite);

        TramiteRenunciaAlumno tramiteRenuncua = new TramiteRenunciaAlumno();
        tramiteRenuncua.setTramite(tramite);
        tramiteRenuncua.setEstado(TramiteEstadoEnum.SOL.name());
        tramiteRenuncua.setFechaRegistro(new Date());
        tramiteRenuncua.setUsuario(ds.getUsuario());
        tramiteRenunciaAlumnoDAO.save(tramiteRenuncua);
    }

    @Override
    public List<TipoTramite> allTipoTramite() {
        return tipoTramiteDAO.all();
    }

    @Override
    @Transactional
    public void saveAlumnoRenunciaCarrera(TramiteRenunciaAlumno tramiteRenunciaAlumno, DataSessionPivot ds) {
        LocalDate today = new LocalDate();

        Alumno alumnoDB = alumnoDAO.find(tramiteRenunciaAlumno.getAlumno());
        if (!alumnoDB.getModalidadEstudio().isOperativePRE()) {
            throw new PhobosException("El trámite es solo para alumnos de pre grado");
        }

        Integer creditosAprobados = Objects.nonNull(alumnoDB.getCreditosAprobados()) ? alumnoDB.getCreditosAprobados() : 0;
        Integer creditosConvalidados = Objects.nonNull(alumnoDB.getCreditosConvalidados()) ? alumnoDB.getCreditosConvalidados() : 0;
        Integer totalCreditos = creditosAprobados + creditosConvalidados;

//        if (totalCreditos.intValue() < 200) {
//            throw new PhobosException(String.format("Alumno %s no es egresado, cuenta con %s créditos", alumnoDB.getCodigo(), totalCreditos.intValue()));
//        }
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_RENUN_ALUMNO);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());

        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RENUNCIA_CAR.name());
        Tramite tramite = tramiteDAO.findByAlumnoTipoTramEstado(alumnoDB, tipoTramite);

        if (tramite != null) {
            throw new PhobosException(String.format(" Alumno %s ya cuenta con tramite renuncia en el ciclo %s", alumnoDB.getCodigo(), tramite.getCicloAcademico().getDescripcion2()));
        }

        tramite = new Tramite();
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setCompania(ds.getCompania());
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setTipoSolicitante(TipoSolicitanteEnum.ALU.name());
        tramite.setPersona(alumnoDB.getPersona());
        tramite.setAlumno(alumnoDB);
        tramite.setTipoTramite(tipoTramite);
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setOficina(oficina);
        tramite.setEstadoEnum(TramiteEstadoEnum.SOL);
        tramite.setFechaRegistro(new Date());
        tramite.setNumeroVisible(tramite.getDescripcion());
        tramiteDAO.save(tramite);

        TramiteRenunciaAlumno tramiteRenuncua = new TramiteRenunciaAlumno();
        tramiteRenuncua.setTramite(tramite);
        tramiteRenuncua.setEstado(TramiteEstadoEnum.SOL.name());
        tramiteRenuncua.setFechaRegistro(new Date());
        tramiteRenuncua.setUsuario(ds.getUsuario());
        tramiteRenunciaAlumnoDAO.save(tramiteRenuncua);
    }

}
