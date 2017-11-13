package pe.edu.lamolina.pivot.controller.general.aula;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
        return tipoAulaDAO.all();
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<Aula> allAulasSuperioresByName(String nombre) {
        return aulaDAO.allAulasSuperioresByName(this.forLike(nombre));
    }

    @Override
    public List<Sede> allSedesByName(String nombre) {
        return sedeDAO.allSedesByName(this.forLike(nombre));
    }

    @Override
    public List<Oficina> allOficinasByName(String nombre) {
        return oficinaDAO.allOficinasByName(this.forLike(nombre));
    }

    @Override
    @Transactional
    public void save(Aula aula, Usuario usuario) {
        String tipoAmbiente = aula.getTipoAmbiente();
        Aula aulaTmp = aulaDAO.findByCode(aula.getCodigo());
        if (aulaTmp != null) {
            new PhobosException("El código pertenece al aula " + aula.getNombre());
        }

        if (tipoAmbiente.equals(TipoAmbienteEnum.EDI.name())) {
            List<Aula> aulasHijo = aulaDAO.allByAulaSuperior(aula);
            if (!aulasHijo.isEmpty()) {
                Integer aforoTotal = aulaDAO.findAforoByEdificio(aula);
                aula.setAforo(aforoTotal);
            }
        }
        ObjectUtil.eliminarAttrSinId(aula, "aulaSuperior");
        ObjectUtil.eliminarAttrSinId(aula, "sede");
        ObjectUtil.eliminarAttrSinId(aula, "tipoAula");
        ObjectUtil.eliminarAttrSinId(aula, "oficinaSupervisora");
        aula.setEstado(EstadoEnum.CRE.name());

        aulaDAO.save(aula);
    }

    @Override
    public Aula find(Long id) {
        return aulaDAO.find(id);
    }

    @Override
    @Transactional
    public void cambioEstado(Aula aula) {
        Aula aulaBD = aulaDAO.find(aula.getId());
        String estado = aulaBD.getEstado();
        
        if (estado.equals(EstadoEnum.ACT.name())) {
            aulaBD.setEstado(EstadoEnum.INA.name());
            aulaBD.setMotivoAnulacion(aula.getMotivoAnulacion());
            aulaBD.setFechaAnulacion(new Date());
        } else {
            aulaBD.setEstado(EstadoEnum.ACT.name());
        }
        aulaDAO.update(aulaBD);
    }

}
