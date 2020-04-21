package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.cambioaulagrupo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
import pe.edu.lamolina.amauta.controller.general.oficina.OficinaService;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.CambioAulaGrupoDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

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

        cambioAulaGrupoDAO.save(cambioAulaGrupo);

        if (cambioAulaGrupo.getGrupoHorasFin().getId().longValue() == cambioAulaGrupo.getGrupoHorasInicio().getId()) {
            throw new PhobosException("No puede asignarle el mismo grupo sección. ");
        }

        System.out.println("horas::" + cambioAulaGrupo.getGrupoHorasFin().getDiaHoraGrupo().size());

        if (cambioAulaGrupo.getGrupoHorasFin().getDiaHoraGrupo() != null) {
            logger.debug("grupoHoraFin.getDiaHoraGrupo size {}", cambioAulaGrupo.getGrupoHorasFin().getDiaHoraGrupo().size());
            if (cambioAulaGrupo.getGrupoHorasFin().getDiaHoraGrupo().size() != seccionBD.getHorasSemanales().intValue()) {
                throw new PhobosException("Asignar la cantidad de horas requeridas para la sección.");
            }
        } else {
            throw new PhobosException("No hay horarios para asignar.");
        }

        Date hoy = new Date();
        logger.debug("init reserva with Today {}", hoy);
        logger.debug("seccion {}", seccionBD.getId());
        logger.debug("seccion horas semanales {}", seccionBD.getHorasSemanales());
        logger.debug("grupo seccion {}", seccionBD.getGrupoSeccion().getId());
        logger.debug("ciclo academico {}", ds.getCicloAcademico().getId());

        GrupoHoras grupoHoraFin = cambioAulaGrupo.getGrupoHorasFin();

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccionBD);

        Map<String, List<HorarioSeccion>> horariosSeccionMap = TypesUtil.convertListToMapList("estado", horariosSeccion);

        List<HorarioSeccion> solicitados = horariosSeccionMap.get(EstadoHorarioAulaEnum.SOL.name());
        List<HorarioSeccion> activos = horariosSeccionMap.get(EstadoHorarioAulaEnum.ACT.name());
        List<HorarioSeccion> pendientes = horariosSeccionMap.get(EstadoHorarioAulaEnum.PEND.name());

        if (solicitados != null && !solicitados.isEmpty()) {
            throw new PhobosException("Tiene una solicitud pendiente.");
        }

        if (pendientes != null && !pendientes.isEmpty()) {
            throw new PhobosException("Tiene una solicitud pendiente.");
        }

        Date fechaInicio = activos.get(0).getFechaInicio();
        Date fechaFin = activos.get(0).getFechaFin();
        for (HorarioSeccion horarioSeccion : activos) {

            if (hoy.after(fechaInicio)) {

                HorarioSeccion horSeccionPend = new HorarioSeccion();
                horSeccionPend.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                horSeccionPend.setDia(horarioSeccion.getDia());
                horSeccionPend.setHora(horarioSeccion.getHora());
                horSeccionPend.setAula(horarioSeccion.getAula());
                horSeccionPend.setSeccion(seccionBD);
                horSeccionPend.setFechaInicio(hoy);
                horSeccionPend.setFechaFin(fechaFin);
                horSeccionPend.setReservado("Y");
                horarioSeccionDAO.save(horSeccionPend);
                logger.debug("creado horario seccion {}", horSeccionPend.getId());

                horarioSeccion.setFechaFin(hoy);
                horarioSeccion.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioSeccion.setReservado("Y");
                horarioSeccionDAO.update(horarioSeccion);
                logger.debug("updated horario seccion {}", horarioSeccion.getId());

            } else {

                horarioSeccion.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                horarioSeccion.setReservado("Y");
                horarioSeccionDAO.update(horarioSeccion);
                logger.debug("only updated horario seccion {}", horarioSeccion.getId());

            }

        }

        List<HorarioAula> horariosAula = horarioAulaDAO.allBySeccion(seccionBD);

        for (HorarioAula horarioAula : horariosAula) {

            if (hoy.after(fechaInicio)) {

                HorarioAula horAulaPend = new HorarioAula();
                horAulaPend.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                horAulaPend.setDia(horarioAula.getDia());
                horAulaPend.setHora(horarioAula.getHora());
                horAulaPend.setAula(horarioAula.getAula());
                horAulaPend.setSeccion(seccionBD);
                horAulaPend.setFechaInicio(hoy);
                horAulaPend.setFechaFin(fechaFin);
                horAulaPend.setReservado("Y");
                horarioAulaDAO.save(horAulaPend);
                logger.debug("creado horario aula {}", horAulaPend.getId());

                horarioAula.setFechaFin(hoy);
                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioAula.setReservado("Y");
                horarioAulaDAO.update(horarioAula);
                logger.debug("updated horario aula {}", horarioAula.getId());

            } else {

                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.PEND);
                horarioAula.setReservado("Y");
                horarioAulaDAO.update(horarioAula);
                logger.debug("only updated horario aula {}", horarioAula.getId());

            }

        }

        logger.debug("grupoHoraFin.getDiaHoraGrupo  {}", grupoHoraFin.getDiaHoraGrupo() != null);
        if (grupoHoraFin.getDiaHoraGrupo() != null) {
            logger.debug("grupoHoraFin.getDiaHoraGrupo size {}", grupoHoraFin.getDiaHoraGrupo().size());

            List<DiaHoraGrupo> diaHoraGrupos = diaHoraGrupoDAO.allByDiaHoraGrupo(grupoHoraFin.getDiaHoraGrupo());
            for (DiaHoraGrupo diaHoraGrupo : diaHoraGrupos) {
                logger.debug("***** new DiaHoraGrupo {}", diaHoraGrupo.getId());

                HorarioSeccion horSeccion = new HorarioSeccion();
                horSeccion.setEstadoEnum(EstadoHorarioAulaEnum.SOL);
                horSeccion.setDia(diaHoraGrupo.getDia());
                horSeccion.setHora(diaHoraGrupo.getHora());
                horSeccion.setAula(cambioAulaGrupo.getAulaFin());
                horSeccion.setSeccion(seccionBD);
                horSeccion.setFechaInicio(hoy);
                horSeccion.setFechaFin(fechaFin);
                horSeccion.setReservado("Y");
                horarioSeccionDAO.save(horSeccion);

                HorarioAula horAula = new HorarioAula();
                horAula.setEstadoEnum(EstadoHorarioAulaEnum.SOL);
                horAula.setDia(diaHoraGrupo.getDia());
                horAula.setHora(diaHoraGrupo.getHora());
                horAula.setAula(cambioAulaGrupo.getAulaFin());
                horAula.setSeccion(seccionBD);
                horAula.setFechaInicio(hoy);
                horAula.setFechaFin(fechaFin);
                horAula.setReservado("Y");
                horarioAulaDAO.save(horAula);

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

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(cambioAulaGrupo.getSeccion());
        Map<String, List<HorarioSeccion>> horariosSeccionMap = TypesUtil.convertListToMapList("estado", horariosSeccion);
        {
            List<HorarioSeccion> solicitados = horariosSeccionMap.get(EstadoHorarioAulaEnum.SOL.name());
            List<HorarioSeccion> activos = horariosSeccionMap.get(EstadoHorarioAulaEnum.ACT.name());
            List<HorarioSeccion> pendientes = horariosSeccionMap.get(EstadoHorarioAulaEnum.PEND.name());

            if (activos != null && !activos.isEmpty()) {

                Date fechaFin = pendientes.get(0).getFechaFin();

                for (HorarioSeccion activo : activos) {
                    activo.setFechaFin(fechaFin);
                    activo.setReservado(null);
                    horarioSeccionDAO.update(activo);
                }
                for (HorarioSeccion pendiente : pendientes) {
                    horarioSeccionDAO.delete(pendiente);
                }
                for (HorarioSeccion solicitado : solicitados) {
                    horarioSeccionDAO.delete(solicitado);
                }

            } else {

                for (HorarioSeccion solicitado : solicitados) {
                    horarioSeccionDAO.delete(solicitado);
                }

            }
        }

        List<HorarioAula> horariosAula = horarioAulaDAO.allBySeccion(cambioAulaGrupo.getSeccion());
        Map<String, List<HorarioAula>> horarioAulaMap = TypesUtil.convertListToMapList("estado", horariosAula);
        {
            List<HorarioAula> solicitados = horarioAulaMap.get(EstadoHorarioAulaEnum.SOL.name());
            List<HorarioAula> activos = horarioAulaMap.get(EstadoHorarioAulaEnum.ACT.name());
            List<HorarioAula> pendientes = horarioAulaMap.get(EstadoHorarioAulaEnum.PEND.name());

            if (activos != null && !activos.isEmpty()) {

                Date fechaFin = pendientes.get(0).getFechaFin();

                for (HorarioAula activo : activos) {
                    activo.setFechaFin(fechaFin);
                    activo.setReservado(null);
                    horarioAulaDAO.update(activo);
                }
                if (pendientes != null) {
                    for (HorarioAula pendiente : pendientes) {
                        horarioAulaDAO.delete(pendiente);
                    }
                }
                if (solicitados != null) {
                    for (HorarioAula solicitado : solicitados) {
                        horarioAulaDAO.delete(solicitado);
                    }
                }

            } else {
                if (solicitados != null) {
                    for (HorarioAula solicitado : solicitados) {
                        horarioAulaDAO.delete(solicitado);
                    }
                }
            }
        }
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

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(cambioAulaGrupoBD.getSeccion());
        Map<String, List<HorarioSeccion>> horariosSeccionMap = TypesUtil.convertListToMapList("estado", horariosSeccion);
        {
            List<HorarioSeccion> solicitados = horariosSeccionMap.get(EstadoHorarioAulaEnum.SOL.name());
            List<HorarioSeccion> activos = horariosSeccionMap.get(EstadoHorarioAulaEnum.ACT.name());
            List<HorarioSeccion> pendientes = horariosSeccionMap.get(EstadoHorarioAulaEnum.PEND.name());

            if (activos != null && !activos.isEmpty()) {

                Date fechaFin = pendientes.get(0).getFechaFin();

                for (HorarioSeccion activo : activos) {
                    activo.setFechaFin(fechaFin);
                    activo.setReservado(null);
                    horarioSeccionDAO.update(activo);
                }
                if (pendientes != null) {
                    for (HorarioSeccion pendiente : pendientes) {
                        horarioSeccionDAO.delete(pendiente);
                    }
                }
                if (solicitados != null) {
                    for (HorarioSeccion solicitado : solicitados) {
                        horarioSeccionDAO.delete(solicitado);
                    }
                }

            } else {
                if (solicitados != null) {
                    for (HorarioSeccion solicitado : solicitados) {
                        horarioSeccionDAO.delete(solicitado);
                    }
                }
            }
        }

        List<HorarioAula> horariosAula = horarioAulaDAO.allBySeccion(cambioAulaGrupoBD.getSeccion());
        Map<String, List<HorarioAula>> horariosAulaMap = TypesUtil.convertListToMapList("estado", horariosAula);
        {
            List<HorarioAula> solicitados = horariosAulaMap.get(EstadoHorarioAulaEnum.SOL.name());
            List<HorarioAula> activos = horariosAulaMap.get(EstadoHorarioAulaEnum.ACT.name());
            List<HorarioAula> pendientes = horariosAulaMap.get(EstadoHorarioAulaEnum.PEND.name());

            if (activos != null && !activos.isEmpty()) {

                Date fechaFin = pendientes.get(0).getFechaFin();

                for (HorarioAula activo : activos) {
                    activo.setFechaFin(fechaFin);
                    activo.setReservado(null);
                    horarioAulaDAO.update(activo);
                }
                if (pendientes != null) {
                    for (HorarioAula pendiente : pendientes) {
                        horarioAulaDAO.delete(pendiente);
                    }
                }
                if (solicitados != null) {
                    for (HorarioAula solicitado : solicitados) {
                        horarioAulaDAO.delete(solicitado);
                    }
                }

            } else {
                if (solicitados != null) {
                    for (HorarioAula solicitado : solicitados) {
                        horarioAulaDAO.delete(solicitado);
                    }
                }
            }
        }

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

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(cambioAulaGrupoBD.getSeccion());
        Map<String, List<HorarioSeccion>> mapHorariosSeccion = TypesUtil.convertListToMapList("estado", horariosSeccion);

        {
            List<HorarioSeccion> solicitados = getListNotNull(mapHorariosSeccion.get(EstadoHorarioAulaEnum.SOL.name()));
            List<HorarioSeccion> activos = getListNotNull(mapHorariosSeccion.get(EstadoHorarioAulaEnum.ACT.name()));
            List<HorarioSeccion> pendientes = getListNotNull(mapHorariosSeccion.get(EstadoHorarioAulaEnum.PEND.name()));

            if (solicitados == null) {
                throw new PhobosException("Tiene una solicitud pendiente.");
            }

            for (HorarioSeccion solicitado : solicitados) {
                solicitado.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                solicitado.setReservado(null);
                horarioSeccionDAO.update(solicitado);
            }
            for (HorarioSeccion activo : activos) {
                activo.setReservado(null);
                horarioSeccionDAO.update(activo);
            }
            for (HorarioSeccion pendiente : pendientes) {
                horarioSeccionDAO.delete(pendiente);
            }
        }

        List<HorarioAula> horariosAula = horarioAulaDAO.allBySeccion(cambioAulaGrupoBD.getSeccion());
        Map<String, List<HorarioAula>> mapHorariosAula = TypesUtil.convertListToMapList("estado", horariosAula);
        {
            List<HorarioAula> solicitados = getListNotNull(mapHorariosAula.get(EstadoHorarioAulaEnum.SOL.name()));
            List<HorarioAula> activos = getListNotNull(mapHorariosAula.get(EstadoHorarioAulaEnum.ACT.name()));
            List<HorarioAula> pendientes = getListNotNull(mapHorariosAula.get(EstadoHorarioAulaEnum.PEND.name()));

            if (solicitados == null) {
                throw new PhobosException("Tiene una solicitud pendiente.");
            }

            for (HorarioAula solicitado : solicitados) {
                solicitado.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                solicitado.setReservado(null);
                horarioAulaDAO.update(solicitado);
            }
            for (HorarioAula activo : activos) {
                activo.setReservado(null);
                horarioAulaDAO.update(activo);
            }
            for (HorarioAula pendiente : pendientes) {
                horarioAulaDAO.delete(pendiente);
            }
        }

    }

    private List getListNotNull(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
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
