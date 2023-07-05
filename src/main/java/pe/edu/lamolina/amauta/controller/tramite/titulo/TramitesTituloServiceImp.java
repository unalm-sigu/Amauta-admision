package pe.edu.lamolina.amauta.controller.tramite.titulo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTituloDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Service
@Transactional(readOnly = true)
public class TramitesTituloServiceImp implements TramitesTituloService {

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TramiteTituloDAO tramiteTituloDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Override
    public List<TramiteTitulo> allTramitesByFilter(DynatableFilter filter) {

        return tramiteTituloDAO.allByDynatable(filter);
    }

    @Override
    public Context reporte(Long idTramite, DataSessionPivot ds) {

        Tramite tramite = this.findByTramite(idTramite);

        TramiteTitulo tramiteTitulo = tramiteTituloDAO.findByTramite(tramite);
        if (tramiteTitulo == null) {
            throw new PhobosException("No se ha encontrado el tramite");
        }

        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);

        List< AlumnoCiclo> alumnosCiclos = alumnoCicloCursos.stream().map(x -> x.getAlumnoCiclo()).collect(Collectors.toList());

        int creditosConvalidados = 0;

        List<AlumnoCicloCurso> listAlumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnoOrderByTipoCurso(alumno);

        for (AlumnoCicloCurso alumnoCicloCurso : listAlumnoCicloCurso) {
            if (alumnoCicloCurso.getNota().equals("TE")) {
                creditosConvalidados = creditosConvalidados + alumnoCicloCurso.getCreditos();
            }
        }

        alumno.setCreditosConvalidadosTransient(creditosConvalidados);

        String codigo = "10000000";
        String codigoFin = "1";
        CicloAcademico cicloInicio = new CicloAcademico();
        AlumnoCiclo alumnoCiclo = null;
        for (AlumnoCiclo alumnoCic : alumnosCiclos) {
            Integer cod = Integer.parseInt(codigo);
            Integer codFin = Integer.parseInt(codigoFin);
            Integer coda = Integer.parseInt(alumnoCic.getCicloAcademico().getCodigo());
            if (coda < cod) {
                cicloInicio = alumnoCic.getCicloAcademico();
                codigo = alumnoCic.getCicloAcademico().getCodigo();
            }
            if (coda > codFin) {
                codigoFin = alumnoCic.getCicloAcademico().getCodigo();
                alumnoCiclo = alumnoCic;
            }
        }

        EventoCicloAcademico eventoActual = eventoCicloAcademicoDAO.findByCicloAndEvento(alumno.getCicloActivo(), EventoAcademicoEnum.FECHAS_BACH);
        if (eventoActual == null) {
            throw new PhobosException(String.format("No se ha configurado el evento fecha primera matricula y egreso para el ciclo %s", alumno.getCicloActivo().getDescripcion()));
        }

        EventoCicloAcademico eventoIngreso = eventoCicloAcademicoDAO.findByCicloAndEvento(cicloInicio, EventoAcademicoEnum.FECHAS_BACH);

        if (eventoIngreso == null) {
            throw new PhobosException(String.format("No se ha configurado el evento fecha primera matricula y egreso para el ciclo %s", cicloInicio.getDescripcion()));
        }

        ObtencionGrado obtencionGrado = obtencionGradoDAO.findByAlumnoAndTipo(alumno, TipoGradoAcademicoEnum.BACH);

        if (obtencionGrado == null) {
            throw new PhobosException("El trámite no tiene resolución");
        }

        Context ctx = new Context();

        ctx.setVariable("alumno", alumno);
        ctx.setVariable("ciclo", cicloAcademico);
        ctx.setVariable("alumnoCiclo", alumnoCiclo);
        ctx.setVariable("titulo", tramiteTitulo);
        ctx.setVariable("obtencionGrado", obtencionGrado);
        ctx.setVariable("fechaPrimaMatricula", TypesUtil.getStringDate(eventoIngreso.getFechaInicio(), " dd'/'MM'/'yyyy", "es"));
        ctx.setVariable("fechaEgreso", TypesUtil.getStringDate(eventoActual.getFechaFin(), " dd'/'MM'/'yyyy", "es"));
        ctx.setVariable("fechaResolucion", TypesUtil.getStringDate(obtencionGrado.getResolucion().getFecha(), " dd'/'MM'/'yyyy", "es"));

        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));

        ctx.setVariable("nombrePdf", "Informe Titulo " + tramite.getAlumno().getPersona().getPaterno() + " " + tramite.getNumero());
        ctx.setVariable("templatePdf", "detalleTitulo");

        return ctx;
    }

    @Override
    @Transactional
    public void saveTitulo(TramiteTitulo tramiteTituloForm, DataSessionPivot ds) {

        LocalDate today = new LocalDate();

        Alumno alumnoDB = alumnoDAO.find(tramiteTituloForm.getAlumno());
        if (!alumnoDB.getModalidadEstudio().isOperativePRE()) {
            throw new PhobosException("El trámite es solo para alumnos de pre grado");
        }

        TramiteBachiller tramiteBachiller = tramiteBachillerDAO.findByAlumnoACEP(alumnoDB);
        
        if (tramiteBachiller == null) {
            throw new PhobosException(String.format("El alumno %s no es bachiller", alumnoDB.getCodigo()));
        }

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_TITULO);

        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());

        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.TIT.name());

        Tramite tramite = tramiteDAO.findByAlumnoTipoTramEstado(alumnoDB, tipoTramite);

        if (tramite != null) {
            throw new PhobosException(String.format(" Ya cuenta con un tramite titulo en proceso en el ciclo %s", tramite.getCicloAcademico().getDescripcion2()));
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

        TramiteTitulo titulo = new TramiteTitulo();
        titulo.setTramite(tramite);
        titulo.setEstado(TramiteEstadoEnum.SOL.name());
        titulo.setFechaRegistro(new Date());
        titulo.setUsuario(ds.getUsuario());
        tramiteTituloDAO.save(titulo);

    }

    @Override
    public void anularTitulo(TramiteTitulo tramiteTitulo, DataSessionPivot ds) {
        tramiteTitulo = tramiteTituloDAO.find(tramiteTitulo.getId());
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);
        ObtencionGrado obtencionGrado = obtencionGradoDAO.findByAlumnoAndTipo(tramiteTitulo.getTramite().getAlumno(), TipoGradoAcademicoEnum.TIT);
        if (obtencionGrado != null) {

            obtencionGrado.setEstadoTramite(estadoTramite);
            obtencionGrado.setFechaAnula(new Date());
            obtencionGrado.setUserAnula(ds.getUsuario());
            obtencionGradoDAO.updateColumns(obtencionGrado, "estadoTramite", "fechaAnula", "userAnula");
        }

        Tramite tramite = tramiteTitulo.getTramite();
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramiteDAO.updateEstado(tramite);

        tramiteTitulo.setEstado(TramiteEstadoEnum.ANU.name());
        tramiteTitulo.setUsuarioAnulaTramite(ds.getUsuario());
        tramiteTituloDAO.updateColumns(tramiteTitulo, "estado","usuarioAnulaTramite");
    }

    public Tramite findByTramite(Long id) {
        return tramiteDAO.findById(new Tramite(id));
    }

}
