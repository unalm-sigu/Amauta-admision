package pe.edu.lamolina.amauta.controller.tramite.bolsainvestigacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bienestar.TipoSubvencion;
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.ID_TIPO_SUBVENCION_INVESTIGACION;
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.ID_TIPO_TRAMITE_SUBVENCION;
import pe.edu.lamolina.model.enums.AlumnoBolsaInvestigacionEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.FichaSocioeconomicaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.socioeconomico.FichaSocioeconomica;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.encuesta.FichaSocioeconomicaDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteBienestarDAO;
import pe.edu.lamolina.amauta.dao.tramite.AlumnoBolsaInvestigacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.BolsaInvestigacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteSubvencionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.BolsaInvestigacionEstadoEnum;

@Service
@Transactional(readOnly = true)
public class BolsaInvestigacionServiceImp implements BolsaInvestigacionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BolsaInvestigacionDAO bolsaInvestigacionDAO;

    @Autowired
    AlumnoBolsaInvestigacionDAO alumnoBolsaInvestigacionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    TramiteSubvencionDAO tramiteSubvencionDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    AccionTramiteBienestarDAO accionTramiteBienestarDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;
    @Autowired
    FichaSocioeconomicaDAO fichaSocioeconomicaDAO;

    @Override
    @Transactional
    public void agregarAlumno(Facultad facultad, CicloAcademico ciclo, AlumnoBolsaInvestigacion alumnoBolsa, DataSessionPivot ds) {
        DateTime today = new DateTime();
        BolsaInvestigacion bolsa = findByFacultadCicloAcademico(facultad, ciclo);

        Assert.isTrue(bolsa.getPostulantes().compareTo(bolsa.getBecados()) < 0, "Cantidad de postulantes excedida");
        Assert.isTrue(bolsa.getEstadoEnum() == BolsaInvestigacionEstadoEnum.ENV, "Aún no está habilitado agregar alumnos");

        bolsa.setPostulantes(bolsa.getPostulantes() + 1);
        bolsaInvestigacionDAO.update(bolsa);

        AlumnoBolsaInvestigacion alumnoBolsaBD = alumnoBolsaInvestigacionDAO.findByBolsaInvestigacionAlumno(bolsa, alumnoBolsa.getAlumno());
        Assert.isNull(alumnoBolsaBD, "Ya se ha registrado una investigación de este alumno");

        Assert.isTrue(checkearAlumno(alumnoBolsa.getAlumno(), ciclo, facultad).isEmpty(), "Alumno no válido");
        TipoDocumentoCompania tdc = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serie = serieDocumentoService.getCorrelativo(tdc, Long.parseLong(ds.getCicloAcademico().getCodigo()), ds.getUsuario());

        Alumno alumno = new Alumno(alumnoBolsa.getAlumno().getId());
        Persona persona = new Persona(alumnoBolsa.getAlumno().getPersona().getId());
        Colaborador supervisor = new Colaborador(alumnoBolsa.getSupervisor().getId());
        alumno.setPersona(persona);

        alumnoBolsa.setAlumno(alumno);
        alumnoBolsa.setSupervisor(supervisor);

        FichaSocioeconomica fichaSocioeconomica = fichaSocioeconomicaDAO.findByAlumno(alumnoBolsa.getAlumno(), ciclo);
        if (fichaSocioeconomica == null) {
            fichaSocioeconomica = new FichaSocioeconomica();
            fichaSocioeconomica.setAlumno(alumnoBolsa.getAlumno());
            fichaSocioeconomica.setCicloAcademico(ciclo);
            fichaSocioeconomica.setEstado(FichaSocioeconomicaEstadoEnum.PEND.name());
            fichaSocioeconomica.setFechaRegistro(today.toDate());
            fichaSocioeconomicaDAO.save(fichaSocioeconomica);
        } else {
            fichaSocioeconomica.setEstado(FichaSocioeconomicaEstadoEnum.PEND.name());
            fichaSocioeconomicaDAO.update(fichaSocioeconomica);
        }

        Tramite tramite = new Tramite();
        tramite.setSerie(Long.parseLong(serie.getNumeroSerie()));
        tramite.setNumero(Long.parseLong(serie.getNumeroDocumento()));
        tramite.setAlumno(alumnoBolsa.getAlumno());
        tramite.setCicloAcademico(ciclo);
        tramite.setCompania(ds.getCompania());
        tramite.setEstadoEnum(TramiteEstadoEnum.CRE);
        tramite.setPersona(alumnoBolsa.getAlumno().getPersona());
        tramite.setTipoTramite(new TipoTramite(ID_TIPO_TRAMITE_SUBVENCION));
        tramite.setFichaSocioeconomica(fichaSocioeconomica);

        tramite.setUserRegistro(ds.getUsuario());
        tramite.setFechaRegistro(today.toDate());
        tramiteDAO.save(tramite);

        TramiteSubvencion subvencion = new TramiteSubvencion();
        subvencion.setSupervisor(alumnoBolsa.getSupervisor());
        subvencion.setTipoSubvencion(new TipoSubvencion(ID_TIPO_SUBVENCION_INVESTIGACION));
        subvencion.setTramite(tramite);
        subvencion.setVoboSupervisor(true);
        subvencion.setFichaSocioeconomica(fichaSocioeconomica);
        subvencion.setComentario(alumnoBolsa.getNombreInvestigacion());
        subvencion.setUserRegistro(ds.getUsuario());
        subvencion.setFechaRegistro(today.toDate());
        tramiteSubvencionDAO.save(subvencion);

        alumnoBolsa.setBolsaInvestigacion(bolsa);
        alumnoBolsa.setEstadoEnum(AlumnoBolsaInvestigacionEstadoEnum.CRE);
        alumnoBolsa.setTramiteSubvencion(subvencion);
        alumnoBolsa.setUserRegistro(ds.getUsuario());
        alumnoBolsa.setFechaRegistro(new Date());
        alumnoBolsaInvestigacionDAO.save(alumnoBolsa);
    }

    @Override
    public List<AlumnoBolsaInvestigacion> allByDynatableFacultadCicloAcademico(DynatableFilter filter, Facultad facultad, CicloAcademico cicloAcademico) {
        BolsaInvestigacion bolsa = findByFacultadCicloAcademico(facultad, cicloAcademico);
        logger.info("bolsa = {}", bolsa);
        if (bolsa == null) {
            return new ArrayList();
        }
        return alumnoBolsaInvestigacionDAO.allByDynatableBolsaInvestigacion(filter, bolsa);
    }

    @Override
    public List<String> checkearAlumno(Alumno alumno, CicloAcademico cicloAcademico, Facultad facultad) {
        List<String> mensajes = new ArrayList();

        Alumno alum = alumnoDAO.find(alumno);
        Facultad fac = alum.getCarrera().getFacultad();
        if (!fac.getId().equals(facultad.getId())) {
            String valor = "El alumno debe pertenecer a la facultad de " + facultad.getNombre() + ".";
            mensajes.add(valor);
        }

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findUltimoCicloRegularByAlumno(alum, cicloAcademico);
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allCicloRegularByAlumno(alum);
        TramiteSubvencion tramiteSub = tramiteSubvencionDAO.findSubvencionByAlumnoCicloAcademico(alumno, cicloAcademico);
        MatriculaResumen matriculaResumen = matriculaResumenDAO.findMatriculadoByAlumno(cicloAcademico, alumno);

        if (alumnoCiclo != null) {
            int val = alumnoCiclo.getPromedioCiclo().compareTo(BigDecimal.valueOf(11));
            if (val < 0) {
                String valor = "El alumno cuenta con un promedio semestral menor a 11.";
                mensajes.add(valor);
            }
            int val1 = alumnoCiclo.getPromedioAcumulado().compareTo(BigDecimal.valueOf(11));
            if (val1 < 0) {
                String valor = "El alumno cuenta con un promedio acumulado menor a 11.";
                mensajes.add(valor);
            }
            int val2 = alumnoCiclo.getCreditosAprobadosConvalidadosAcumulados();
            if (val2 < 15) {
                String valor = "El alumno cuenta créditos aprobados acumulados menor a 15.";
                mensajes.add(valor);
            }
        }
        if (!Arrays.asList("N", "5").contains(alum.getSituacionAcademica().getCodigo())) {
            mensajes.add("El alumno no cuenta con una situación académica normal.");
        }
        if (matriculaResumen == null) {
            mensajes.add("El Alumno no está matriculado.");
        } else {
            if (matriculaResumen.getCreditosMatriculados() == null || matriculaResumen.getCreditosMatriculados() < 12) {
                mensajes.add("El alumno cuenta con creditos matriculados menor a 12.");
            }
        }
        if (alumnoCiclos.size() > 12) {
            mensajes.add("El alumno superó los 12 ciclos permitidos para el beneficio.");
        }
        for (AlumnoCiclo alumnoCiclo1 : alumnoCiclos) {
            if (alumnoCiclo1.getCreditosAprobadosCiclo() < 10) {
                mensajes.add("El alumno no cumple con los 10 creditos aprobados por ciclo.");
                return mensajes;
            }
        }

        if (tramiteSub != null && tramiteSub.getTramite().getEstadoEnum() != TramiteEstadoEnum.ANU) {
            String valor = "Ya tiene un registro de tramite de subvención.";
            mensajes.add(valor);
        }
        return mensajes;
    }

    @Override
    @Transactional
    public void eliminarAlumno(AlumnoBolsaInvestigacion alumnoBolsa, CicloAcademico cicloAcademico, Facultad facultad, DataSessionPivot ds) {
        AlumnoBolsaInvestigacion alumnoBolsaBD = alumnoBolsaInvestigacionDAO.find(alumnoBolsa.getId());
        Assert.isNotNull(alumnoBolsaBD, "No se pudo ubicar el registro de la bolsa de investigación del alumno");
        Assert.isFalse(alumnoBolsaBD.isTramiteAnulado(), "Esta registro ya se encuentra anulado");

        Facultad facultadAlumno = alumnoBolsaBD.getBolsaInvestigacion().getFacultad();
        Assert.isTrue(facultadAlumno.getId().equals(facultad.getId()), "Este alumno no pertenece a esta facultad");

        TramiteSubvencion tramiteSubvencion = alumnoBolsaBD.getTramiteSubvencion();
        Tramite tramite = tramiteSubvencion.getTramite();
        Assert.isFalse(tramite.getEstadoEnum() == TramiteEstadoEnum.ANU, "El trámite del alumnos ya se encuentra anulado");

        DateTime today = new DateTime();

        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setAceptado(false);
        tramite.setFinalizado(true);
        tramite.setUserModificacion(ds.getUsuario());
        tramite.setFechaModificacion(today.toDate());
        tramiteDAO.update(tramite);

        BolsaInvestigacion bolsa = alumnoBolsaBD.getBolsaInvestigacion();
        bolsa.setPostulantes(bolsa.getPostulantes() - 1);
        bolsaInvestigacionDAO.update(bolsa);

        alumnoBolsaBD.setEstadoEnum(AlumnoBolsaInvestigacionEstadoEnum.ANU);
        alumnoBolsaBD.setUserAnulacion(ds.getUsuario());
        alumnoBolsaBD.setFechaAnulacion(today.toDate());
        alumnoBolsaInvestigacionDAO.update(alumnoBolsaBD);
    }

    @Override
    @Transactional
    public void enviarInvitaciones(Facultad facultad, CicloAcademico ciclo, DataSessionPivot ds) {
        BolsaInvestigacion bolsa = findByFacultadCicloAcademico(facultad, ciclo);
        List<AlumnoBolsaInvestigacion> alumnosBolsas = alumnoBolsaInvestigacionDAO.allByBolsaInvestigacion(bolsa);

        int contador = 0;
        for (AlumnoBolsaInvestigacion aluBolsa : alumnosBolsas) {
            if (aluBolsa.getEstadoEnum() == AlumnoBolsaInvestigacionEstadoEnum.CRE) {
                aluBolsa.setEstadoEnum(AlumnoBolsaInvestigacionEstadoEnum.INVI);
                aluBolsa.setUserModificacion(ds.getUsuario());
                aluBolsa.setFechaModificacion(new Date());
                alumnoBolsaInvestigacionDAO.update(aluBolsa);
                contador++;
            }
        }
        Assert.isTrue(contador > 0, "No existen alumnos a quienes invitar a la bolsa de investigación");
    }

    @Override
    public AlumnoBolsaInvestigacion findAlumnoBolsaInvestigacion(Long id) {
        return alumnoBolsaInvestigacionDAO.find(id);
    }

    @Override
    public BolsaInvestigacion findByFacultadCicloAcademico(Facultad facultad, CicloAcademico cicloAcademico) {
        return bolsaInvestigacionDAO.findByFacultadCicloAcademico(facultad, cicloAcademico);
    }

    @Override
    public List<Alumno> searchAlumnosByFacultadNombre(List<Facultad> facultades, String nombre, CicloAcademico ciclo) {
        List<Alumno> alumnos = alumnoDAO.allByNombreFacultad(nombre, facultades);
        List<MatriculaResumen> resumenes = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ciclo);
        Map<Long, MatriculaResumen> mapResumen = TypesUtil.convertListToMap("alumno.id", resumenes);
        for (Alumno alumno : alumnos) {
            MatriculaResumen resumen = mapResumen.get(alumno.getId());
            if (resumen == null) {
                resumen = new MatriculaResumen();
                resumen.setEstadoEnum(EstadoMatriculaEnum.INH);
                resumen.setCreditosMatriculados(0);
            }
            alumno.setMatriculaResumen(new ArrayList());
            alumno.getMatriculaResumen().add(resumen);
        }
        return alumnos;
    }

    @Override
    public List<Colaborador> searchColaboradoresByFacultadNombre(Facultad facultad, String nombre) {
        return colaboradorDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void updateAlumno(Facultad facultad, CicloAcademico ciclo, AlumnoBolsaInvestigacion alumnoBolsa, DataSessionPivot ds) {
        AlumnoBolsaInvestigacion alumnoBolsaBD = alumnoBolsaInvestigacionDAO.find(alumnoBolsa.getId());
        Facultad facultadBD = alumnoBolsaBD.getBolsaInvestigacion().getFacultad();
        Assert.isTrue(facultadBD.getId().equals(facultad.getId()), "Esta investigación no pertenece a esta facultad");

        alumnoBolsaBD.setSupervisor(alumnoBolsa.getSupervisor());
        alumnoBolsaBD.setNombreInvestigacion(alumnoBolsa.getNombreInvestigacion());
        alumnoBolsaBD.setUserModificacion(ds.getUsuario());
        alumnoBolsaBD.setFechaModificacion(new Date());

        alumnoBolsaInvestigacionDAO.update(alumnoBolsaBD);
    }

    @Override
    public Facultad findByDataSession(DataSessionPivot ds) {
        return new Facultad(5L);
    }

}
