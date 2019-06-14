package pe.edu.lamolina.pivot.controller.general.tipocarpeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TipoCarpetaServiceImp implements TipoCarpetaService {

    @Autowired
    TipoCarpetaDAO tipoCarpetaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TipoCarpeta> allTipoCarpeta(DataSessionPivot ds) {
        List<TipoCarpeta> result = new ArrayList<>();
        List<TipoCarpeta> tipoCarpetas = tipoCarpetaDAO.allTipoCarpeta();
        List<TipoCarpeta> tipoCarpetasHijas = tipoCarpetaDAO.allByTipoCarpetas(tipoCarpetas);
        Map<Long, List<TipoCarpeta>> tipoCarpetasHijasMap = TypesUtil.convertListToMapList("tipoCarpetaSuperior.id", tipoCarpetasHijas);
        for (TipoCarpeta tipoCarpeta : tipoCarpetas) {
            tipoCarpeta.setTipoCarpetaPadre(true);
            result.add(tipoCarpeta);
            if (tipoCarpetasHijasMap.get(tipoCarpeta.getId()) != null) {
                for (TipoCarpeta tipoCarpeta1 : tipoCarpetasHijasMap.get(tipoCarpeta.getId())) {
                    result.add(tipoCarpeta1);
                }
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void save(TipoCarpeta tipoCarpeta, DataSessionPivot ds) {
        tipoCarpetaDAO.save(tipoCarpeta);
    }

    @Override
    @Transactional
    public void editar(TipoCarpeta tipoCarpeta, DataSessionPivot ds) {
        tipoCarpetaDAO.update(tipoCarpeta);
    }

}
