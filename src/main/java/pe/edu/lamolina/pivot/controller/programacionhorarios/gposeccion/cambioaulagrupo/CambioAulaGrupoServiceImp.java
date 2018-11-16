package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.cambioaulagrupo;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CambioAulaGrupoEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.CambioAulaGrupoDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CambioAulaGrupoServiceImp implements CambioAulaGrupoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CambioAulaGrupoDAO cambioAulaGrupoDAO;

    @Autowired
    OficinaService oficinaService;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Override
    public List<CambioAulaGrupo> allAulaGrupos(Seccion seccion) {
        return cambioAulaGrupoDAO.allBySeccion(seccion);

    }

    @Override
    @Transactional
    public void saveCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupo, DataSessionPivot ds) {
        
        Persona persona = ds.getPersona();
        Oficina oficinaMain = cambioAulaGrupo.getOficina();
        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());

        cambioAulaGrupo.setColaborador(colaborador);

        Seccion seccionForm = cambioAulaGrupo.getSeccion();
        Seccion seccionBD = seccionDAO.find(seccionForm);

        cambioAulaGrupo.setAulaInicio(seccionBD.getAula());
        cambioAulaGrupo.setGrupoHorasInicio(seccionBD.getGrupoHoras());

        cambioAulaGrupo.setFechaRegistro(new Date());
        cambioAulaGrupo.setFechaSolicitud(new Date());
        cambioAulaGrupo.setUserRegistro(ds.getUsuario());
        cambioAulaGrupo.setEstadoEnum(CambioAulaGrupoEstadoEnum.PENDIENTE);

        cambioAulaGrupoDAO.save(cambioAulaGrupo);
    }

    @Override
    @Transactional
    public void rechazarCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupoForm, DataSessionPivot ds) {
        Persona persona = ds.getPersona();
        Oficina oficinaMain = cambioAulaGrupoForm.getOficina();
        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());

        cambioAulaGrupoForm.setColaborador(colaborador);

        CambioAulaGrupo cambioAulaGrupo = cambioAulaGrupoDAO.find(cambioAulaGrupoForm);
        cambioAulaGrupo.setEstadoEnum(CambioAulaGrupoEstadoEnum.RECHAZADO);
        cambioAulaGrupo.setUserModificacion(ds.getUsuario());
        cambioAulaGrupo.setFechaModificacion(new Date());
        cambioAulaGrupo.setFechaRespuesta(new Date());
        cambioAulaGrupo.setComentarioRespuesta(cambioAulaGrupoForm.getComentarioRespuesta());
        cambioAulaGrupoDAO.update(cambioAulaGrupo);
    }

    @Override
    @Transactional
    public void deleteCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupoForm, DataSessionPivot ds) {
        CambioAulaGrupo cambioAulaGrupoBD = cambioAulaGrupoDAO.find(cambioAulaGrupoForm);
        Assert.isTrue(cambioAulaGrupoBD.getEstadoEnum() == CambioAulaGrupoEstadoEnum.PENDIENTE, "No puede eliminarse ni anularse esta solicitud");

        Persona personaAnulador = ds.getPersona();
        Persona personaSolicitud = cambioAulaGrupoBD.getColaborador().getPersona();
        if (personaAnulador.getId() != personaSolicitud.getId().longValue()) {
            Oficina oficinaMain = cambioAulaGrupoBD.getOficina();
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

        cambioAulaGrupoBD.setEstadoEnum(CambioAulaGrupoEstadoEnum.ANULADA);
        cambioAulaGrupoBD.setUserModificacion(ds.getUsuario());
        cambioAulaGrupoBD.setFechaModificacion(new Date());
        cambioAulaGrupoDAO.update(cambioAulaGrupoBD);
    }

    @Override
    @Transactional
    public void aceptarCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupoForm, DataSessionPivot ds) {
        CambioAulaGrupo cambioAulaGrupoBD = cambioAulaGrupoDAO.find(cambioAulaGrupoForm);

        Persona persona = ds.getPersona();
        Oficina oficinaMain = cambioAulaGrupoForm.getOficina();
        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());

        cambioAulaGrupoForm.setColaborador(colaborador);

        Seccion seccionForm = cambioAulaGrupoForm.getSeccion();
        Seccion seccionBD = seccionDAO.find(seccionForm);

        cambioAulaGrupoForm.setAulaInicio(seccionBD.getAula());
        cambioAulaGrupoForm.setGrupoHorasInicio(seccionBD.getGrupoHoras());

        cambioAulaGrupoForm.setEstadoEnum(CambioAulaGrupoEstadoEnum.ACEPTADO);
        cambioAulaGrupoForm.setUserRegistro(ds.getUsuario());
        cambioAulaGrupoForm.setUserModificacion(ds.getUsuario());
        cambioAulaGrupoForm.setFechaModificacion(new Date());
        cambioAulaGrupoForm.setFechaRespuesta(new Date());
        cambioAulaGrupoDAO.update(cambioAulaGrupoForm);

    }

    @Override
    public List<Aula> searchCambioAulaByName(String nombre, CicloAcademico ciclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        List<Aula> aulas = aulaDAO.searchByNombreFilter(nombre, 15);

        return aulas;
    }

    @Override
    public List<GrupoHoras> searchCambioGrupoByName(String nombre, CicloAcademico ciclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        List<GrupoHoras> gruposHoras = grupoHorasDAO.searchByNombreFilter(nombre, 15);

        return gruposHoras;
    }

}
