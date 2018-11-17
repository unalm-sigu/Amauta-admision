package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.cambioaulagrupo;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CambioAulaGrupoEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.CambioAulaGrupoDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
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

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

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
        
        if(true){
            return;
        }

        //cambioAulaGrupoDAO.save(cambioAulaGrupo);
        Date hoy = new Date();
        logger.debug("init reserva with Today {}", hoy);
        logger.debug("seccion {}", seccionBD.getId());
        logger.debug("grupo seccion {}", seccionBD.getGrupoSeccion().getId());
        logger.debug("ciclo academico {}", ds.getCicloAcademico().getId());

        GrupoHoras grupoHoraInicio = cambioAulaGrupo.getGrupoHorasInicio();
        GrupoHoras grupoHoraFin = cambioAulaGrupo.getGrupoHorasFin();

        List<DiaHoraGrupo> diaHoraGrupoInicio = diaHoraGrupoDAO.allByGrupoCiclo(grupoHoraInicio, ds.getCicloAcademico());

        for (DiaHoraGrupo diaHoraGrp : diaHoraGrupoInicio) {

            List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccionDiaHora(seccionBD, diaHoraGrp.getDia(), diaHoraGrp.getHora());

            for (HorarioSeccion horarioSeccion : horariosSeccion) {
                Date fechaInicio = horarioSeccion.getFechaInicio();
                if (fechaInicio.compareTo(hoy) > 0) {

                    HorarioSeccion horSeccion = new HorarioSeccion();
                    horSeccion.setEstadoEnum(EstadoHorarioAulaEnum.SOL);
                    horSeccion.setDia(horarioSeccion.getDia());
                    horSeccion.setHora(horarioSeccion.getHora());
                    horSeccion.setAula(horarioSeccion.getAula());
                    horSeccion.setSeccion(horarioSeccion.getSeccion());
                    horSeccion.setFechaInicio(hoy);
                    horSeccion.setReservado("Y");
                    horarioSeccionDAO.save(horSeccion);

                    horarioSeccion.setFechaFin(hoy);
                    horarioSeccion.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                    horarioSeccion.setReservado(null);
                    horarioSeccionDAO.update(horarioSeccion);

                    HorarioSeccion horSeccionPend = new HorarioSeccion();
                    horSeccionPend.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                    horSeccionPend.setDia(horarioSeccion.getDia());
                    horSeccionPend.setHora(horarioSeccion.getHora());
                    horSeccionPend.setAula(horarioSeccion.getAula());
                    horSeccionPend.setSeccion(horarioSeccion.getSeccion());
                    horSeccionPend.setFechaInicio(hoy);
                    horSeccion.setReservado("Y");
                    horarioSeccionDAO.save(horSeccionPend);

                } else {

                    HorarioSeccion horSeccion = new HorarioSeccion();
                    horSeccion.setEstadoEnum(EstadoHorarioAulaEnum.SOL);
                    horSeccion.setDia(horarioSeccion.getDia());
                    horSeccion.setHora(horarioSeccion.getHora());
                    horSeccion.setAula(horarioSeccion.getAula());
                    horSeccion.setSeccion(horarioSeccion.getSeccion());
                    horSeccion.setFechaInicio(hoy);
                    horSeccion.setReservado("Y");
                    horarioSeccionDAO.save(horSeccion);

                    horarioSeccion.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                    horarioSeccion.setReservado(null);
                    horarioSeccionDAO.update(horarioSeccion);

                }

            }

            List<HorarioAula> horariosAula = horarioAulaDAO.allBySeccionDiaHora(seccionBD, diaHoraGrp.getDia(), diaHoraGrp.getHora());

            for (HorarioAula horarioAula : horariosAula) {
                Date fechaInicio = horarioAula.getFechaInicio();
                if (fechaInicio.compareTo(hoy) > 0) {

                    HorarioAula horAula = new HorarioAula();
                    horAula.setEstadoEnum(EstadoHorarioAulaEnum.SOL);
                    horAula.setDia(horarioAula.getDia());
                    horAula.setHora(horarioAula.getHora());
                    horAula.setAula(horarioAula.getAula());
                    horAula.setSeccion(horarioAula.getSeccion());
                    horAula.setFechaInicio(hoy);
                    horAula.setReservado("Y");
                    horarioAulaDAO.save(horAula);

                    horarioAula.setFechaFin(hoy);
                    horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                    horarioAula.setReservado(null);
                    horarioAulaDAO.update(horarioAula);

                    HorarioAula horAulaPend = new HorarioAula();
                    horAulaPend.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                    horAulaPend.setDia(horarioAula.getDia());
                    horAulaPend.setHora(horarioAula.getHora());
                    horAulaPend.setAula(horarioAula.getAula());
                    horAulaPend.setSeccion(horarioAula.getSeccion());
                    horAulaPend.setFechaInicio(hoy);
                    horAula.setReservado("Y");
                    horarioAulaDAO.save(horAulaPend);

                } else {

                    HorarioAula horAula = new HorarioAula();
                    horAula.setEstadoEnum(EstadoHorarioAulaEnum.SOL);
                    horAula.setDia(horarioAula.getDia());
                    horAula.setHora(horarioAula.getHora());
                    horAula.setAula(horarioAula.getAula());
                    horAula.setSeccion(horarioAula.getSeccion());
                    horAula.setFechaInicio(hoy);
                    horAula.setReservado("Y");
                    horarioAulaDAO.save(horAula);

                    horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                    horarioAula.setReservado(null);
                    horarioAulaDAO.update(horarioAula);

                }

            }
        }
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
