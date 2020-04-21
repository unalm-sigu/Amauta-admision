package pe.edu.lamolina.amauta.controller.posgrado.cronograma;

import java.time.LocalDate;
import java.time.ZoneId;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.posgrado.CronogramaCuota;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.posgrado.CronogramaCuotaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CronogramaPosgradoServiceImp implements CronogramaPosgradoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CronogramaCuotaDAO cronogramaCuotaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Override
    public List<CronogramaCuota> allByCiclo(CicloAcademico ciclo) {
        return cronogramaCuotaDAO.allByCiclo(ciclo);
    }

    @Override
    @Transactional
    public void generar(CronogramaCuota cronograma, DataSessionPivot ds) {

        if (cronograma.getCicloAcademico().getId() == null) {
            throw new PhobosException(GlobalMessages.FORBIDEN);
        }

        CicloAcademico cicloSession = ds.getCicloAcademico();

        if (cronograma.getCicloAcademico().getId() != cicloSession.getId().longValue()) {
            throw new PhobosException(GlobalMessages.FORBIDEN);
        }

        CicloAcademico ciclo = cicloAcademicoDAO.find(cronograma.getCicloAcademico().getId());

        List<CronogramaCuota> cuotasAnteriores = cronogramaCuotaDAO.allByCiclo(ciclo);

        if (!cuotasAnteriores.isEmpty()) {
            throw new PhobosException("El presente ciclo acádemico ya contiene datos");
        }

        Date fecha = cronograma.getFechaEmision();
        
        if (fecha == null) {
            fecha = new Date();
        }

        LocalDate fechaLocal = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Integer numero = cronograma.getNumeroCuota();
        if (numero == null) {
            numero = 10;
        }

        for (int i = 0; i < numero; i++) {

            CronogramaCuota cronogramaNew = new CronogramaCuota();
            cronogramaNew.setFechaRegistro(new Date());
            cronogramaNew.setUserRegistro(ds.getUsuario());
            cronogramaNew.setNumeroCuota((i+1));

            LocalDate fechaEmisionLocalDate = fechaLocal.plusMonths((i));

            LocalDate fechaEmisionLocal = fechaEmisionLocalDate.minusDays(fechaEmisionLocalDate.getDayOfMonth()).plusDays(25);
            Date fechaEmision = Date.from(fechaEmisionLocal.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            cronogramaNew.setFechaEmision(fechaEmision);

            LocalDate fechaPagoLocal = fechaEmisionLocalDate.with(lastDayOfMonth());
            Date fechaPago = Date.from(fechaPagoLocal.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            
            cronogramaNew.setFechaEmision(fechaEmision);
            cronogramaNew.setFechaPago(fechaPago);

            cronogramaNew.setCicloAcademico(ciclo);
            cronogramaCuotaDAO.save(cronogramaNew);
        }
    }

    @Override
    @Transactional
    public void deleteAll(CicloAcademico ciclo, DataSessionPivot ds) {

        if (ciclo.getId() == null) {
            throw new PhobosException(GlobalMessages.FORBIDEN);
        }

        CicloAcademico cicloSession = ds.getCicloAcademico();

        if (ciclo.getId() != cicloSession.getId().longValue()) {
            throw new PhobosException(GlobalMessages.FORBIDEN);
        }

        cronogramaCuotaDAO.deleteAllByCiclo(ciclo);
    }

    @Override
    @Transactional
    public void update(CronogramaCuota cronogramaForm, DataSessionPivot ds) {

        CronogramaCuota cronograma = cronogramaCuotaDAO.find(cronogramaForm);

        if (cronograma.getCicloAcademico().getId() == null) {
            throw new PhobosException(GlobalMessages.FORBIDEN);
        }

        CicloAcademico cicloSession = ds.getCicloAcademico();

        if (cronograma.getCicloAcademico().getId() != cicloSession.getId().longValue()) {
            throw new PhobosException(GlobalMessages.FORBIDEN);
        }

        cronograma.setFechaEmision(cronogramaForm.getFechaEmision());
        cronograma.setFechaPago(cronogramaForm.getFechaPago());

        cronogramaCuotaDAO.update(cronograma);
    }

}
