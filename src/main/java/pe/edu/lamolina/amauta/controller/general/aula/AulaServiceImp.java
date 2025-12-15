package pe.edu.lamolina.amauta.controller.general.aula;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.*;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.enums.*;

import static pe.edu.lamolina.model.enums.RolEnum.INF_OBUAE;
import static pe.edu.lamolina.model.enums.RolEnum.IOREA;
import static pe.edu.lamolina.model.enums.RolEnum.OREA;
import static pe.edu.lamolina.model.enums.RolEnum.RESCULT;
import static pe.edu.lamolina.model.enums.RolEnum.RESDEP;

import pe.edu.lamolina.model.general.*;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.almacen.ResumenInventarioDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.general.ResponsableAulaAsignacionDAO;
import pe.edu.lamolina.amauta.dao.general.ResponsableAulaDAO;
import pe.edu.lamolina.amauta.dao.general.SedeDAO;
import pe.edu.lamolina.amauta.dao.general.TipoAulaDAO;
import pe.edu.lamolina.amauta.dao.general.TipoCarpetaDAO;
import pe.edu.lamolina.amauta.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import static pe.edu.lamolina.model.enums.RolEnum.OPER_PROGH_OERA;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AulaServiceImp implements AulaService {

    private final AulaDAO aulaDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final DiaDAO diaDAO;
    private final DiaHoraGrupoDAO diaHoraGrupoDAO;
    private final DocenteSeccionDAO docenteSeccionDAO;
    private final EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    private final HoraDAO horaDAO;
    private final HorarioAulaDAO horarioAulaDAO;
    private final HorarioSeccionDAO horarioSeccionDAO;
    private final OficinaDAO oficinaDAO;
    private final ResponsableAulaAsignacionDAO responsableAulaAsignacionDAO;
    private final ResponsableAulaDAO responsableAulaDAO;
    private final ResumenInventarioDAO resumenInventarioDAO;
    private final SedeDAO sedeDAO;
    private final TipoAulaDAO tipoAulaDAO;
    private final TipoCarpetaDAO tipoCarpetaDAO;
    private final UsuarioRolDAO usuarioRolDAO;

    private final VerificadorService verificadorService;

    private final String SOPORTE_TECNICO_DERA = "SOPORTE_TECNICO_DERA";
    private final String PERSONAL_AULA = "PAULA";
    private final String TIPO_AULA_LABORATORIO = "LAB";

    @Override
    public List<Aula> allByDynatable(DynatableFilter filter, DataSessionPivot ds) {

        List<Oficina> oficinas = getOficinas(ds);
        Boolean filterObu = this.filterByRol(ds);
        log.info("[allByDynatable] filterObu={} oficinas={} ", filterObu, toString(oficinas));

        List<String> roles = ds.getRoles().stream().map(b -> b.getCodigo()).collect(Collectors.toList());

        Long idPersona = ds.getUsuario().getPersona().getId();
        List<ResponsableAulaAsignacion> responsableAulas = responsableAulaAsignacionDAO.allByAulas(idPersona);
        List<Long> aulaSuperior = responsableAulas.stream().map(a -> a.getAula().getId()).collect(Collectors.toList());

        List<Aula> aulas = new ArrayList();
        if (roles.contains(OPER_PROGH_OERA.name())) {
            log.info("Con permisos temporalmente hasta que las demas oficinas asuman su Rol.");
            List<Oficina> oficiAulasLab = oficinaDAO.allByLaboratorios(TIPO_AULA_LABORATORIO);
            for (Oficina oficina : oficiAulasLab) {
                oficinas.add(oficina);
            }
            aulas = aulaDAO.allByDynatable(filter, oficiAulasLab);

        } else if (roles.contains(SOPORTE_TECNICO_DERA)) {
            Oficina oficinaDera = oficinaDAO.findByCode(OficinaEnum.OERA.name());
            aulas = aulaDAO.allByDynatable(filter, oficinaDera);

        } else if (roles.contains(PERSONAL_AULA)) {
            Oficina oficinaDera = oficinaDAO.findByCode(OficinaEnum.OERA.name());
            aulas = aulaDAO.allByDynatable(filter, oficinaDera, aulaSuperior);

        } else if (!oficinas.isEmpty()) {
            aulas = aulaDAO.allByDynatable(filter, oficinas);
        }

        List<Aula> aulasHijas = aulaDAO.allByAulasSuperiores(aulas);
        List<ResumenInventario> resumenAulas = resumenInventarioDAO.allVisiblesByAulas(aulas);
        List<HorarioAula> horariosAulasByCiclo = horarioAulaDAO.allByCicloAndTipoHorario(ds.getCicloAcademico(), aulas, TipoHorarioAulaEnum.DICT);

        Map<Long, List<ResumenInventario>> resumenAulasMap = TypesUtil.convertListToMapList("almacen.aula.id", resumenAulas);
        Map<Long, List<Aula>> mapAulas = TypesUtil.convertListToMapList("aulaSuperior.id", aulasHijas);
        Map<Long, List<HorarioAula>> mapHorariosAulas = TypesUtil.convertListToMapList("aula.id", horariosAulasByCiclo);
        for (Aula aula : aulas) {
            List<Aula> hijas = mapAulas.get(aula.getId());
            hijas = hijas == null ? new ArrayList() : hijas;
            aula.setAulasContenido(hijas);
            List<ResumenInventario> inventarios = resumenAulasMap.get(aula.getId());
            aula.setInventario(inventarios);
            List<HorarioAula> horariosAulasByAula = mapHorariosAulas.get(aula.getId());
            aula.setSeccion(new ArrayList<>());
            if (horariosAulasByAula != null) {
                List<Seccion> seccionesByAula = horariosAulasByAula.stream()
                        .filter(x -> x.getSeccion() != null)
                        .map(x -> x.getSeccion())
                        .distinct().collect(Collectors.toList());
                aula.setSeccion(seccionesByAula);
            }
        }
        return aulas;
    }

    @Override
    public List<TipoAula> allTiposAula(DataSessionPivot ds) {
        List<TipoAula> tipox = new ArrayList();
        List<TipoAula> tipos = new ArrayList<>();
        List<String> roles = ds.getRoles().stream().map(b -> b.getCodigo()).collect(Collectors.toList());
        if (roles.contains(SOPORTE_TECNICO_DERA)) {
            tipos = tipoAulaDAO.allByCodigos(Arrays.asList("AUL", "AUD"));
        } else {
            tipos = tipoAulaDAO.all();
        }

        for (TipoAula tipo : tipos) {
            TipoAula ta = new TipoAula(tipo.getId());
            ta.setCodigo(tipo.getCodigo());
            ta.setNombre(tipo.getNombre());
            ta.setTipoAmbiente(tipo.getTipoAmbienteEnum());
            tipox.add(ta);
        }
        return tipox;

    }

    @Override
    public List<TipoAula> allTiposAula() {
        List<TipoAula> tipox = new ArrayList();
        List<TipoAula> tipos = tipoAulaDAO.all();
        for (TipoAula tipo : tipos) {
            TipoAula ta = new TipoAula(tipo.getId());
            ta.setCodigo(tipo.getCodigo());
            ta.setNombre(tipo.getNombre());
            ta.setTipoAmbiente(tipo.getTipoAmbienteEnum());
            tipox.add(ta);
        }
        return tipox;
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<Aula> allAulasSuperioresByName(String nombre) {
        return aulaDAO.allAulasSuperioresByName(this.forLike(nombre));
    }

    @Override
    public List<Sede> allSedes() {
        return sedeDAO.all();
    }

    @Override
    public List<Oficina> allOficinasByName(String nombre) {
        return oficinaDAO.allOficinasByName(this.forLike(nombre));
    }

    @Override
    @Transactional
    public void save(Aula aula, DataSessionPivot ds) {
        boolean esInformativo = verificadorService.esInformaticoOERA(ds);
        aula.setCodigo(aula.getCodigo().toUpperCase().replaceAll("\\s+", ""));
        Aula aulaTmp = aulaDAO.findByCode(aula.getCodigo());
        Assert.isNull(aulaTmp, "Este código ya fue asignado a otro ambiente");
        TipoCarpeta tipocarpeta = null;
        if (aula.getTipoCarpeta().getId() != null) {

            tipocarpeta = tipoCarpetaDAO.find(aula.getTipoCarpeta().getId());
        }

        if (aula.getTipoAmbienteEnum() == TipoAmbienteEnum.EDI) {
            aula.setAforo(0);
        }

        ObjectUtil.eliminarAttrSinId(aula, "aulaSuperior");
        ObjectUtil.eliminarAttrSinId(aula, "sede");
        ObjectUtil.eliminarAttrSinId(aula, "tipoAula");
        ObjectUtil.eliminarAttrSinId(aula, "oficinaSupervisora");
        ObjectUtil.eliminarAttrSinId(aula, "tipoCarpeta");

        Aula aulaSup = aula.getAulaSuperior();
        if (aulaSup != null) {
            aulaSup = aulaDAO.find(aulaSup.getId());
            Assert.isTrue(aulaSup.getTipoAmbienteEnum() == TipoAmbienteEnum.EDI, "Un ambiente solo debería pertenecer a otro del tipo Edificio");
        }

        if (aula.getOficinaSupervisora() != null && !esInformativo) {
            Oficina supervisora = oficinaDAO.find(aula.getOficinaSupervisora());
            Assert.isNotNull(supervisora, "No se pudo ubicar a la oficina supervisora");
            Assert.isNotNull(supervisora.getOficinaPrincipal(), "No se pudo ubicar a la oficina principal");

            Oficina oficinaMain = supervisora.getOficinaPrincipal();
            List<Oficina> oficinas = getOficinas(ds);
            Oficina existe = oficinas.stream()
                    .filter(ofi -> ofi.equals(oficinaMain))
                    .findAny().orElse(null);
            Assert.isNotNull(existe, "No tiene permiso para registrar un ambiente para esta oficina supervisora");
        }

        revisarNombre(aula);
        aula.setEstadoEnum(EstadoEnum.CRE);
        aula.setUserRegistro(ds.getUsuario());
        aula.setTipoCarpeta(tipocarpeta);
        aula.setFechaRegistro(new Date());
        aula.setCodigoPronabec(aula.getCodigoPronabec());
        aulaDAO.save(aula);
    }

    @Override
    @Transactional
    public void update(Aula aula, DataSessionPivot ds) {
        boolean esInformativo = verificadorService.esInformaticoOERA(ds);
        boolean esProgramacionAula = verificadorService.isEditorProgramacionOera(ds);
        aula.setCodigo(aula.getCodigo().toUpperCase().replaceAll("\\s+", ""));
        Aula aulaBD = aulaDAO.findByCode(aula.getCodigo());
        if (aulaBD != null) {
            Assert.isTrue(aula.getId() == aulaBD.getId().longValue(), "El código ya pertenece a otro ambiente");
        } else {
            aulaBD = aulaDAO.find(aula.getId());
        }

        ObjectUtil.eliminarAttrSinId(aula, "aulaSuperior");
        ObjectUtil.eliminarAttrSinId(aula, "sede");
        ObjectUtil.eliminarAttrSinId(aula, "tipoAula");
        ObjectUtil.eliminarAttrSinId(aula, "oficinaSupervisora");
        if (ObjectUtil.getParentTree(aula, "tipoCarpeta.id") == null) {
            aula.setTipoCarpeta(null);
        }

        Aula aulaSup = aula.getAulaSuperior();
        if (aula.getTipoAmbienteEnum() == TipoAmbienteEnum.EDI) {
            Assert.isTrue(aulaSup == null, "Un ambiente tipo Edificio no puede pertenecer parte de otro Ambiente");

            Integer aforoTotal = 0;
            List<Aula> aulasHijo = aulaDAO.allByAulaSuperior(aula);
            for (Aula aulaHijo : aulasHijo) {
                if (aulaHijo.getEstadoEnum() == EstadoEnum.ACT) {
                    aforoTotal = aulaHijo.getAforo() == null ? 0 : aulaHijo.getAforo();
                }
            }
            aula.setAforo(aforoTotal);
        }
        if (aulaSup != null) {
            aulaSup = aulaDAO.find(aulaSup.getId());
            Assert.isTrue(aulaSup.getTipoAmbienteEnum() == TipoAmbienteEnum.EDI, "Un ambiente solo debería pertenecer a otro del tipo Edificio");
        }

        if (aula.getOficinaSupervisora() != null && !esInformativo && !esProgramacionAula ) {
            Oficina supervisora = oficinaDAO.find(aula.getOficinaSupervisora());
            Assert.isNotNull(supervisora, "No se pudo ubicar a la oficina supervisora");
            Assert.isNotNull(supervisora.getOficinaPrincipal(), "No se pudo ubicar a la oficina principal");

            Oficina oficinaMain = supervisora.getOficinaPrincipal();
            List<Oficina> oficinas = getOficinas(ds);
            Oficina existe = oficinas.stream()
                    .filter(ofi -> ofi.equals(oficinaMain))
                    .findAny().orElse(null);
            Assert.isNotNull(existe, "No tiene permiso para registrar un ambiente para esta oficina supervisora");
        }

        revisarNombre(aula);
        aulaBD.setAulaSuperior(aula.getAulaSuperior());
        aulaBD.setSede(aula.getSede());
        aulaBD.setTipoAula(aula.getTipoAula());
        aulaBD.setTipoCarpeta(aula.getTipoCarpeta());
        aulaBD.setOficinaSupervisora(aula.getOficinaSupervisora());

        aulaBD.setAforo(aula.getAforo());
        aulaBD.setCapacidadAula(aula.getCapacidadAula());
        aulaBD.setCapacidadExtra(aula.getCapacidadExtra());
        aulaBD.setCodigo(aula.getCodigo());
        aulaBD.setCodigoPronabec(aula.getCodigoPronabec());
        aulaBD.setNombre(aula.getNombre());
        aulaBD.setPermiteCruce(aula.getPermiteCruce());
        aulaBD.setPiso(aula.getPiso());
        aulaBD.setPisos(aula.getPisos());
        aulaBD.setTipoAmbiente(aula.getTipoAmbiente());

        aulaDAO.update(aulaBD);
    }

    private void revisarNombre(Aula aula) {
        String nom = aula.getNombre();
        if (nom == null) {
            return;
        }
        nom = nom.trim();
        if (StringUtils.isEmpty(nom)) {
            nom = null;
        }
        aula.setNombre(nom);
    }

    @Override
    public Aula findAulaById(Long id) {
        return aulaDAO.find(id);
    }

    @Override
    @Transactional
    public void cambioEstado(Aula aula, DataSessionPivot ds) {
        Aula aulaBD = aulaDAO.find(aula.getId());

        if (aula.getEstadoEnum() == EstadoEnum.ACT) {
            Assert.isFalse(aulaBD.getEstadoEnum() == EstadoEnum.ACT, "Este ambiente ya se encuentra activo");
            aulaBD.setEstadoEnum(EstadoEnum.ACT);

        } else if (aula.getEstadoEnum() == EstadoEnum.INA) {
            Assert.isFalse(aulaBD.getEstadoEnum() == EstadoEnum.INA, "Este ambiente ya se encuentra desactivado");
            aulaBD.setEstadoEnum(EstadoEnum.INA);
            aulaBD.setMotivoAnulacion(aula.getMotivoAnulacion());
            aulaBD.setFechaAnulacion(new Date());
            aulaBD.setUserAnulacion(ds.getUsuario());
        }

        JsonHelper.createJson(ds, JsonNodeFactory.instance);

        aulaDAO.update(aulaBD);
    }

    @Override
    @Transactional
    public void eliminarAula(Aula aula) {
        Aula aulaBD = aulaDAO.find(aula.getId());
        Aula aulaSup = aulaBD.getAulaSuperior();
        if (aulaSup != null) {
            Integer aforo = aulaBD.getAforo() == null ? 0 : aulaBD.getAforo();
            aulaSup.setAforo(aulaSup.getAforo() - aforo);
            aulaDAO.update(aulaSup);
        }

        List<Aula> aulasHijas = aulaDAO.allByAulaSuperior(aula);
        Assert.isTrue(aulasHijas.isEmpty(), "Este ambiente es tipo Edificio que agrupa otros ambientes. Desvincule primero esos ambientes e intente eliminar");

        aulaDAO.delete(aulaBD);
    }

    @Override
    public Aula findAulaFull(Aula aulaFormFecha) {

        Aula aula = aulaDAO.find(aulaFormFecha.getId());
        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAulaFecha(aulaFormFecha);
        this.completarDocentes(horariosAulas);
        aula.setHorariosAula(horariosAulas);
        return aula;
    }

    @Override
    public Aula findAulaHorarioProgramacion(Aula aula) {
        aula = aulaDAO.find(aula.getId());
        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAulaFecha(aula);
        this.completarDocentes(horariosAulas);
        aula.setHorariosAula(horariosAulas);
        return aula;
    }

    @Override
    public List<Dia> allDia() {
        return diaDAO.allDia();
    }

    private void completarDocentes(List<HorarioAula> horariosAulas) {

        List<Seccion> secciones = horariosAulas.stream().filter(x -> x.getSeccion() != null).map(x -> x.getSeccion()).collect(Collectors.toList());

//        Map<Long, CicloAcademico> mapCiclos = TypesUtil.convertListToMap("grupoSeccion.cicloAcademico.id", "grupoSeccion.cicloAcademico", secciones);
//        List<CicloAcademico> ciclos = mapCiclos.values().stream().collect(Collectors.toList());
//        List<EventoCicloAcademico> eventoClases = eventoCicloAcademicoDAO.allActivoByCicloTipoEvento(ciclos, EventoAcademicoEnum.CLASES_PRE);
//        List<EventoCicloAcademico> eventoClasesEpg = eventoCicloAcademicoDAO.allActivoByCicloTipoEvento(ciclos, EventoAcademicoEnum.CLASES_EPG);
//        eventoClases.addAll(eventoClasesEpg);
//
//        Map<Long, EventoCicloAcademico> mapEventoClases = TypesUtil.convertListToMap("cicloAcademico.id", eventoClases);
//        for (Seccion seccion : secciones) {
//            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
//            if (TipoDictadoGrupoSeccionEnum.SEM.name().equalsIgnoreCase(gpoSecc.getTipoDictado())) {
//                EventoCicloAcademico evento = mapEventoClases.get(gpoSecc.getCicloAcademico().getId());
//                gpoSecc.setFechaInicioPeriodo(evento.getFechaInicio());
//                gpoSecc.setFechaFinPeriodo(evento.getFechaFin());
//            }
//        }
        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allPrincipalesBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> docenteSeccionesMap = TypesUtil.convertListToMapList("seccion.id", docenteSecciones);

        for (HorarioAula horariosAula : horariosAulas) {
            Seccion seccion = horariosAula.getSeccion();
            if (seccion != null) {
                seccion.setDocenteSeccion(null);
                List<DocenteSeccion> docenteSeccioness = docenteSeccionesMap.get(seccion.getId());
                seccion.setSizeDocente(docenteSeccioness != null ? docenteSeccioness.size() : 0);
                if (docenteSeccioness != null) {
                    if (docenteSeccioness.size() > 2) {
                        List<DocenteSeccion> misdocentes = new ArrayList();
                        misdocentes.add(docenteSeccioness.get(0));
                        misdocentes.add(docenteSeccioness.get(1));
                        docenteSeccioness = misdocentes;
                    }
                }
                seccion.setDocenteSeccion(docenteSeccioness);
            }
        }
    }

    @Override
    public List<TipoCarpeta> allTipoCarpeta() {
        return tipoCarpetaDAO.all();
    }

    @Override
    public List<Aula> allAulaByOficinaSuperior(DataSessionPivot ds) {
        return aulaDAO.allByListOficinaSupervisora(ds.getOficinas());
    }

    @Override
    public List<Aula> allAulaByAulaSuperior(List<Aula> listAulaSuperior) {
        return aulaDAO.allByAulasSuperiores(listAulaSuperior);
    }

    @Override
    public List<Dia> allDiaForPrinter() {
        return diaDAO.allDiaForPrinter();
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByCicloRegular(CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allByTipoGpoEnumCiclo(TipoGrupoHorasEnum.REGULAR, cicloAcademico);
    }

    @Override
    public List<Hora> returnHorasEncontradas(Aula aulaBD, List<Dia> dias, CicloAcademico ciclo) {

        List<DiaHoraGrupo> diasHorasGposRegulares = diaHoraGrupoDAO.allByTipoGpoEnumCiclo(TipoGrupoHorasEnum.REGULAR, ciclo);

        List<Hora> horasEncontradas = new ArrayList();
        for (HorarioAula horarioAula : aulaBD.getHorariosAula()) {
            if (!horasEncontradas.contains(horarioAula.getHora())) {
                horasEncontradas.add(horarioAula.getHora());
            }
        }

        Collections.sort(horasEncontradas, (horas1, horas2) -> horas1.getNumero().compareTo(horas2.getNumero()));

        List<HorarioAula> horarios = aulaBD.getHorariosAula();

        Map<String, HorarioAula> diasHorasMap = horarios.stream().collect(Collectors.toMap(x -> x.getHora().getId() + "-" + x.getDia().getId(), x -> x, (f, s) -> s));

        Map<String, DiaHoraGrupo> diaHoraGrupoMap = diasHorasGposRegulares.stream().collect(Collectors.toMap(x -> x.getHora().getId() + "-" + x.getDia().getId(), x -> x, (f, s) -> s));

        for (Hora horasEncontrada : horasEncontradas) {
            List<Dia> diass = new ArrayList();
            for (Dia dia : dias) {
                Dia diaClone = dia.clone();
                diaClone.setMainHorarioAula(null);
                diaClone.setGrupohoras(null);
                String key = horasEncontrada.getId() + "-" + dia.getId();
                HorarioAula horarioAula = diasHorasMap.get(key);
                DiaHoraGrupo diaHoraGrupo = diaHoraGrupoMap.get(key);
                diaClone.setMainHorarioAula(horarioAula);
                if (diaHoraGrupo != null) {
                    diaClone.setGrupohoras(diaHoraGrupo.getGrupoHorario());
                }
                diass.add(diaClone);
            }
            horasEncontrada.setDias(diass);
        }
        return horasEncontradas;
    }

    @Override
    public List<Hora> returnHorasEcontradasModal(Aula aula, List<Dia> dias) {

        List<Hora> horasEncontradas = new ArrayList<>();
        for (HorarioAula horarioAula : aula.getHorariosAula()) {
            if (!horasEncontradas.contains(horarioAula.getHora())) {
                horasEncontradas.add(horarioAula.getHora());
            }
        }

        Collections.sort(horasEncontradas, (horas1, horas2) -> horas1.getNumero().compareTo(horas2.getNumero()));

        List<HorarioAula> horarios = aula.getHorariosAula();

        Map<String, HorarioAula> diasHorasMap = horarios.stream().collect(Collectors.toMap(x -> x.getHora().getId() + "-" + x.getDia().getId(), x -> x, (f, s) -> s));

        for (Hora horasEncontrada : horasEncontradas) {
            List<Dia> diass = new ArrayList();
            for (Dia dia : dias) {
                Dia diaClone = dia.clone();
                diaClone.setMainHorarioAula(null);
                String key = horasEncontrada.getId() + "-" + dia.getId();
                HorarioAula horarioAula = diasHorasMap.get(key);
                diaClone.setMainHorarioAula(horarioAula);
                diass.add(diaClone);
            }
            horasEncontrada.setDias(diass);
        }

        return horasEncontradas;
    }

    @Override
    public List<Hora> allHorasHorario() {
        Hora horaInicial = horaDAO.findByNumeroHora(8); // 8AM
        Hora horaFinal = horaDAO.findByNumeroHora(21); // 9PM

        return horaDAO.allByInicioFin(horaInicial, horaFinal);

    }

    @Override
    public Aula findById(Aula aulaSuperiorForm) {
        return aulaDAO.find(aulaSuperiorForm.getId());
    }

    @Override
    public List<HorarioAula> allHorariosAulaByCiclo(CicloAcademico cicloAcademico, Aula aula) {

        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allByAulaCiclo(aula, OficinaEnum.OERA, cicloAcademico);

        if (horariosSecciones.size() == 0) {
            throw new PhobosException("No hay secciones programadas");
        }

        List<DocenteSeccion> docentesSeccionesByCiclo = docenteSeccionDAO.allByCicloAula(cicloAcademico, aula, OficinaEnum.OERA, EstadoEnum.ACT);
        docentesSeccionesByCiclo = docentesSeccionesByCiclo.stream()
                .filter(x -> x.esDocentePrincipal())
                .collect(Collectors.toList());

        Map<Long, List<DocenteSeccion>> mapDocenteSeccionBySeccion = TypesUtil.convertListToMapList("seccion.id", docentesSeccionesByCiclo);

        EventoAcademicoEnum eventoEnum = cicloAcademico.isTipoRegular() ? EventoAcademicoEnum.CLASES_PRE : EventoAcademicoEnum.CLASES_VER;
        EventoCicloAcademico eventoDictado = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, eventoEnum);
        List<HorarioAula> horariosAulasReservas
                = horarioAulaDAO.allByFechas(
                        eventoDictado.getFechaInicio(),
                        eventoDictado.getFechaFin(),
                        aula,
                        OficinaEnum.OERA,
                        TipoHorarioAulaEnum.RESERV);

        horariosAulasReservas.removeIf(x -> x.getReservaAula() != null && Boolean.FALSE.equals(x.getReservaAula().getVisibleHorario()));

        for (HorarioSeccion horarioSecc : horariosSecciones) {
            Seccion seccion = horarioSecc.getSeccion();
            if (seccion == null) {
                continue;
            }
            List<DocenteSeccion> docentesSecciones = TypesUtil.getListNotNull(mapDocenteSeccionBySeccion.get(seccion.getId()));
            seccion.setDocenteSeccion(docentesSecciones);
        }

        for (HorarioSeccion hs : horariosSecciones) {
            HorarioAula ha = new HorarioAula(hs.getSeccion(), hs.getDia(), hs.getHora(), hs.getAula());
            ha.setFechaInicio(hs.getFechaInicio());
            ha.setFechaFin(hs.getFechaFin());
            horariosAulasReservas.add(ha);
        }

        return horariosAulasReservas;
    }

    @Override
    public List<HorarioAula> allHorarioLaboratorioByCiclo(CicloAcademico cicloAcademico) {

        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allByCiclo(cicloAcademico);

        if (horariosSecciones.size() == 0) {
            throw new PhobosException("No hay secciones programadas");
        }

        horariosSecciones = horariosSecciones.stream()
                .filter(hs -> hs.getAula() != null)
                .filter(hs -> hs.getAula().getTipoAula() != null && "LAB".equals(hs.getAula().getTipoAula().getCodigo()))
                .filter(hs -> hs.getAula().getOficinaSupervisora() != null && !hs.getAula().getOficinaSupervisora().isOficinaOera())
                .filter(hs -> hs.getSeccion() != null
                        && hs.getSeccion().getGrupoSeccion() != null
                        && hs.getSeccion().getGrupoSeccion().getCurso() != null
                        && hs.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio() != null
                        && hs.getSeccion().getGrupoSeccion().getCurso().getModalidadEstudio().getCodigoEnum() == ModalidadEstudioEnum.PRE)
                .collect(Collectors.toList());

        List<Seccion> seccionesLab = horariosSecciones.stream()
                .map(hs -> hs.getSeccion())
                .distinct()
                .collect(Collectors.toList());

        List<DocenteSeccion> docentesSeccionesByCiclo = docenteSeccionDAO.allByCiclo(cicloAcademico, EstadoEnum.ACT);

        List<Long> idsSeccionesLab = seccionesLab.stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());

        docentesSeccionesByCiclo = docentesSeccionesByCiclo.stream()
                .filter(ds -> ds.getSeccion() != null && idsSeccionesLab.contains(ds.getSeccion().getId()))
                .filter(x -> x.esDocentePrincipal())
                .collect(Collectors.toList());

        Map<Long, List<DocenteSeccion>> mapDocenteSeccionBySeccion = TypesUtil.convertListToMapList("seccion.id", docentesSeccionesByCiclo);

        EventoAcademicoEnum eventoEnum = cicloAcademico.isTipoRegular() ? EventoAcademicoEnum.CLASES_PRE : EventoAcademicoEnum.CLASES_VER;
        EventoCicloAcademico eventoDictado = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, eventoEnum);

        List<HorarioAula> horariosAulasReservas = horarioAulaDAO.allByFechas(
                        eventoDictado.getFechaInicio(),
                        eventoDictado.getFechaFin());

        horariosAulasReservas = horariosAulasReservas.stream()
                .filter(ha -> ha.getReservaAula() != null)
                .filter(ha -> ha.getAula() != null)
                .filter(ha -> ha.getAula().getTipoAula() != null && "LAB".equals(ha.getAula().getTipoAula().getCodigo()))
                .filter(ha -> ha.getAula().getOficinaSupervisora() != null && !ha.getAula().getOficinaSupervisora().isOficinaOera())
                .collect(Collectors.toList());

        horariosAulasReservas.removeIf(x -> x.getReservaAula() != null && Boolean.FALSE.equals(x.getReservaAula().getVisibleHorario()));

        for (HorarioSeccion horarioSecc : horariosSecciones) {
            Seccion seccion = horarioSecc.getSeccion();
            if (seccion == null) {
                continue;
            }
            List<DocenteSeccion> docentesSecciones = TypesUtil.getListNotNull(mapDocenteSeccionBySeccion.get(seccion.getId()));
            seccion.setDocenteSeccion(docentesSecciones);
        }

        for (HorarioSeccion hs : horariosSecciones) {
            HorarioAula ha = new HorarioAula(hs.getSeccion(), hs.getDia(), hs.getHora(), hs.getAula());
            ha.setFechaInicio(hs.getFechaInicio());
            ha.setFechaFin(hs.getFechaFin());
            horariosAulasReservas.add(ha);
        }

        return horariosAulasReservas;
    }

    @Override
    public List<Aula> allAulas(CicloAcademico cicloAcademico) {
        return aulaDAO.allByOficinaSupervisora(OficinaEnum.OBUAE, EstadoEnum.ACT);
    }

    @Override
    public List<ResponsableAula> allResponsablesAulas(EstadoEnum... estado) {
        return responsableAulaDAO.allByEstado(EstadoEnum.ACT);
    }

    @Override
    public List<ResponsableAulaAsignacion> allResponsablesAulasAsignadas(EstadoEnum... estado) {
        return responsableAulaAsignacionDAO.allByEstado(EstadoEnum.ACT);
    }

    private boolean filterByRol(DataSessionPivot ds) {
        List<Rol> rolesActivo = ds.getRoles();
        for (Rol rol : rolesActivo) {
            if (Arrays.asList(IOREA, OREA).contains(rol.getCodigoEnum())) {
                return Boolean.FALSE;
            }
        }
        for (Rol rol : rolesActivo) {
            if (Arrays.asList(INF_OBUAE, RESDEP, RESCULT).contains(rol.getCodigoEnum())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Oficina findOficina(OficinaEnum oficinaEnum, Rol role, Usuario usuario) {
        UsuarioRol rol = usuarioRolDAO.findByOficinaRolUser(oficinaEnum, role, usuario);
        if (rol == null) {
            rol = new UsuarioRol();
        }
        return rol.getOficina();
    }

    private List<Oficina> getOficinas(DataSessionPivot ds) {
        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(ds.getPersona());
        return colaboradores.stream()
                .map(col -> col.getOficina().getOficinaPrincipal())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void agregarZoom(Aula aula, DataSessionPivot ds) {
        aulaDAO.updateColumns(aula, "usuarioZoom", "passZoom");
    }

    private String toString(List<Oficina> oficinas) {
        if (oficinas == null) {
            return null;
        }
        return JaneHelper.from(oficinas)
                .only("id,codigo")
                .array().toString();
    }

    @Override
    public List<Aula> allAulasActivas(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        List<Oficina> oficinas = getOficinas(ds);
        Boolean filterObu = this.filterByRol(ds);

        List<String> roles = ds.getRoles().stream().map(b -> b.getCodigo()).collect(Collectors.toList());
        log.info("[allAulasActivas] roles={}", roles);
        log.info("[allAulasActivas] oficinas={}", oficinas.size());

        List<Aula> aulas = new ArrayList<>();

        if (roles.contains(OPER_PROGH_OERA.name())) {
            List<Oficina> oficiAulasLab = oficinaDAO.allByLaboratorios(TIPO_AULA_LABORATORIO);
            oficinas.addAll(oficiAulasLab);

            List<Aula> pabellones = aulaDAO.allByListOficinaSupervisora(oficiAulasLab);
            log.info("[allAulasActivas] OPER_PROGH_OERA - pabellones encontrados: {}", pabellones.size());

            aulas = aulaDAO.allByAulasSuperiores(pabellones);
            log.info("[allAulasActivas] OPER_PROGH_OERA - aulas encontradas: {}", aulas.size());

        } else if (roles.contains(SOPORTE_TECNICO_DERA)) {
            aulas = aulaDAO.allByOficinaSupervisora(OficinaEnum.OERA, EstadoEnum.ACT);
            log.info("[allAulasActivas] SOPORTE_TECNICO_DERA - aulas encontradas: {}", aulas.size());

        } else if (!oficinas.isEmpty()) {
            List<Aula> pabellones = aulaDAO.allByListOficinaSupervisora(oficinas);
            log.info("[allAulasActivas] Por oficinas - pabellones encontrados: {}", pabellones.size());

            aulas = aulaDAO.allByAulasSuperiores(pabellones);
            log.info("[allAulasActivas] Por oficinas - aulas encontradas: {}", aulas.size());
        } else {
            aulas = aulaDAO.allByOficinaSupervisora(OficinaEnum.OERA, EstadoEnum.ACT);
            log.info("[allAulasActivas] Por defecto OERA - aulas encontradas: {}", aulas.size());
        }

        log.info("[allAulasActivas] Total aulas antes de filtros: {}", aulas.size());

        List<Aula> aulasActivas = aulas.stream()
                .filter(a -> a.getEstadoEnum() != null && a.getEstadoEnum() == EstadoEnum.ACT)
                .collect(Collectors.toList());
        log.info("[allAulasActivas] Aulas con estado ACT: {}", aulasActivas.size());

        aulas = aulasActivas.stream()
                .filter(a -> a.getTipoAmbiente() != null && !"EDI".equals(a.getTipoAmbiente()))
                .collect(Collectors.toList());

        log.info("[allAulasActivas] Total aulas despues de filtros: {}", aulas.size());

        List<HorarioAula> horariosAulas = horarioAulaDAO.allByCicloAndTipoHorario(
                cicloAcademico, aulas, TipoHorarioAulaEnum.DICT);

        log.info("[allAulasActivas] Total horarios encontrados: {}", horariosAulas.size());

        Map<Long, List<HorarioAula>> mapHorariosAulas = TypesUtil.convertListToMapList("aula.id", horariosAulas);

        for (Aula aula : aulas) {
            List<HorarioAula> horariosAula = mapHorariosAulas.get(aula.getId());
            if (horariosAula == null) {
                horariosAula = new ArrayList<>();
            }
            aula.setHorariosAula(horariosAula);
        }

        log.info("[allAulasActivas] Retornando {} aulas con horarios", aulas.size());

        return aulas;
    }

}
