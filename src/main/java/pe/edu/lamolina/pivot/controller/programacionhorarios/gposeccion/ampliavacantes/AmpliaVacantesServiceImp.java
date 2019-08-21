package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.ampliavacantes;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AmpliacionVacanteEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo.ResponseRestService;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.academico.AmpliacionVacantesDAO;

@Service
@Transactional(readOnly = true)
public class AmpliaVacantesServiceImp implements AmpliaVacantesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AmpliacionVacantesDAO ampliacionVacanteDAO;

    @Autowired
    OficinaService oficinaService;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Override
    public List<AmpliacionVacantes> allAmpliacionVacante(Seccion seccion) {

        return ampliacionVacanteDAO.allBySeccion(seccion);
    }

    @Override
    @Transactional
    public void saveAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds) {

        Seccion seccion = seccionDAO.find(ampliacionVacante.getSeccion());
        List<AmpliacionVacantes> ampliaciones = ampliacionVacanteDAO.allPendientesBySeccion(seccion);
        Assert.isTrue(ampliaciones.isEmpty(), "Aún existe solicitudes de ampliación pendientes de atención");

        Seccion seccionSuperior = seccion.getSeccionSuperior();
        if (seccionSuperior != null) {
            List<Seccion> secciones = seccionDAO.allByGposSeccion(seccion.getGrupoSeccion());
            for (Seccion seccBD : secciones) {
                if (seccion.getId() == seccBD.getId().longValue()) {
                    continue;
                }
                List<AmpliacionVacantes> ampliacionesOtras = ampliacionVacanteDAO.allPendientesBySeccion(seccBD);
                Assert.isTrue(ampliacionesOtras.isEmpty(), "Aún existe solicitudes de ampliación pendientes para la sección " + seccBD.getCodigo2());
            }
        }

        Persona persona = ds.getPersona();
        Oficina oficinaMain = ampliacionVacante.getOficina();
        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());

        Assert.isTrue(seccion.getVacantesOcupadas() <= ampliacionVacante.getVacantesFin().intValue(), "No puede disminuir las vacantes menor a la cantidad de matriculados + reservados");
        Aula aula = seccion.getAula();
        if (aula != null && aula.getCapacidadAula() != null) {
            Assert.isTrue(aula.getCapacidadAula().intValue() >= ampliacionVacante.getVacantesFin().intValue(), "No puede exceder la capacidad del aula");
        }

        if (seccionSuperior != null) {
            aula = seccionSuperior.getAula();
            if (aula != null && aula.getCapacidadAula() != null) {
                int total = seccionSuperior.getVacantesOcupadas() + ampliacionVacante.getIncremento();
                Assert.isTrue(aula.getCapacidadAula().intValue() >= total, "No puede exceder la capacidad del aula de la sección teórica");
            }
        }

        ampliacionVacante.setColaborador(colaborador);
        ampliacionVacante.setFechaRegistro(new Date());
        ampliacionVacante.setUserRegistro(ds.getUsuario());
        ampliacionVacante.setFechaSolicitud(new Date());
        ampliacionVacante.setUserRegistro(ds.getUsuario());
        ampliacionVacante.setEstadoEnum(AmpliacionVacanteEstadoEnum.PENDIENTE);
        ampliacionVacanteDAO.save(ampliacionVacante);
    }

    @Override
    @Transactional
    public void deleteAmpliacionVacante(AmpliacionVacantes ampliacionForm, DataSessionPivot ds) {
        AmpliacionVacantes ampliacionBD = ampliacionVacanteDAO.find(ampliacionForm);
        Assert.isTrue(ampliacionBD.getEstadoEnum() == AmpliacionVacanteEstadoEnum.PENDIENTE, "No puede eliminarse ni anularse esta solicitud");

        Persona personaAnulador = ds.getPersona();
        Persona personaSolicitud = ampliacionBD.getColaborador().getPersona();
        if (personaAnulador.getId() != personaSolicitud.getId().longValue()) {
            Oficina oficinaMain = ampliacionBD.getOficina();
            boolean esMismaOficina = false;
            List<Oficina> oficinasPersona = oficinaService.allOficinasMainByPersona(personaAnulador);
            for (Oficina oficina : oficinasPersona) {
                if (oficina.getId() == oficinaMain.getId().longValue()) {
                    esMismaOficina = true;
                    break;
                }
            }
            Assert.isTrue(esMismaOficina, "Solo una persona de la misma oficina del solicitante puede anular esta solicitud");
        }

        ampliacionBD.setEstadoEnum(AmpliacionVacanteEstadoEnum.ANULADA);
        ampliacionBD.setUserModificacion(ds.getUsuario());
        ampliacionBD.setFechaModificacion(new Date());
        ampliacionVacanteDAO.update(ampliacionBD);
    }

    @Override
    public AmpliacionVacantes findAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm) {
        return ampliacionVacanteDAO.find(ampliacionVacanteForm);
    }

    @Override
    public List<Oficina> allOficinaByPersona(Persona persona) {
        return oficinaService.allOficinasMainByPersona(persona);
    }

    @Override
    @Transactional
    public void aceptarAmpliacionVacante(AmpliacionVacantes ampliacionForm, DataSessionPivot ds) {
        //TODO AMPLIACION VACANTE
        AmpliacionVacantes ampliacionBD = ampliacionVacanteDAO.find(ampliacionForm);

        Seccion seccion = seccionDAO.find(ampliacionBD.getSeccion());

        if (seccion.getMatriculados() > ampliacionBD.getVacantesFin()) {
            throw new PhobosException("Todas las vacantes ya fueron acupadas");
        }

        Aula aula = seccion.getAula();

        if (aula != null && aula.getCapacidadAula() != null) {
            if (ampliacionBD.getVacantesFin() > aula.getCapacidadAula()) {
                throw new PhobosException("Ya ha completo la capacidad del aula");
            }
        }

        ampliacionBD.setEstadoEnum(AmpliacionVacanteEstadoEnum.ACEPTADO);
        ampliacionBD.setUserModificacion(ds.getUsuario());
        ampliacionBD.setFechaModificacion(new Date());
        ampliacionBD.setFechaRespuesta(new Date());
        ampliacionVacanteDAO.update(ampliacionBD);

        seccion.setVacantes(ampliacionBD.getVacantesFin());
        seccionDAO.update(seccion);

        Seccion secSuperior = seccion.getSeccionSuperior();
        if (secSuperior != null) {
            secSuperior.setVacantes(secSuperior.getVacantes() + ampliacionBD.getIncremento());
            seccionDAO.update(secSuperior);
        }
    }

    @Override
    @Transactional
    public void rechazarAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm, DataSessionPivot ds) {

        AmpliacionVacantes ampliacionVacante = ampliacionVacanteDAO.find(ampliacionVacanteForm);
        ampliacionVacante.setEstadoEnum(AmpliacionVacanteEstadoEnum.RECHAZADO);
        ampliacionVacante.setUserModificacion(ds.getUsuario());
        ampliacionVacante.setFechaModificacion(new Date());
        ampliacionVacante.setFechaRespuesta(new Date());
        ampliacionVacante.setComentarioRespuesta(ampliacionVacanteForm.getComentarioRespuesta());
        ampliacionVacanteDAO.update(ampliacionVacante);
    }

}
