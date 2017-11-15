package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.Hora;

@Service
@Transactional(readOnly = true)
public class GrupoHorasServiceImp implements GrupoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Override
    public GrupoHoras findGrupoHoras(GrupoHoras grupoHoras) {
        return grupoHorasDAO.find(grupoHoras.getId());
    }

    @Override
    public GrupoHoras findGrupoHoras(Long grupoHoras) {
        return grupoHorasDAO.find(grupoHoras);
    }

    @Override
    @Transactional
    public void delete(GrupoHoras grupoHoras) {
        grupoHorasDAO.delete(grupoHoras);
    }

    @Override
    @Transactional
    public void save(GrupoHoras grupoHoras) {
        grupoHoras.setLetra(grupoHoras.getLetra().toUpperCase());
        grupoHoras.setCodigo(grupoHoras.getCodigo().toUpperCase());
        grupoHorasDAO.save(grupoHoras);
    }

    @Override
    @Transactional
    public void update(GrupoHoras grupoHoras) {
        GrupoHoras grupoHorasDb = grupoHorasDAO.find(grupoHoras.getId());
        grupoHorasDb.setLetra(grupoHoras.getLetra().toUpperCase());
        grupoHorasDb.setCodigo(grupoHoras.getCodigo().toUpperCase());
        grupoHorasDb.setColor(grupoHoras.getColor());
        grupoHorasDb.setTipoGrupoHoras(grupoHoras.getTipoGrupoHoras());
        grupoHorasDb.setTipoCiclo(grupoHoras.getTipoCiclo());
        grupoHorasDb.setTipoSeccion(grupoHoras.getTipoSeccion());
        grupoHorasDAO.update(grupoHorasDb);
    }

    @Override
    public GrupoHoras findGrupoHorasByCode(String codigo) {
        return grupoHorasDAO.findGrupoHorasByCode(codigo);
    }

    @Override
    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter, Long idTipoGrupo) {
        return grupoHorasDAO.allGrupoHoras(filter, idTipoGrupo);
    }

    @Override
    public List<Hora> allHora() {
        return horaDAO.allHora();
    }

    @Override
    public List<Dia> allDia() {
        return diaDAO.allDia();
    }

    @Override
    @Transactional
    public void saveDiaHoraGrupo(DiaHoraGrupo diaHoraGrupo) {
        DiaHoraGrupo diaHoraGrupoDb = diaHoraGrupoDAO.findByDiaHoraCiclo(diaHoraGrupo);
        if (diaHoraGrupoDb != null) {
            diaHoraGrupoDb.setGrupoHorario(diaHoraGrupo.getGrupoHorario());
            diaHoraGrupoDAO.update(diaHoraGrupo);
            return;
        }
        diaHoraGrupoDAO.save(diaHoraGrupo);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allDiaHoraGrupo(grupoHoras, cicloAcademico);
    }

}
