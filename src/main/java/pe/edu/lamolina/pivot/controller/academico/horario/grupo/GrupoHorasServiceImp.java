package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;

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

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

    @Override
    public GrupoHoras findGrupoHoras(GrupoHoras grupoHoras) {
        return grupoHorasDAO.find(grupoHoras);
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
        grupoHorasDb.setConHorario(grupoHoras.getConHorario());
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
        return horaDAO.all();
    }

    @Override
    public List<Dia> allDia() {
        return diaDAO.allDia();
    }

    @Override
    @Transactional
    public void saveDiaHoraGrupo(DiaHoraGrupo diaHoraGrupo) {

        GrupoHoras grupoHoras = grupoHorasDAO.find(diaHoraGrupo.getGrupoHorario());
        TipoGrupoHoras tipoGrupoHoras = grupoHoras.getTipoGrupoHoras();

        if (TipoGrupoHorasEnum.ESPECIAL.name().equalsIgnoreCase(tipoGrupoHoras.getTipo())) {
            diaHoraGrupoDAO.save(diaHoraGrupo);
            return;
        }

        if (TipoGrupoHorasEnum.ZETA.name().equalsIgnoreCase(tipoGrupoHoras.getTipo())) {
            StringBuilder sb = new StringBuilder();
            sb.append("No puede agregar horarios a un grupo ZETA");
            throw new PhobosException(sb.toString());
        }
        diaHoraGrupo.setGrupoHorario(grupoHoras);
        DiaHoraGrupo diaHoraGrupoDb = diaHoraGrupoDAO.findByDiaHoraCiclo(diaHoraGrupo);
        if (diaHoraGrupoDb != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("El grupo ");
            sb.append(diaHoraGrupoDb.getDia().getNombre());
            sb.append(" ");
            sb.append(diaHoraGrupoDb.getHora().getDescripcion());
            sb.append(" ya esta ocupado por: ");
            sb.append(diaHoraGrupoDb.getGrupoHorario().getCodigo());
            sb.append(".");
            throw new PhobosException(sb.toString());
        } else {
            diaHoraGrupoDAO.save(diaHoraGrupo);
        }
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allByGrupoCiclo(grupoHoras, cicloAcademico);
    }

    @Override
    @Transactional
    public void desasignarHora(DiaHoraGrupo diaHoraGrupo) {
        logger.debug("DELETE DIAHOARGRUPO {}", diaHoraGrupo.getId());
        diaHoraGrupoDAO.delete(diaHoraGrupo);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allByGruposCiclo(grupos, cicloAcademico);
    }

    @Override
    public TipoGrupoHoras findTipoGrupoHoras(Long idTipoGrupo) {
        return tipoGrupoHorasDAO.find(idTipoGrupo);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allByTipoGpoCiclo(tipoGrupoHoras, cicloAcademico);
    }

    @Override
    @Transactional
    public void gencolor() {

        List<GrupoHoras> grupos = grupoHorasDAO.all();
        for (GrupoHoras grupo : grupos) {
            Random random = new Random();
            int nextInt = random.nextInt(256 * 256 * 256);
            String colorCode = String.format("#%06x", nextInt);
            grupo.setColor(colorCode);
            grupoHorasDAO.update(grupo);
        }
    }

    @Override
    public TipoGrupoHoras findTipoGpoRegular() {
        List<TipoGrupoHoras> tipos = tipoGrupoHorasDAO.all();
        for (TipoGrupoHoras tipo : tipos) {
            if (tipo.getTipoCicloEnum() == TipoCicloEnum.REG && tipo.getEstadoEnum() == EstadoEnum.ACT && tipo.getTipoEnum() == TipoGrupoHorasEnum.REGULAR) {
                return tipo;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void clonar(CicloAcademico cicloOrigen, CicloAcademico cicloDestino) {
        List<DiaHoraGrupo> diaHoraGrupos = diaHoraGrupoDAO.allByCiclo(cicloOrigen);
        Assert.isTrue(cicloOrigen.getId() != cicloDestino.getId(), "No puede clonar del mismo ciclo");
        Assert.isTrue(cicloOrigen.getTipo() == cicloDestino.getTipo(), "No puede clonar de un tipo de ciclo distinto");
      
        for (DiaHoraGrupo diaHoraGrupo : diaHoraGrupos) {
            DiaHoraGrupo horaGrupo = new DiaHoraGrupo();
            horaGrupo.setDia(diaHoraGrupo.getDia());
            horaGrupo.setGrupoHorario(diaHoraGrupo.getGrupoHorario());
            horaGrupo.setHora(diaHoraGrupo.getHora());
            horaGrupo.setCicloAcademico(cicloDestino);
            diaHoraGrupoDAO.save(horaGrupo);
        }
    }

}
