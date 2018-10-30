package pe.edu.lamolina.pivot.controller.academico.horario.tipo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;

@Service
@Transactional(readOnly = true)
public class TipoGrupoHorasServiceImp implements TipoGrupoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

    @Override
    public List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter) {
        return tipoGrupoHorasDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void changeEstado(TipoGrupoHoras tipoGrupo) {
        TipoGrupoHoras tipoGrupoBD = tipoGrupoHorasDAO.find(tipoGrupo.getId());
        if (EstadoEnum.INA == tipoGrupoBD.getEstadoEnum()) {
            tipoGrupoBD.setEstadoEnum(EstadoEnum.ACT);
        } else if (EstadoEnum.CRE == tipoGrupoBD.getEstadoEnum()) {
            tipoGrupoBD.setEstadoEnum(EstadoEnum.ACT);
        } else {
            tipoGrupoBD.setEstadoEnum(EstadoEnum.INA);
        }
        tipoGrupoHorasDAO.update(tipoGrupoBD);

    }

    @Override
    @Transactional
    public void deleteTipoGpo(TipoGrupoHoras tipoGrupo) {
        tipoGrupoHorasDAO.delete(tipoGrupo);
    }

    @Override
    @Transactional
    public void updateTipoGpo(TipoGrupoHoras tipoGrupo) {
        TipoGrupoHoras tipoGrupoBD = tipoGrupoHorasDAO.find(tipoGrupo.getId());
        tipoGrupoBD.setTipoCiclo(tipoGrupo.getTipoCiclo());
        tipoGrupoBD.setDescripcion(tipoGrupo.getDescripcion());
        tipoGrupoHorasDAO.update(tipoGrupoBD);
    }

    @Override
    @Transactional
    public void saveTipogpo(TipoGrupoHoras tipoGrupo) {
        List<TipoGrupoHoras> tipoGrupoHoras = tipoGrupoHorasDAO.all();

        tipoGrupo.setTipo(TipoGrupoHorasEnum.REGULAR.name());
        tipoGrupo.setEstadoEnum(EstadoEnum.CRE);
        tipoGrupo.setEstadoGruposEnum(EstadoGrupoHorasEnum.INCOMP);

        if (tipoGrupoHoras.isEmpty()) {
            tipoGrupo.setCodigo("HOR-001");
            tipoGrupoHorasDAO.save(tipoGrupo);
            return;
        }

        Map<Long, Long> mapCodigos = new LinkedHashMap();

        for (TipoGrupoHoras tipoGrupoHora : tipoGrupoHoras) {
            String c = tipoGrupoHora.getCodigo().substring(4);
            Long key = new Long(c);
            mapCodigos.put(key, key);
        }
        Long codigo = 1L;
        Long codigoDisp = mapCodigos.get(codigo);
        while (codigoDisp != null) {
            codigo++;
            codigoDisp = mapCodigos.get(codigo);
        }

        tipoGrupo.setCodigo("HOR-" + NumberFormat.codigo(codigo, 3));
        tipoGrupoHorasDAO.save(tipoGrupo);
    }

    @Override
    public TipoGrupoHoras find(TipoGrupoHoras tipoGrupo) {
        return tipoGrupoHorasDAO.find(tipoGrupo.getId());
    }

    @Override
    public TipoGrupoHoras findTipoGrupoHorasByCode(String codigo) {
        return tipoGrupoHorasDAO.findByCode(codigo);
    }

}
