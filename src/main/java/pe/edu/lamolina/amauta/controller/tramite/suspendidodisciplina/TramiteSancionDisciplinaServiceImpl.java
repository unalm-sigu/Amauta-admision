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
        return cicloAcademicoDAO.allUltimosByModalidadEnum(ModalidadEstudioEnum.PRE, 20);
    }

    @Override
    public List<SancionDisciplina> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {
        CicloAcademico cicloActual = cicloAcademicoDAO.findConfiguradoPregrado();
        return sancionDisciplinaDAO.allByCicloDynatable(cicloActual,filter);
    }

    @Override
    @Transactional
    public String saveSancionByCiclos(SancionDTO sancionForm, DataSessionPivot ds, List<CicloAcademico> idsCiclos) {
        String mensajeJson = "OK";

        Alumno alumnoDB = alumnoDAO.find(sancionForm.getAlumno());

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
        tramite.setAlumno(sancionForm.getAlumno());
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

        List<SancionDisciplinaCiclo> ciclos = idsCiclos.stream()
                .map(ciclo -> {
                    SancionDisciplinaCiclo relacion = new SancionDisciplinaCiclo();
                    relacion.setSancionDisciplina(sancion);
                    relacion.setCiclo(ciclo);
                    return relacion;
                })
                .collect(Collectors.toList());
        sancionCicloDAO.saveAll(ciclos);

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
}
