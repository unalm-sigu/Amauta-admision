package pe.edu.lamolina.amauta.controller.programacionhorarios.horario.grupo;

import java.util.ArrayList;
import java.util.Date;
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
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.GrupoHorasExcluido;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasExcluidoDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.dao.horario.TipoGrupoHorasDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GrupoHorasServiceImp implements GrupoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    GrupoHorasExcluidoDAO grupoHorasExcluidoDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    SeccionDAO seccionDAO;

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
    public void delete(GrupoHoras grupoHoras, CicloAcademico ciclo, DataSessionPivot ds) {
        GrupoHoras gpoBD = grupoHorasDAO.find(grupoHoras);
        Assert.isNotNull(gpoBD, "Este grupo no existe en la base de datos");
        try {
            grupoHorasDAO.delete(gpoBD);
        } catch (Exception e) {
            throw new PhobosException("Este grupo se encuentra relacionado a otros elementos de la base de datos. No puede ser eliminado.");
        }

    }

    @Override
    @Transactional
    public void save(GrupoHoras grupoHoras) {
        grupoHoras.setLetra(grupoHoras.getLetra().toUpperCase());
        grupoHoras.setCodigo(grupoHoras.getCodigo().toUpperCase());
        grupoHoras.setTipoCiclo("REGULAR");
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
    @Transactional
    public void ocultar(GrupoHoras grupoHoras, CicloAcademico ciclo, DataSessionPivot ds) {
        GrupoHoras gpoBD = grupoHorasDAO.find(grupoHoras);
        Assert.isNotNull(gpoBD, "Este grupo no existe en la base de datos");

        List<DiaHoraGrupo> diasHorasGpo = diaHoraGrupoDAO.allByGrupoCiclo(gpoBD, ciclo);
        Assert.isTrue(diasHorasGpo.isEmpty(), "No puede ocultar un grupo si tiene horario");

        GrupoHorasExcluido gpoExcluido = grupoHorasExcluidoDAO.findByGpoCiclo(gpoBD, ciclo);
        Assert.isNull(gpoExcluido, "Este grupo ya fue ocultado en el ciclo");

        gpoExcluido = new GrupoHorasExcluido();
        gpoExcluido.setGrupoHoras(gpoBD);
        gpoExcluido.setCicloAcademico(ciclo);
        gpoExcluido.setUserExcluye(ds.getUsuario());
        gpoExcluido.setFechaExcluye(new Date());
        grupoHorasExcluidoDAO.save(gpoExcluido);

    }

    @Override
    public GrupoHoras findGrupoHorasByCode(String codigo) {
        return grupoHorasDAO.findGrupoHorasByCode(codigo);
    }

    @Override
    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter, CicloAcademico ciclo) {
        return grupoHorasDAO.allGrupoHoras(filter, ciclo);
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
    public void desasignarHora(DiaHoraGrupo diaHoraGrupoForm, CicloAcademico ciclo) {
        logger.debug("DELETE DIAHOARGRUPO {}", diaHoraGrupoForm.getId());
        DiaHoraGrupo diaHoraGrupoBD = diaHoraGrupoDAO.findByCicloGrupoDiaHora(ciclo, diaHoraGrupoForm.getGrupoHorario(), diaHoraGrupoForm.getDia(), diaHoraGrupoForm.getHora());
        Assert.isNotNull(diaHoraGrupoBD, "No existe este dia-hora asignado a este grupo");
        diaHoraGrupoDAO.delete(diaHoraGrupoBD);
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
            if (tipo.getEstadoEnum() == EstadoEnum.ACT && tipo.getTipoEnum() == TipoGrupoHorasEnum.REGULAR) {
                return tipo;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void clonar(CicloAcademico cicloOrigenForm, CicloAcademico cicloDestinoForm) {
        CicloAcademico cicloDestino = cicloAcademicoDAO.findByCiclo(cicloDestinoForm);
        CicloAcademico cicloOrigen = cicloAcademicoDAO.findByCiclo(cicloOrigenForm);

        List<DiaHoraGrupo> diaHoraGrupos = diaHoraGrupoDAO.allByCiclo(cicloOrigen);
        Assert.isTrue(cicloDestino.getId() != cicloOrigen.getId(), "No puede clonar del mismo ciclo");

        Assert.isTrue(cicloDestino.getTipo().equals(cicloOrigen.getTipo()), "No puede clonar de un tipo de ciclo distinto");

        for (DiaHoraGrupo diaHoraGrupo : diaHoraGrupos) {
            DiaHoraGrupo horaGrupo = new DiaHoraGrupo();
            horaGrupo.setDia(diaHoraGrupo.getDia());
            horaGrupo.setGrupoHorario(diaHoraGrupo.getGrupoHorario());
            horaGrupo.setHora(diaHoraGrupo.getHora());
            horaGrupo.setCicloAcademico(cicloDestino);
            diaHoraGrupoDAO.save(horaGrupo);
        }
    }

    @Override
    public GrupoHorasExcluido findGrupoHorasOculto(GrupoHoras grupoCode, CicloAcademico cicloAcademico) {
        if (grupoCode == null) {
            return null;
        }
        return grupoHorasExcluidoDAO.findByGpoCiclo(grupoCode, cicloAcademico);
    }

    @Override
    public List<GrupoHoras> allOcultosByCiclo(TipoGrupoHoras tipoGpo, CicloAcademico cicloAcademico) {
        List<GrupoHorasExcluido> gposExcluidos = grupoHorasExcluidoDAO.allByTipoGpoCiclo(tipoGpo, cicloAcademico);
        List<GrupoHoras> gpos = new ArrayList();
        for (GrupoHorasExcluido gpoHide : gposExcluidos) {
            gpos.add(gpoHide.getGrupoHoras());
        }
        return gpos;
    }

    @Override
    @Transactional
    public void activarGrupos(List<GrupoHoras> gpos, CicloAcademico cicloAcademico) {
        List<GrupoHorasExcluido> gposExcluidos = grupoHorasExcluidoDAO.allByGpoHorasCiclo(gpos, cicloAcademico);
        for (GrupoHorasExcluido ghe : gposExcluidos) {
            grupoHorasExcluidoDAO.delete(ghe);
        }
    }

}
