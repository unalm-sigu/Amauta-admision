package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
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

        return cuotaGpoHorasDAO.allByDynatable(filter, anexoBoletin, cicloAcademico);
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
        for (CuotasGrupoHoras cuota : cuotas) {
            cuota.setCicloAcademico(ciclo);
            cuota.setUserRegistro(ds.getUsuario());
            cuota.setAsignadasSistema(0);
            cuota.setFechaRegistro(new Date());
            cuota.setTotalUtilizadas(0);
            if (cuota.getId() == null) {
                if (cuota.getCuotas() > 0) {
                    cuotaGpoHorasDAO.save(cuota);
                }
            } else if (cuota.getCuotas() > 0) {
                cuotaGpoHorasDAO.update(cuota);
            } else {
                cuotaGpoHorasDAO.delete(cuota);
            }

        }

    }

    @Override
    public List<CuotasGrupoHoras> allCuotasByAnexo(AnexoBoletin anexoBoletin, CicloAcademico cicloAcademico) {
        List<CuotasGrupoHoras> cuotasGrupoHoras = cuotaGpoHorasDAO.allCuotasByAnexo(anexoBoletin, cicloAcademico);
        return cuotasGrupoHoras;
    }

}
