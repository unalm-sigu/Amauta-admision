package pe.edu.lamolina.pivot.controller.general.aula;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoAmbienteEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Sede;
import pe.edu.lamolina.model.general.TipoAula;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.almacen.ResumenInventarioDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.SedeDAO;
import pe.edu.lamolina.pivot.dao.general.TipoAulaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AulaServiceImp implements AulaService {

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    TipoAulaDAO tipoAulaDAO;

    @Autowired
    SedeDAO sedeDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    ResumenInventarioDAO resumenInventarioDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    TipoCarpetaDAO tipoCarpetaDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    HoraDAO horaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Aula> allByDynatable(DynatableFilter filter) {
        List<Aula> aulas = aulaDAO.allByDynatable(filter);
        List<Aula> aulasHijas = aulaDAO.allByAulasSuperiores(aulas);
        List<ResumenInventario> resumenAulas = resumenInventarioDAO.allVisiblesByAulas(aulas);
        Map<Long, List<ResumenInventario>> resumenAulasMap = TypesUtil.convertListToMapList("almacen.aula.id", resumenAulas);
        Map<Long, List<Aula>> mapAulas = TypesUtil.convertListToMapList("aulaSuperior.id", aulasHijas);
        for (Aula aula : aulas) {
            List<Aula> hijas = mapAulas.get(aula.getId());
            hijas = hijas == null ? new ArrayList() : hijas;
            aula.setAulasContenido(hijas);
            List<ResumenInventario> inventarios = resumenAulasMap.get(aula.getId());
            aula.setInventario(inventarios);
        }
        return aulas;
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
    public void save(Aula aula, Usuario usuario) {
        aula.setCodigo(aula.getCodigo().toUpperCase().replaceAll("\\s+", ""));
        Aula aulaTmp = aulaDAO.findByCode(aula.getCodigo());
        Assert.isNull(aulaTmp, "Este código ya fue asignado a otro ambiente");
//        TipoCarpeta tipocarpeta = tipoCarpetaDAO.find(aula.getTipoCarpeta().getId());

        if (aula.getTipoAmbienteEnum() == TipoAmbienteEnum.EDI) {
            aula.setAforo(0);
        }

        ObjectUtil.eliminarAttrSinId(aula, "aulaSuperior");
        ObjectUtil.eliminarAttrSinId(aula, "sede");
        ObjectUtil.eliminarAttrSinId(aula, "tipoAula");
        ObjectUtil.eliminarAttrSinId(aula, "oficinaSupervisora");

        Aula aulaSup = aula.getAulaSuperior();
        if (aulaSup != null) {
            aulaSup = aulaDAO.find(aulaSup.getId());
            Assert.isTrue(aulaSup.getTipoAmbienteEnum() == TipoAmbienteEnum.EDI, "Un ambiente solo debería pertenecer a otro del tipo Edificio");
        }

        revisarNombre(aula);
        aula.setEstadoEnum(EstadoEnum.CRE);
        aula.setUserRegistro(usuario);
//        aula.setTipoCarpeta(tipocarpeta);
        aula.setFechaRegistro(new Date());
        aulaDAO.save(aula);
    }

    @Override
    @Transactional
    public void update(Aula aula, Usuario usuario) {
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

        revisarNombre(aula);
        aulaBD.setAulaSuperior(aula.getAulaSuperior());
        aulaBD.setSede(aula.getSede());
        aulaBD.setTipoAula(aula.getTipoAula());
        aulaBD.setTipoCarpeta(aula.getTipoCarpeta());
        aulaBD.setOficinaSupervisora(aula.getOficinaSupervisora());

        aulaBD.setAforo(aula.getAforo());
        aulaBD.setCapacidadAula(aula.getCapacidadAula());
        aulaBD.setCodigo(aula.getCodigo());
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
    public Aula findAulaFull(Aula aula) {

        aula = aulaDAO.find(aula.getId());
        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAulaFecha(aula);
        this.completarDocentes(horariosAulas);
        aula.setHorariosAula(horariosAulas);
        return aula;
    }

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

}
