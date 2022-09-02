package pe.edu.lamolina.amauta.controller.tramite.bolsainvestigacion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
import pe.edu.lamolina.amauta.dao.bienestar.TipoSubvencionDAO;
import pe.edu.lamolina.amauta.dao.socioeconomico.FichaSocioeconomicaDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.dao.socioeconomico.FlujoFichaSocioeconomicaDAO;
import pe.edu.lamolina.amauta.dao.tramite.AlumnoBolsaInvestigacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.BolsaInvestigacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteBienestarDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteSubvencionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.BolsaInvestigacionEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.model.socioeconomico.FlujoFichaSocioeconomica;
import pe.edu.lamolina.model.tramite.FlujoTramiteBienestar;
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;
import static pe.edu.lamolina.model.enums.FichaSocioeconomicaEstadoEnum.ACP;
import static pe.edu.lamolina.model.enums.FichaSocioeconomicaEstadoEnum.LIB;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class BolsaInvestigacionServiceImp implements BolsaInvestigacionService {

    private final AlumnoBolsaInvestigacionDAO alumnoBolsaInvestigacionDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoDAO alumnoDAO;
    private final BolsaInvestigacionDAO bolsaInvestigacionDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final FichaSocioeconomicaDAO fichaSocioeconomicaDAO;
    private final FlujoFichaSocioeconomicaDAO flujoFichaSocioeconomicaDAO;
    private final FlujoTramiteBienestarDAO flujoTramiteBienestarDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final RolDAO rolDAO;
    private final TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;
    private final TipoSubvencionDAO tipoSubvencionDAO;
    private final TramiteDAO tramiteDAO;
    private final TramiteSubvencionDAO tramiteSubvencionDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioRolDAO usuarioRolDAO;

    private final OficinaService oficinaService;
    private final SerieDocumentoService serieDocumentoService;

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

        log.info("checkearAlumno {}", alumnoBolsa.getAlumno().getId());
        List<String> incumplimientos = checkearAlumno(alumnoBolsa.getAlumno(), ciclo, facultad);
        if (!incumplimientos.isEmpty()) {
            String error = "El alumno tiene los siguientes impedimento: \n" + incumplimientos.stream().collect(Collectors.joining(" "));
            Assert.isTrue(Boolean.FALSE, error);
        }

        TipoDocumentoCompania tdc = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serie = serieDocumentoService.getCorrelativo(tdc, Long.parseLong(ds.getCicloAcademico().getCodigo()), ds.getUsuario());

        Alumno alumno = new Alumno(alumnoBolsa.getAlumno().getId());
        Persona persona = new Persona(alumnoBolsa.getAlumno().getPersona().getId());
        Colaborador supervisor = checkSupervisor(alumnoBolsa.getSupervisor(), ds);
        alumno.setPersona(persona);

        alumnoBolsa.setAlumno(alumno);
        alumnoBolsa.setSupervisor(supervisor);

        FichaSocioeconomica ficha = fichaSocioeconomicaDAO.findByAlumno(alumnoBolsa.getAlumno(), ciclo);
        FichaSocioeconomicaEstadoEnum estadoFicha = FichaSocioeconomicaEstadoEnum.PEND;
        if (ficha == null) {
            ficha = new FichaSocioeconomica();
            ficha.setAlumno(alumnoBolsa.getAlumno());
            ficha.setCicloAcademico(ciclo);
            ficha.setEstadoEnum(estadoFicha);
            ficha.setFechaRegistro(today.toDate());
            fichaSocioeconomicaDAO.save(ficha);

        } else {
            Assert.isNull(ficha.getTramiteProcesando(), "La ficha socioeconómica del alumno está bloqueada por otro trámite");
            Assert.isTrue(Arrays.asList(ACP, LIB).contains(ficha.getEstadoEnum()), "La ficha socioeconómica del alumno está bloqueada por otro trámite");
            ficha.setEstadoEnum(estadoFicha);
            fichaSocioeconomicaDAO.update(ficha);
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
        tramite.setFichaSocioeconomica(ficha);

        tramite.setUserRegistro(ds.getUsuario());
        tramite.setFechaRegistro(today.toDate());
        tramiteDAO.save(tramite);

        FlujoTramiteBienestar flujo = new FlujoTramiteBienestar();
        flujo.setTramite(tramite);
        flujo.setEstadoEnum(TramiteEstadoEnum.CRE);
        flujo.setUserRegistro(ds.getUsuario());
        flujo.setFechaRegistro(today.toDate());
        flujoTramiteBienestarDAO.save(flujo);

        FlujoFichaSocioeconomica flujoFicha = new FlujoFichaSocioeconomica();
        flujoFicha.setFichaSocioeconomica(ficha);
        flujoFicha.setTramite(tramite);
        flujoFicha.setEstadoEnum(estadoFicha);
        flujoFicha.setUserRegistro(ds.getUsuario());
        flujoFicha.setFechaRegistro(today.toDate());
        flujoFichaSocioeconomicaDAO.save(flujoFicha);

        ficha.setTramiteProcesando(tramite);
        fichaSocioeconomicaDAO.update(ficha);

        TipoSubvencion tipoSubvencion = tipoSubvencionDAO.find(ID_TIPO_SUBVENCION_INVESTIGACION);

        TramiteSubvencion subvencion = new TramiteSubvencion();
        subvencion.setSupervisor(alumnoBolsa.getSupervisor());
        subvencion.setTipoSubvencion(tipoSubvencion);
        subvencion.setTramite(tramite);
        subvencion.setVoboSupervisor(true);
        subvencion.setHoras(tipoSubvencion.getHorasLaborales());
        subvencion.setFichaSocioeconomica(ficha);
        subvencion.setMotivo(alumnoBolsa.getNombreInvestigacion());
        subvencion.setUserRegistro(ds.getUsuario());
        subvencion.setFechaRegistro(today.toDate());
        tramiteSubvencionDAO.save(subvencion);

        alumnoBolsa.setBolsaInvestigacion(bolsa);
        alumnoBolsa.setEstadoEnum(AlumnoBolsaInvestigacionEstadoEnum.CRE);
        alumnoBolsa.setTramiteSubvencion(subvencion);
        alumnoBolsa.setUserRegistro(ds.getUsuario());
        alumnoBolsa.setFechaRegistro(today.toDate());
        alumnoBolsaInvestigacionDAO.save(alumnoBolsa);
    }

    private Colaborador checkSupervisor(Colaborador supervisor, DataSessionPivot ds) {
        Colaborador colaborador = colaboradorDAO.find(supervisor);
        Persona persona = colaborador.getPersona();
        Usuario user = usuarioDAO.findActivoByPersona(persona);
        Assert.isNotNull(user, "Este supervisor no tiene usuario asignado en el sistema");

        UsuarioRol userRol = usuarioRolDAO.findByUsuarioRolEnum(user, RolEnum.SUPER_SUBV);
        if (userRol != null) {
            return colaborador;
        }

        Rol rol = rolDAO.findByCode(RolEnum.SUPER_SUBV);

        userRol = new UsuarioRol();
        userRol.setUsuario(user);
        userRol.setRol(rol);
        userRol.setEstadoEnum(UserEstadoEnum.ACT);
        userRol.setFechaInicio(new Date());
        userRol.setFechaRegistro(new Date());
        userRol.setUserRegistro(ds.getUsuario());
        usuarioRolDAO.save(userRol);

        return colaborador;
    }

    @Override
    public List<AlumnoBolsaInvestigacion> allByDynatable(DynatableFilter filter, Facultad facultad, CicloAcademico cicloAcademico) {
        BolsaInvestigacion bolsa = findByFacultadCicloAcademico(facultad, cicloAcademico);
        log.info("bolsa = {}", bolsa);
        if (bolsa == null) {
            return new ArrayList();
        }

        List<Oficina> oficinasOrganizadas = oficinaService.allOficinasOrganizadas();

        List<AlumnoBolsaInvestigacion> bolsistasBD = alumnoBolsaInvestigacionDAO.allByDynatableBolsaInvestigacion(filter, bolsa);
        List<AlumnoBolsaInvestigacion> bolsistas = new ArrayList();
        for (AlumnoBolsaInvestigacion bolsistaBD : bolsistasBD) {
            AlumnoBolsaInvestigacion bolsista = bolsistaBD.clone();
            Oficina areaLabora = bolsista.getSupervisor().getOficina();
            Oficina oficinaMain = oficinaService.findOficinaMain(areaLabora, oficinasOrganizadas);
            areaLabora.setOficinaSuperior(oficinaMain);
            bolsistas.add(bolsista);
        }

        return bolsistas;
    }

    @Override
    public List<String> checkearAlumno(Alumno alumnoForm, CicloAcademico cicloAcademico, Facultad facultad) {
        List<String> mensajes = new ArrayList();

        Alumno alumno = alumnoDAO.find(alumnoForm);
        Facultad fac = alumno.getCarrera().getFacultad();
        if (!fac.getId().equals(facultad.getId())) {
            String valor = "El alumno debe pertenecer a la facultad de " + facultad.getNombre() + ".";
            mensajes.add(valor);
        }

        AlumnoCiclo ultimoCiclo = alumnoCicloDAO.findUltimoCicloRegularByAlumno(alumno, cicloAcademico);
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allCicloRegularByAlumno(alumno);
        TramiteSubvencion tramiteSub = tramiteSubvencionDAO.findByAlumnoCiclo(alumnoForm, cicloAcademico);
        MatriculaResumen matriculaResumen = matriculaResumenDAO.findMatriculadoByAlumno(cicloAcademico, alumnoForm);

        BigDecimal prom;
        if (ultimoCiclo != null) {
            prom = ultimoCiclo.getPromedioCiclo().divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
            int val = ultimoCiclo.getPromedioCiclo().compareTo(BigDecimal.valueOf(11));
            if (val < 0) {
                String valor = "El alumno cuenta con un promedio semestral menor a 11 (" + prom + ").";
                mensajes.add(valor);
            }

            prom = ultimoCiclo.getPromedioAcumulado().divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
            int val1 = ultimoCiclo.getPromedioAcumulado().compareTo(BigDecimal.valueOf(11));
            if (val1 < 0) {
                String valor = "El alumno cuenta con un promedio acumulado menor a 11 (" + prom + ").";
                mensajes.add(valor);
            }

            int creditos = ultimoCiclo.getCreditosAprobadosConvalidadosAcumulados();
            int val2 = ultimoCiclo.getCreditosAprobadosConvalidadosAcumulados();
            if (val2 < 15) {
                String valor = "El alumno cuenta créditos aprobados acumulados menor a 15 (actualmente tiene " + creditos + " créditos).";
                mensajes.add(valor);
            }
        }

        SituacionAcademica situacion = alumno.getSituacionAcademica();
        if (!Arrays.asList("N", "5").contains(situacion.getCodigo())) {
            mensajes.add("El alumno no cuenta con una situación académica normal (" + situacion.getNombre() + ").");
        }

        if (matriculaResumen == null) {
            mensajes.add("El Alumno no está matriculado.");

        } else {
            if (matriculaResumen.getCreditosMatriculados() == null || matriculaResumen.getCreditosMatriculados() < 12) {
                mensajes.add("El alumno cuenta con creditos matriculados menor a 12.");
            }
        }

        if (alumnoCiclos.size() > 12) {
            mensajes.add("El alumno superó los 12 ciclos permitidos para el beneficio ( tiene " + alumnoCiclos.size() + " ciclos).");
        }

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            int creditos = alumnoCiclo.getCreditosAprobadosCiclo();
            CicloAcademico ciclo = alumnoCiclo.getCicloAcademico();

            if (creditos < 10) {
                mensajes.add("El alumno no cumple con los 10 créditos aprobados en el " + ciclo.getDescripcion() + " (solo tiene " + creditos + " créditos).");
            }
            break;
        }

        if (tramiteSub != null && tramiteSub.getTramite().getEstadoEnum() != TramiteEstadoEnum.ANU) {
            String valor = "Ya tiene un registro de tramite de subvención.";
            mensajes.add(valor);
        }

        log.info("+++++++");
        for (String msg : mensajes) {
            log.info("- {}", msg);
        }
        log.info("+++++++");

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

        FlujoTramiteBienestar flujo = new FlujoTramiteBienestar();
        flujo.setTramite(tramite);
        flujo.setEstadoEnum(TramiteEstadoEnum.ANU);
        flujo.setUserRegistro(ds.getUsuario());
        flujo.setFechaRegistro(today.toDate());
        flujoTramiteBienestarDAO.save(flujo);

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
        List<Oficina> oficinasOrganizadas = oficinaService.allOficinasOrganizadas();

        List<Colaborador> colaboradoresBD = colaboradorDAO.allByName(nombre);
        List<Colaborador> colaboradores = new ArrayList();

        for (Colaborador colaboradorBD : colaboradoresBD) {
            Colaborador colaborador = colaboradorBD.clone();
            Oficina areaLabora = colaborador.getOficina();
            Oficina oficinaMain = oficinaService.findOficinaMain(areaLabora, oficinasOrganizadas);
            areaLabora.setOficinaSuperior(oficinaMain);
            colaboradores.add(colaborador);
        }

        return colaboradores;
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
