package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import java.util.ArrayList;
import java.util.Date;
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
import pe.edu.lamolina.model.horario.GrupoHoras;
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

            LetraCuotaUtilizadaBean letraUtilizadoFound = mapLetraUtilizados.get(cuota.getGrupoHoras().getLetra());
            LetraCuotaUtilizadaBean letraHorasUtilizadoFound = mapLetraHorasUtilizadas.get(cuota.getGrupoHoras().getLetra());
            List<LetraCuotaUtilizadaBean> cantidadGrupoFound = TypesUtil.getListNotNull(mapCantidadGrupos.get(cuota.getGrupoHoras().getLetra()));

            cuota.setHorasUtilizadas(letraUtilizadoFound != null ? letraUtilizadoFound.getCantidad() : 0L);
            cuota.setGruposUtilizados(letraHorasUtilizadoFound != null ? letraHorasUtilizadoFound.getCantidad() : 0L);

            String strCantGpos = "";

            for (LetraCuotaUtilizadaBean letra : cantidadGrupoFound) {
                strCantGpos += strCantGpos.equals("") ? "" : ", ";
                strCantGpos += letra.getGrupo() + "(" + letra.getCantidad() + ")";
            }
            cuota.setDetalleGrupos(strCantGpos);
        }

        return cuotas;
    }

    @Override
    public List<AnexoBoletin> allAnexos() {
        List<AnexoBoletin> anexos = anexoBoletinDAO.allActivosHijos();
        List<AnexoBoletin> anexosBoletin = new ArrayList();

        for (AnexoBoletin anx : anexos) {
            if (anx.getAnexoSuperior().getId() == 2) {
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
                cuotaForm.setAsignadasSistema(0);
                cuotaForm.setTotalUtilizadas(0);
                cuotaForm.setUserRegistro(ds.getUsuario());
                cuotaForm.setFechaRegistro(today.toDate());
                if (cuotaForm.getCuotas() > 0) {
                    cuotaGpoHorasDAO.save(cuotaForm);
                }

            } else if (cuotaForm.getCuotas() > 0) {
                CuotasGrupoHoras cuotaBD = mapCuotas.get(cuotaForm.getId());
                if (cuotaBD.getCuotas() != cuotaForm.getCuotas().intValue()) {
                    cuotaBD.setCuotas(cuotaForm.getCuotas());
                    cuotaBD.setFechaModificacion(today.toDate());
                    cuotaBD.setUserModificacion(ds.getUsuario());
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
