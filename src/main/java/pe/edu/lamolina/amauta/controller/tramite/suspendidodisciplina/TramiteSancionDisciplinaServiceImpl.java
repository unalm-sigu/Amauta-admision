package pe.edu.lamolina.amauta.controller.tramite.suspendidodisciplina;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.*;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.enums.tramite.TipoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;

@Service
@Transactional(readOnly = true)
public class TramiteSancionDisciplinaServiceImpl implements TramiteSancionDisciplinaService {

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    SancionDisciplinaDAO sancionDisciplinaDAO;

    @Autowired
    SancionDisciplinaCicloDAO sancionCicloDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Override
    public List<CicloAcademico> getCiclos(DataSessionPivot ds) {
        CicloAcademico cicloLogueado = ds.getCicloAcademico();
        int yearInit = cicloLogueado.getYear() - 4;
        int yearEnd = cicloLogueado.getYear() + 3;
        return cicloAcademicoDAO.allPregradoFuturosByRange(yearInit, yearEnd);
    }

    @Override
    public List<SancionDisciplina> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {
        CicloAcademico cicloActual = cicloAcademicoDAO.findActivoPregrado();
        return sancionDisciplinaDAO.allByCicloDynatable(cicloActual,filter);
    }

    @Override
    @Transactional
    public String saveSancionByCiclos(SancionDTO sancionForm, DataSessionPivot ds, List<CicloAcademicoDTO> idsCiclos) {
        String mensajeJson = "OK";

        Alumno alumnoDB = alumnoDAO.find(sancionForm.getAlumno().getId());

        SancionDisciplina tieneSancionActiva = sancionDisciplinaDAO.findByAlumnoAct(alumnoDB);
        if (tieneSancionActiva != null) {
            throw new PhobosException("El alumno ya tiene una sanción disciplinaria activa");
        }

        DateTime today = new DateTime();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.SUSP_DISCIPLI.name());
        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_RETIRO_CICLO);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        CicloAcademico cicloSession = ds.getCicloAcademico();

        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(alumnoDB);
        tramite.setCicloAcademico(cicloSession);
        tramite.setEstadoEnum(TramiteEstadoEnum.SOL);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumnoDB.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setOficina(oficina);
        tramite.setNumeroVisible(tramite.getDescripcion());
        tramiteDAO.save(tramite);

        SancionDisciplina sancion = new SancionDisciplina();
        sancion.setAlumno(alumnoDB);
        sancion.setTramite(tramite);
        sancion.setFechaRegistro(new Date());
        sancion.setMotivo(sancionForm.getMotivo());
        sancion.setUsuario(ds.getUsuario());
        sancion.setEstadoEnum(TramiteEstadoEnum.SOL);
        sancionDisciplinaDAO.save(sancion);

//        List<Long> idCiclos = sancionForm.getIdsCiclos();

        List<SancionDisciplinaCiclo> ciclos = idsCiclos.stream()
                .map(id -> {
                    CicloAcademico cicloEntity = cicloAcademicoDAO.find(id.getId());
                    SancionDisciplinaCiclo relacion = new SancionDisciplinaCiclo();
                    relacion.setSancionDisciplina(sancion);
                    relacion.setCiclo(cicloEntity);
                    return relacion;
                })
                .collect(Collectors.toList());

        sancionCicloDAO.saveAll(ciclos);

        return mensajeJson;
    }

    @Override
    @Transactional
    public String updateSancionByCiclos(SancionDTO sancionForm, DataSessionPivot ds, List<CicloAcademicoDTO> idsCiclos) {
        String mensajeJson = "OK";

        try {
            SancionDisciplina sancionExistente = sancionDisciplinaDAO.find(sancionForm.getId());

            List<TramiteEstadoEnum> estadosPermitidos = Arrays.asList(TramiteEstadoEnum.SOL, TramiteEstadoEnum.ACEP);
            if (!estadosPermitidos.contains(sancionExistente.getEstadoEnum())) {
                throw new PhobosException("No se puede editar una sanción que no está en estado 'Solicitado' o 'Aceptado'");
            }

            sancionExistente.setMotivo(sancionForm.getMotivo());
            sancionExistente.setFechaActualizacion(new Date());
            sancionExistente.setUsuarioActualizacion(ds.getUsuario());
            sancionDisciplinaDAO.update(sancionExistente);

            List<SancionDisciplinaCiclo> ciclosExistentes = sancionCicloDAO.findBySancionDisciplina(sancionExistente);
            if (!ciclosExistentes.isEmpty()) {
                for (SancionDisciplinaCiclo ciclo : ciclosExistentes) {
                    sancionCicloDAO.delete(ciclo);
                }
            }

            List<SancionDisciplinaCiclo> ciclos = idsCiclos.stream()
                    .map(id -> {
                        CicloAcademico cicloEntity = cicloAcademicoDAO.find(id.getId()); // 👈 obtienes la entidad real
                        SancionDisciplinaCiclo relacion = new SancionDisciplinaCiclo();
                        relacion.setSancionDisciplina(sancionExistente);
                        relacion.setCiclo(cicloEntity);
                        return relacion;
                    })
                    .collect(Collectors.toList());

            sancionCicloDAO.saveAll(ciclos);

        } catch (Exception e) {
            e.printStackTrace();
            mensajeJson = "Error al actualizar la sanción: " + e.getMessage();
        }

        return mensajeJson;
    }

    @Override
    @Transactional
    public void anular(Long idSancion, Usuario usuario) {
        SancionDisciplina sancionDisciplina = sancionDisciplinaDAO.find(idSancion);

        if (sancionDisciplina == null) {
            throw new PhobosException("El trámite no fue encontrado");
        }

        if (sancionDisciplina.getEstado().equalsIgnoreCase(ACEP.name())) {
            throw new PhobosException("El trámite ya fue aceptado");
        }

        if (sancionDisciplina.getEstado().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())) {
            throw new PhobosException("El trámite ya fue anulado");
        }

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);

        Tramite tramite = sancionDisciplina.getTramite();
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(usuario);
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setEstadoTramite(estadoTramite);
        tramiteDAO.updateEstado(tramite);

        sancionDisciplina.setEstado(TramiteEstadoEnum.ANU.name());
        sancionDisciplina.setUsuarioActualizacion(usuario);
        sancionDisciplina.setFechaActualizacion(new Date());
        sancionDisciplinaDAO.updateColumns(sancionDisciplina, "estado");

    }

    @Override
    public SancionDTO getSancionDTOById(Long sancionId) {
        try {
            SancionDisciplina sancion = sancionDisciplinaDAO.find(sancionId);
            if (sancion == null) {
                return null;
            }

            SancionDTO dto = new SancionDTO();
            dto.setId(sancion.getId());
            dto.setMotivo(sancion.getMotivo());

            if (sancion.getAlumno() != null) {
                Alumno alumno = sancion.getAlumno();
                AlumnoDTO alumnoDTO = new AlumnoDTO();
                alumnoDTO.setId(alumno.getId());
                alumnoDTO.setNombreCompleto(alumno.getPersona().getNombreCompleto());
                alumnoDTO.setNumeroDocumento(alumno.getPersona().getNumeroDocIdentidad());
                dto.setAlumno(alumnoDTO);
            }

            List<SancionDisciplinaCiclo> ciclosSancion = sancionCicloDAO.findBySancionDisciplina(sancion);
            List<CicloAcademicoDTO> ciclos = ciclosSancion.stream()
                    .map(sc -> {
                        CicloAcademicoDTO c = new CicloAcademicoDTO();
                        c.setId(sc.getCiclo().getId());
                        c.setDescripcion(sc.getCiclo().getDescripcion());
                        return c;
                    })
                    .collect(Collectors.toList());

            dto.setCicloAcademico(ciclos);

            return dto;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
