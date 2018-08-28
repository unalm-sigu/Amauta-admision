package pe.edu.lamolina.pivot.controller.tramite.bolsainvestigacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bienestar.TipoSubvencion;
import pe.edu.lamolina.model.enums.AlumnoBolsaInvestigacionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteBienestarDAO;
import pe.edu.lamolina.pivot.dao.tramite.AlumnoBolsaInvestigacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.BolsaInvestigacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteSubvencionDAO;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.ID_TIPO_SUBVENCION_INVESTIGACION;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.ID_TIPO_TRAMITE_SUBVENCION;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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

    @Override
    @Transactional
    public void agregarAlumno(Facultad facultad, CicloAcademico cicloAcademico, AlumnoBolsaInvestigacion alumno, DataSessionPivot ds) {
        BolsaInvestigacion bi = findByFacultadCicloAcademico(facultad, cicloAcademico);

        Assert.isTrue(bi.getPostulantes().compareTo(bi.getBecados()) < 0, "Cantidad de postulantes excedida");

        bi.setPostulantes(bi.getPostulantes() + 1);
        bolsaInvestigacionDAO.update(bi);

        AlumnoBolsaInvestigacion abiBD = alumnoBolsaInvestigacionDAO.findByBolsaInvestigacionAlumno(bi, alumno.getAlumno());
        Assert.isNull(abiBD, "Ya se ha registrado una investigación de este alumno");

        Assert.isTrue(checkearAlumno(alumno.getAlumno(), cicloAcademico).isEmpty(), "Alumno no válido");
        TipoDocumentoCompania tdc = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serie = serieDocumentoService.getCorrelativo(tdc, Long.parseLong(ds.getCicloAcademico().getCodigo()), ds.getUsuario());

        Tramite tramite = new Tramite();
        tramite.setSerie(Long.parseLong(serie.getNumeroSerie()));
        tramite.setNumero(Long.parseLong(serie.getNumeroDocumento()));
        tramite.setAlumno(alumno.getAlumno());
        tramite.setCicloAcademico(cicloAcademico);
        tramite.setCompania(ds.getCompania());
        tramite.setEstado(TramiteEstadoEnum.CRE.name());
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumno.getAlumno().getPersona());
        tramite.setTipoTramite(new TipoTramite(ID_TIPO_TRAMITE_SUBVENCION));
        tramite.setUserRegistro(ds.getUsuario());
        tramiteDAO.save(tramite);

        TramiteSubvencion subvencion = new TramiteSubvencion();
        subvencion.setFechaRegistro(new Date());
        subvencion.setSupervisor(alumno.getSupervisor());
        subvencion.setTipoSubvencion(new TipoSubvencion(ID_TIPO_SUBVENCION_INVESTIGACION));
        subvencion.setTramite(tramite);
        subvencion.setUserRegistro(ds.getUsuario());
        subvencion.setVoboSupervisor(1);
        tramiteSubvencionDAO.save(subvencion);

        alumno.setBolsaInvestigacion(bi);
        alumno.setEstado(AlumnoBolsaInvestigacionEstadoEnum.CRE);
        alumno.setUserRegistro(ds.getUsuario());
        alumno.setFechaRegistro(new Date());
        alumno.setTramiteSubvencion(subvencion);
        alumnoBolsaInvestigacionDAO.save(alumno);
    }

    @Override
    public List<AlumnoBolsaInvestigacion> allByDynatableFacultadCicloAcademico(DynatableFilter filter, Facultad facultad, CicloAcademico cicloAcademico) {
        BolsaInvestigacion bi = findByFacultadCicloAcademico(facultad, cicloAcademico);
        return alumnoBolsaInvestigacionDAO.allByDynatableBolsaInvestigacion(filter, bi);
    }

    @Override
    public List<String> checkearAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        List<String> mensajes = new ArrayList();

        Alumno alum = alumnoDAO.findSituacionAcademica(alumno);
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
            int val2 = alumnoCiclo.getCreditosAprobadosAcumulados();
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

        if (tramiteSub != null) {
            String valor = "Ya tiene un registro de tramite de subvención.";
            mensajes.add(valor);
        }
        return mensajes;
    }

    @Override
    @Transactional
    public void eliminarAlumno(Long id, CicloAcademico cicloAcademico, Facultad facultad) {
        AlumnoBolsaInvestigacion abiBD = alumnoBolsaInvestigacionDAO.find(id);

        BolsaInvestigacion bi = abiBD.getBolsaInvestigacion();
        bi.setPostulantes(bi.getPostulantes() - 1);
        bolsaInvestigacionDAO.update(bi);

        Assert.isTrue(abiBD.getBolsaInvestigacion().getFacultad().getId().equals(facultad.getId()), "Esta investigación no pertenece a esta facultad");

        alumnoBolsaInvestigacionDAO.delete(abiBD);
    }

    @Override
    @Transactional
    public void enviarInvitaciones(Facultad facultad, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        BolsaInvestigacion bi = findByFacultadCicloAcademico(facultad, cicloAcademico);

        List<AlumnoBolsaInvestigacion> abis = alumnoBolsaInvestigacionDAO.allByBolsaInvestigacion(bi);
        for (AlumnoBolsaInvestigacion abi : abis) {
            //Enviar investigacion
            abi.setEstado(AlumnoBolsaInvestigacionEstadoEnum.INVI);
            alumnoBolsaInvestigacionDAO.update(abi);
        }
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
    public List<Alumno> searchAlumnosByFacultadNombre(Facultad facultad, String nombre) {
        return alumnoDAO.allByNombreFacultad(nombre, facultad);
    }

    @Override
    public List<Colaborador> searchColaboradoresByFacultadNombre(Facultad facultad, String nombre) {
        return colaboradorDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void updateAlumno(Facultad facultad, CicloAcademico cicloAcademico, AlumnoBolsaInvestigacion alumno, DataSessionPivot ds) {
        AlumnoBolsaInvestigacion abiBD = alumnoBolsaInvestigacionDAO.find(alumno.getId());
        Assert.isTrue(abiBD.getBolsaInvestigacion().getFacultad().getId().equals(facultad.getId()), "Esta investigación no pertenece a esta facultad");

        abiBD.setSupervisor(alumno.getSupervisor());
        abiBD.setNombreInvestigacion(alumno.getNombreInvestigacion());

        alumnoBolsaInvestigacionDAO.update(abiBD);
    }

    @Override
    public Facultad findByDataSession(DataSessionPivot ds) {
        return new Facultad(5L);
    }

}
