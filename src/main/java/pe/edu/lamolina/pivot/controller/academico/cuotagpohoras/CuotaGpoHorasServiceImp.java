package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
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
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CuotaGpoHorasServiceImp implements CuotaGpoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotaGpoHorasDAO cuotaGpoHorasDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    GpoSeccionService gpoSeccionService;

    @Override
    public List<CuotasGrupoHoras> allCuotasGpoHoras(DynatableFilter filter, AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {

        List<CuotasGrupoHoras> cuotas = cuotaGpoHorasDAO.allByDynatable(filter, anexoBoletin, cicloAcademico);

        List<LetraCuotaUtilizadaBean> letrasUtilizados = cuotaGpoHorasDAO.allLetrasUtilizadasByAnexoCiclo(anexoBoletin, cicloAcademico);
        List<LetraCuotaUtilizadaBean> horasUtilizadas = cuotaGpoHorasDAO.allHorasUtilizadasByAnexoCiclo(anexoBoletin, cicloAcademico);
        List<LetraCuotaUtilizadaBean> cantidadGrupos = cuotaGpoHorasDAO.allGposUtilizadosByAnexoCiclo(anexoBoletin, cicloAcademico);

        Map<String, LetraCuotaUtilizadaBean> mapLetraUtilizados = TypesUtil.convertListToMap("letra", letrasUtilizados);
        Map<String, LetraCuotaUtilizadaBean> mapLetraHorasUtilizadas = TypesUtil.convertListToMap("letra", horasUtilizadas);
        Map<String, List<LetraCuotaUtilizadaBean>> mapCantidadGrupos = TypesUtil.convertListToMapList("letra", cantidadGrupos);

        for (CuotasGrupoHoras cuota : cuotas) {
            String idsTeoria = getIdsGpoSecciones(cuota, cicloAcademico, "TEO");
            String idsPractica = getIdsGpoSecciones(cuota, cicloAcademico, "PRA");
            cuota.setIdsGposSeccionesTeoria(idsTeoria);
            cuota.setIdsGposSeccionesPractica(idsPractica);

            LetraCuotaUtilizadaBean letraUtilizadoFound = mapLetraUtilizados.get(cuota.getGrupoHoras().getLetra());
            LetraCuotaUtilizadaBean letraHorasUtilizadoFound = mapLetraHorasUtilizadas.get(cuota.getGrupoHoras().getLetra());
            List<LetraCuotaUtilizadaBean> cantidadGrupoFound = TypesUtil.getListNotNull(mapCantidadGrupos.get(cuota.getGrupoHoras().getLetra()));

            cuota.setGruposUtilizadosTeoria(letraUtilizadoFound != null ? letraUtilizadoFound.getCantidadTeoria() : 0L);
            cuota.setGruposUtilizadosPractica(letraUtilizadoFound != null ? letraUtilizadoFound.getCantidadPractica() : 0L);
            cuota.setHorasUtilizadasTeoria(letraHorasUtilizadoFound != null ? letraHorasUtilizadoFound.getCantidadTeoria() : 0L);
            cuota.setHorasUtilizadasPractica(letraHorasUtilizadoFound != null ? letraHorasUtilizadoFound.getCantidadPractica() : 0L);

            String strCantGposTeoria = "";
            String strCantGposPractica = "";

            for (LetraCuotaUtilizadaBean letraGrupo : cantidadGrupoFound) {
                if (letraGrupo.getCantidadTeoria() > 0) {
                    strCantGposTeoria += strCantGposTeoria.equals("") ? "" : ", ";
                    strCantGposTeoria += letraGrupo.getGrupo() + "(" + letraGrupo.getCantidadTeoria() + ")";
                }
                if (letraGrupo.getCantidadPractica() > 0) {
                    strCantGposPractica += strCantGposPractica.equals("") ? "" : ", ";
                    strCantGposPractica += letraGrupo.getGrupo() + "(" + letraGrupo.getCantidadPractica() + ")";
                }
            }
            cuota.setDetalleGruposTeoria(strCantGposTeoria);
            cuota.setDetalleGruposPractica(strCantGposPractica);
        }

        return cuotas;
    }

    private String getIdsGpoSecciones(CuotasGrupoHoras cuota, CicloAcademico cicloAcademico, String tipoSeccion) {
        AnexoBoletin anexo = cuota.getAnexoBoletin();
        String letra = cuota.getGrupoHoras().getLetra();
        DynatableFilter filterGpoSeccion = createFilterGpoSeccion(anexo, letra, tipoSeccion);
        List<GrupoSeccion> gpoSecciones = gpoSeccionService.allCleanByDynatable(filterGpoSeccion, cicloAcademico);

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

    @Override
    public List<AnexoBoletin> allAnexos() {
        List<AnexoBoletin> anexos = anexoBoletinDAO.allActivosHijos();
        List<AnexoBoletin> anexosBoletin = new ArrayList();

        for (AnexoBoletin anx : anexos) {
            if (anx.getAnexoSuperior().getId() == 1) {
                anexosBoletin.add(anx);
            } else if (anx.getAnexoSuperior().getId() == 2) {
                anexosBoletin.add(anx);
            }
        }
        return anexosBoletin;
    }

    @Override
    public List<GrupoHoras> allGrupos() {
        List<GrupoHoras> grupos = grupoHorasDAO.allRegulares();
        return grupos;
    }

    @Override
    @Transactional
    public void save(List<CuotasGrupoHoras> cuotas, CicloAcademico ciclo, DataSessionPivot ds) {
        if (cuotas.isEmpty()) {
            return;
        }

        AnexoBoletin anexo = cuotas.get(0).getAnexoBoletin();
        List<CuotasGrupoHoras> cuotasBD = cuotaGpoHorasDAO.allByAnexoCiclo(anexo, ciclo);
        Map<Long, CuotasGrupoHoras> mapCuotas = TypesUtil.convertListToMap("id", cuotasBD);
        DateTime today = new DateTime();

        for (CuotasGrupoHoras cuotaForm : cuotas) {
            if (cuotaForm.getId() == null) {
                cuotaForm.setCicloAcademico(ciclo);
                cuotaForm.setUtilizadasTeoria(0);
                cuotaForm.setUtilizadasPractica(0);
                cuotaForm.setUserRegistro(ds.getUsuario());
                cuotaForm.setFechaRegistro(today.toDate());
                if (cuotaForm.getCuotasTeoria() > 0 || cuotaForm.getCuotasPractica() > 0) {
                    cuotaGpoHorasDAO.save(cuotaForm);
                }

            } else if (cuotaForm.getCuotasTeoria() > 0 || cuotaForm.getCuotasPractica() > 0) {
                CuotasGrupoHoras cuotaBD = mapCuotas.get(cuotaForm.getId());
                boolean seModifico = false;
                if (cuotaBD.getCuotasTeoria() != cuotaForm.getCuotasTeoria().intValue()) {
                    cuotaBD.setCuotasTeoria(cuotaForm.getCuotasTeoria());
                    cuotaBD.setFechaModificacion(today.toDate());
                    cuotaBD.setUserModificacion(ds.getUsuario());
                    seModifico = true;
                }
                if (cuotaBD.getCuotasPractica() != cuotaForm.getCuotasPractica().intValue()) {
                    cuotaBD.setCuotasPractica(cuotaForm.getCuotasPractica());
                    cuotaBD.setFechaModificacion(today.toDate());
                    cuotaBD.setUserModificacion(ds.getUsuario());
                    seModifico = true;
                }

                if (seModifico) {
                    cuotaGpoHorasDAO.update(cuotaBD);
                }

            } else {
                CuotasGrupoHoras cuotaBD = mapCuotas.get(cuotaForm.getId());
                cuotaGpoHorasDAO.delete(cuotaBD);
            }

        }

    }

    @Override
    public List<CuotasGrupoHoras> allCuotasByAnexo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        List<CuotasGrupoHoras> cuotasGrupoHoras = cuotaGpoHorasDAO.allByAnexoCiclo(anexoBoletin, cicloAcademico);
        return cuotasGrupoHoras;
    }

}
