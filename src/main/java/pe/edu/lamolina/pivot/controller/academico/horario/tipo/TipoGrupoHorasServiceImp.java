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
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;
import pe.edu.lamolina.pivot.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoHorasEnum;
import pe.edu.lamolina.pivot.zelper.enums.OficinaEstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoTipoGrupoHorasEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoGrupoHorasEnum;

@Service
@Transactional(readOnly = true)
public class TipoGrupoHorasServiceImp implements TipoGrupoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

    @Override
    public List<TipoGrupoHoras> allTipoGrupoHoras(DynatableFilter filter) {
        return tipoGrupoHorasDAO.allTipoGrupoHoras(filter);
    }

    @Override
    public void estado(TipoGrupoHoras tipoGrupo) {
        TipoGrupoHoras tipoGrupoDb = tipoGrupoHorasDAO.find(tipoGrupo.getId());
        if (OficinaEstadoEnum.INA.name().equalsIgnoreCase(tipoGrupoDb.getEstado())) {
            tipoGrupoDb.setEstado(EstadoTipoGrupoHorasEnum.ACT.name());
        } else {
            tipoGrupoDb.setEstado(EstadoTipoGrupoHorasEnum.INA.name());
        }
        tipoGrupoHorasDAO.update(tipoGrupoDb);
    }

    @Override
    @Transactional
    public void delete(TipoGrupoHoras tipoGrupo) {
        tipoGrupoHorasDAO.delete(tipoGrupo);
    }

    @Override
    @Transactional
    public void update(TipoGrupoHoras tipoGrupo) {
        TipoGrupoHoras tipoGrupoDb = tipoGrupoHorasDAO.find(tipoGrupo.getId());
        tipoGrupoDb.setTipoCiclo(tipoGrupo.getTipoCiclo());
        tipoGrupoHorasDAO.update(tipoGrupoDb);
    }

    @Override
    @Transactional
    public void save(TipoGrupoHoras tipoGrupo) {

        List<TipoGrupoHoras> tipoGrupoHoras = tipoGrupoHorasDAO.all();

        tipoGrupo.setTipo(TipoGrupoHorasEnum.REGULAR.name());
        tipoGrupo.setEstado(EstadoTipoGrupoHorasEnum.CRE.name());
        tipoGrupo.setEstadoGrupos(EstadoGrupoHorasEnum.INCOMP.name());

        if (tipoGrupoHoras.isEmpty()) {
            tipoGrupo.setCodigo("HOR-001");
            tipoGrupoHorasDAO.save(tipoGrupo);
            return;
        }

        Map<Long, Long> mapCodigos = new LinkedHashMap<>();

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

        tipoGrupo.setCodigo("HOR-"+codigo);
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
