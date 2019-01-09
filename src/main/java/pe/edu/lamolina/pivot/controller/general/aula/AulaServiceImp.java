package pe.edu.lamolina.pivot.controller.general.aula;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Sede;
import pe.edu.lamolina.model.general.TipoAula;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.almacen.ResumenInventarioDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.SedeDAO;
import pe.edu.lamolina.pivot.dao.general.TipoAulaDAO;
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
    public void eliminarAula(Aula aula, DataSessionPivot ds) {
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
    public Aula findAulaFull(Long aulaId, CicloAcademico cicloAcademico) {
        Aula aula = aulaDAO.find(aulaId);
        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAula(aula, cicloAcademico);
        this.completarDocentes(horariosAulas);
        aula.setHorariosAula(horariosAulas);
        return aula;
    }

    @Override
    public List<Dia> allDia() {
        return diaDAO.allDia();
    }

    private void completarDocentes(List<HorarioAula> horariosAulas) {
        List<Seccion> secciones = horariosAulas.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allActivosBySeccionesOrderPrincipalLimit(secciones);
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

}
