package pe.edu.lamolina.pivot.controller.general.aula;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.SedeDAO;
import pe.edu.lamolina.pivot.dao.general.TipoAulaDAO;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.Sede;
import pe.edu.lamolina.pivot.model.general.TipoAula;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoAmbienteEnum;
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

    @Override
    public List<Aula> allByDynatable(DynatableFilter filter) {
        return aulaDAO.allByDynatable(filter);
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
        aula.setEstado(EstadoEnum.CRE);
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
            aulaBD.setEstado(EstadoEnum.INA);
            aulaBD.setMotivoAnulacion(aula.getMotivoAnulacion());
            aulaBD.setFechaAnulacion(new Date());
            aulaBD.setUserAnulacion(ds.getUsuario());

        } else if (aula.getEstadoEnum() == EstadoEnum.INA) {
            Assert.isFalse(aulaBD.getEstadoEnum() == EstadoEnum.INA, "Este ambiente ya se encuentra desactivado");
            aulaBD.setEstado(EstadoEnum.ACT);
        }
        aulaDAO.update(aulaBD);
    }

}
