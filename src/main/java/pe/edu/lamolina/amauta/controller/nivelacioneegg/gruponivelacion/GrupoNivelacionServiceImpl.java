package pe.edu.lamolina.amauta.controller.nivelacioneegg.gruponivelacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioGrupoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioGrupoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class GrupoNivelacionServiceImpl implements GrupoNivelacionService {

    private final GrupoNivelacionDAO grupoNivelacionDAO;
    private final HorarioGrupoNivelacionDAO horarioGrupoNivelacionDAO;
    private final DiaDAO diaDAO;
    private final HoraDAO horaDAO;

    private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Z][A-Z0-9/-]*$");
    private static final String REGULAR = "REGULAR";
    private static final String FLEXIBLE = "FLEXIBLE";
    private static final String ZETA = "ZETA";

    @Override
    public List<GrupoNivelacion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, DataSessionPivot ds) {
        List<GrupoNivelacion> grupos = grupoNivelacionDAO.allByDynatable(filter);
        List<HorarioGrupoNivelacion> horariosAll = horarioGrupoNivelacionDAO.allByGruposCiclo(grupos, ciclo);
        Map<Long, List<HorarioGrupoNivelacion>> mapHorarios = horariosAll.stream()
                .collect(Collectors.groupingBy(hg -> hg.getGrupoNivelacion().getId()));

        grupos.forEach(grupo -> {
            List<HorarioGrupoNivelacion> horarios  = mapHorarios.getOrDefault(grupo.getId(), new ArrayList());
            log.info("grupo={} horario={}", grupo.getId(), horarios);
            grupo.setHorariosGrupo(horarios);
        });

        return grupos;
    }

    private void prevalidacionGrupo(GrupoNivelacion grupo, DataSessionPivot ds) {
        Assert.isNotNull(grupo.getCodigo(), "No ha indicado el código del grupo.");
        Assert.isTrue(PATRON_CODIGO.matcher(grupo.getCodigo()).matches(),
                "El código debe empezar con letra y solo contener mayúsculas, números, guiones o barras");

        GrupoNivelacion gpoBD = grupoNivelacionDAO.findByCodigo(grupo.getCodigo());
        Assert.isNull(gpoBD, "Ya existe un grupo con ese código");

        grupo.setTipo(FLEXIBLE);
        grupo.setOrden(2);
        if(grupo.getCodigo().length()==1) {
            grupo.setTipo(REGULAR);
            grupo.setOrden(1);
        } else if(grupo.getCodigo().equals("Z")){
            grupo.setTipo(ZETA);
            grupo.setOrden(3);
        }
    }

    @Override
    @Transactional
    public void saveGrupo(GrupoNivelacion grupo, DataSessionPivot ds) {
        this.prevalidacionGrupo(grupo, ds);

        GrupoNivelacion newGrupo = new GrupoNivelacion();
        newGrupo.setCodigo(grupo.getCodigo());
        newGrupo.setTipo(grupo.getTipo());
        newGrupo.setOrden(grupo.getOrden());
        newGrupo.setUserRegistro(ds.getUsuario());
        newGrupo.setFechaRegistro(new Date());
        grupoNivelacionDAO.save(newGrupo);
    }

    @Override
    @Transactional
    public void updateGrupo(GrupoNivelacion grupo, CicloAcademico ciclo, DataSessionPivot ds) {
        this.prevalidacionGrupo(grupo, ds);

        GrupoNivelacion grupoBD = grupoNivelacionDAO.find(grupo.getId());
        Assert.isNotNull(grupoBD, "El grupo que ha seleccionado no existe en el sistema");
        Assert.isFalse(grupoBD.getCodigo().equals(grupo.getCodigo()), "Este grupo ya tiene ese código");

        List<HorarioGrupoNivelacion> horariosCiclo = horarioGrupoNivelacionDAO.allByGrupoCiclo(grupo, ciclo);
        Assert.isTrue(horariosCiclo.isEmpty(), "Este grupo ya no puede ser modificado porque tiene configurado su horario");

        List<HorarioGrupoNivelacion> horariosAll = horarioGrupoNivelacionDAO.allByGrupo(grupo);
        Assert.isTrue(horariosAll.isEmpty(), "Este grupo ya no puede ser modificado porque fue configurado en otros ciclos");

        grupoBD.setCodigo(grupo.getCodigo());
        grupoBD.setTipo(grupo.getTipo());
        grupoBD.setOrden(grupo.getOrden());
        grupoBD.setUserRegistro(ds.getUsuario());
        grupoBD.setFechaRegistro(new Date());
        grupoNivelacionDAO.update(grupoBD);
    }

    @Override
    @Transactional
    public void eliminarGrupo(GrupoNivelacion grupo, DataSessionPivot ds) {
        GrupoNivelacion grupoBD = grupoNivelacionDAO.find(grupo.getId());
        List<HorarioGrupoNivelacion> horarios = horarioGrupoNivelacionDAO.allByGrupo(grupoBD);
        for (HorarioGrupoNivelacion horario : horarios) {
            horarioGrupoNivelacionDAO.delete(horario);
        }
        grupoNivelacionDAO.delete(grupoBD);
    }

    @Override
    @Transactional
    public void saveHorarioGrupo(Long grupoId, List<HorarioGrupoNivelacion> horarios, CicloAcademico ciclo, DataSessionPivot ds) {
        GrupoNivelacion grupo = grupoNivelacionDAO.find(grupoId);
        Assert.isNotNull(grupo, "El grupo que ha seleccionado no existe en el sistema");

        List<HorarioGrupoNivelacion> otrosHorarios = horarioGrupoNivelacionDAO.allRegularByCiclo(ciclo).stream()
                .filter(hor -> !hor.getGrupoNivelacion().getId().equals(grupoId))
                .collect(Collectors.toList());

        if (grupo.getTipo().equals(REGULAR)) {
            for (HorarioGrupoNivelacion nuevo : horarios) {
                boolean hayCruce = otrosHorarios.stream().anyMatch(existente -> {
                    boolean cruceDia = existente.getDia().getId().equals(nuevo.getDia().getId());
                    boolean cruceHora = existente.getHora().getId().equals(nuevo.getHora().getId());
                    return cruceDia && cruceHora;
                });
                Assert.isFalse(hayCruce, "Hay cruce de horario con grupos regulares");
            }
        }

        Map<Long, Integer> mapHoras = horaDAO.all().stream().collect(Collectors.toMap(Hora::getId, Hora::getNumero));
        Map<Long, List<Integer>> horasPorDia = horarios.stream()
                .collect(Collectors.groupingBy(h -> h.getDia().getId(),
                        Collectors.mapping(h -> mapHoras.get(h.getHora().getId()), Collectors.toList())));

        for (List<Integer> numeros : horasPorDia.values()) {
            if (numeros.size() > 1) {
                numeros.sort(Integer::compareTo);
                for (int i = 0; i < numeros.size() - 1; i++) {
                    boolean sonContinuas = numeros.get(i + 1) == numeros.get(i) + 1;
                    Assert.isTrue(sonContinuas, "Las horas deben ser consecutivas");
                }
            }
        }

        int cambios = 0;

        // Eliminar horarios existentes
        List<HorarioGrupoNivelacion> horariosBD = horarioGrupoNivelacionDAO.allByGrupo(grupo);
        for (HorarioGrupoNivelacion previo : horariosBD) {
            boolean noExiste = horarios.stream().noneMatch(nuevo -> {
                boolean cruceDia = nuevo.getDia().getId().equals(previo.getDia().getId());
                boolean cruceHora = nuevo.getHora().getId().equals(previo.getHora().getId());
                return cruceDia && cruceHora;
            });
            if (noExiste) {
                horarioGrupoNivelacionDAO.delete(previo);
                cambios++;
            };
        }

        // Guardar nuevos horarios
        for (HorarioGrupoNivelacion nuevo : horarios) {
            boolean noExiste = horariosBD.stream().noneMatch(previo -> {
                boolean cruceDia = previo.getDia().getId().equals(nuevo.getDia().getId());
                boolean cruceHora = previo.getHora().getId().equals(nuevo.getHora().getId());
                return cruceDia && cruceHora;
            });

            if (noExiste) {
                HorarioGrupoNivelacion newHorario = new HorarioGrupoNivelacion();
                newHorario.setGrupoNivelacion(grupo);
                newHorario.setCicloAcademico(ciclo);
                newHorario.setDia(new Dia(nuevo.getDia().getId()));
                newHorario.setHora(new Hora(nuevo.getHora().getId()));
                newHorario.setUserRegistro(ds.getUsuario());
                newHorario.setFechaRegistro(new Date());
                horarioGrupoNivelacionDAO.save(newHorario);
                cambios++;
            }
        }

        Assert.isTrue(cambios > 0, "No ha enviado nuevos cambios");
    }

    @Override
    public List<HorarioGrupoNivelacion> getHorarioGrupo(Long grupoId, CicloAcademico ciclo) {
        GrupoNivelacion grupo = new GrupoNivelacion(grupoId);
        return horarioGrupoNivelacionDAO.allByGrupoCiclo(grupo, ciclo);
    }

    @Override
    public List<HorarioGrupoNivelacion> getHorarioOtrosGrupos(Long grupoId, CicloAcademico ciclo) {
        GrupoNivelacion grupo = new GrupoNivelacion(grupoId);
        List<HorarioGrupoNivelacion> horarios = horarioGrupoNivelacionDAO.allRegularByCiclo(ciclo);

        return horarios.stream()
                .filter(hor -> !hor.getGrupoNivelacion().getId().equals(grupoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Dia> allDias() {
        return diaDAO.all();
    }

    @Override
    public List<Hora> allHoras() {
        return horaDAO.all();
    }
}
