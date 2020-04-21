package pe.edu.lamolina.amauta.controller.academico.cuotadpto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CuotaGpoHorasDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasDAO;

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

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    GpoSeccionService gpoSeccionService;

    @Override
    public List<GrupoHoras> allGrupos() {
        return grupoHorasDAO.allRegulares();
    }

    @Override
    public List<CuotasGrupoHoras> allCuotasGpoHoras(DynatableFilter filter, GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {

        List<CuotasGrupoHoras> cuotas = cuotaGpoHorasDAO.allByDynatableGpoHoras(filter, grupoHoras, cicloAcademico);

        List<AnexoCuotaUtilizadaBean> cuotasAnexosUtilizadas = cuotaGpoHorasDAO.allCuotasAnexosByLetraCiclo(grupoHoras, cicloAcademico);
        List<AnexoCuotaUtilizadaBean> gposAnexosUtilizados = cuotaGpoHorasDAO.allGposAnexosByLetraCiclo(grupoHoras, cicloAcademico);
        List<GrupoSeccion> allGrupoSeccion = grupoSeccionDAO.allActivosByLetraAndCiclo(grupoHoras, cicloAcademico);

        Map<String, AnexoCuotaUtilizadaBean> mapAnexoUtilizada = TypesUtil.convertListToMap("idAnexo", cuotasAnexosUtilizadas);
        Map<String, List<AnexoCuotaUtilizadaBean>> mapAnexoGrupos = TypesUtil.convertListToMapList("idAnexo", gposAnexosUtilizados);

        for (CuotasGrupoHoras cuota : cuotas) {

            AnexoCuotaUtilizadaBean anexoUtilizadaFound = mapAnexoUtilizada.get(cuota.getAnexoBoletin().getId());
            List<AnexoCuotaUtilizadaBean> cantidadGruposFound = TypesUtil.getListNotNull(mapAnexoGrupos.get(cuota.getAnexoBoletin().getId()));

            cuota.setGruposUtilizadosTeoria(anexoUtilizadaFound != null ? anexoUtilizadaFound.getCantidadTeoria() : 0L);
            cuota.setGruposUtilizadosPractica(anexoUtilizadaFound != null ? anexoUtilizadaFound.getCantidadPractica() : 0L);

            String strCantGposTeoria = "";
            String strCantGposPractica = "";

            for (AnexoCuotaUtilizadaBean anexo : cantidadGruposFound) {
                if (anexo.getCantidadTeoria() > 0) {
                    strCantGposTeoria += strCantGposTeoria.equals("") ? "" : ", ";
                    strCantGposTeoria += anexo.getGrupo() + "(" + anexo.getCantidadTeoria() + ")";
                }
                if (anexo.getCantidadPractica() > 0) {
                    strCantGposPractica += strCantGposPractica.equals("") ? "" : ", ";
                    strCantGposPractica += anexo.getGrupo() + "(" + anexo.getCantidadPractica() + ")";
                }
            }
            cuota.setDetalleGruposTeoria(strCantGposTeoria);
            cuota.setDetalleGruposPractica(strCantGposPractica);
        }

        return cuotas;
    }

    @Override
    public GrupoHoras findGrupo(GrupoHoras grupoHoras) {
        return grupoHorasDAO.find(grupoHoras.getId());
    }

    @Override
    public String grupos(CuotasGrupoHoras cuotasGrupoHoras, String tipoSeccion) {
        cuotasGrupoHoras = cuotaGpoHorasDAO.find(cuotasGrupoHoras.getId());
        DynatableFilter filterGpoSeccion = createFilterGpoSeccion(cuotasGrupoHoras.getAnexoBoletin(), cuotasGrupoHoras.getGrupoHoras().getLetra(), tipoSeccion);
        List<GrupoSeccion> gpoSecciones = gpoSeccionService.allCleanByDynatable(filterGpoSeccion, cuotasGrupoHoras.getCicloAcademico());
        String ids = "";
        for (GrupoSeccion gpoSecc : gpoSecciones) {
            ids += ids.equals("") ? "" : ",";
            ids += gpoSecc.getId();
        }
        return ids;
    }

    private DynatableFilter createFilterGpoSeccion(AnexoBoletin anexo, String letra, String tipoSeccion) {
        DynatableFilter filter = new DynatableFilter();
        filter.setPage(1);
        filter.setOffset(0);
        filter.setPerPage(1000000);

        Map<String, Object> queries = new HashMap();
        queries.put("letra", letra);
        queries.put("tipoSeccion", tipoSeccion);
        queries.put("anexo", anexo.getId());
        queries.put("order-id", anexo.getId());

        filter.setQueries(queries);

        return filter;
    }

}
