package pe.edu.lamolina.pivot.controller.academico.cuotadpto;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;

@Service
@Transactional(readOnly = true)
public class CuotaDptoServiceImp implements CuotaDptoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotaGpoHorasDAO cuotaGpoHorasDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Override
    public List<GrupoHoras> allGrupos() {
        return grupoHorasDAO.allRegulares();
    }

    @Override
    public List<CuotasGrupoHoras> allCuotasGpoHoras(DynatableFilter filter, GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {

        List<CuotasGrupoHoras> cuotas = cuotaGpoHorasDAO.allByDynatableGpoHoras(filter, grupoHoras, cicloAcademico);

        List<AnexoCuotaUtilizadaBean> anexoCuotaUtilizadaBean = cuotaGpoHorasDAO.allAnexoCuotasByLetraCiclo(grupoHoras, cicloAcademico);
        List<AnexoCuotaUtilizadaBean> anexoCuotaGrupoBean = cuotaGpoHorasDAO.allAnexosCuotasByLetraCiclo(grupoHoras, cicloAcademico);

        Map<String, AnexoCuotaUtilizadaBean> mapAnexoUtilizada = TypesUtil.convertListToMap("idAnexo", anexoCuotaUtilizadaBean);
        Map<String, List<AnexoCuotaUtilizadaBean>> mapAnexoGrupos = TypesUtil.convertListToMapList("idAnexo", anexoCuotaGrupoBean);

        for (CuotasGrupoHoras cuota : cuotas) {

            AnexoCuotaUtilizadaBean anexoUtilizadaFound = mapAnexoUtilizada.get(cuota.getAnexoBoletin().getId());
            List<AnexoCuotaUtilizadaBean> cantidadGruposFound = TypesUtil.getListNotNull(mapAnexoGrupos.get(cuota.getAnexoBoletin().getId()));

            cuota.setHorasUtilizadas(anexoUtilizadaFound != null ? anexoUtilizadaFound.getCantidad() : 0L);

            String strCantGpos = "";
            for (AnexoCuotaUtilizadaBean anexo : cantidadGruposFound) {
                strCantGpos += strCantGpos.equals("") ? "" : ", ";
                strCantGpos += anexo.getGrupo() + "(" + anexo.getCantidad() + ")";
            }
            cuota.setDetalleGrupos(strCantGpos);
        }

        return cuotas;
    }

    @Override
    public GrupoHoras findGrupo(GrupoHoras grupoHoras) {
        return grupoHorasDAO.find(grupoHoras.getId());
    }

}
