package pe.edu.lamolina.pivot.controller.rolexamen.reportes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;

@Service
@Transactional(readOnly = true)
public class RolExamenReporteServiceImp implements RolExamenReporteService {

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    GrupoRegularExamenDAO grupoRegularExamenDAO;

    @Autowired
    AulaCursoMasivoDAO aulaCursoMasivoDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Override
    public List<LetraGrupoRegular> allLetrasGrupoRegularByRolExamenes(RolExamenes rol) {
        List<LetraGrupoRegular> regulares = letraGrupoRegularDAO.allByRolExamenesForReporte(rol);
        List<GrupoRegularExamen> grupos = grupoRegularExamenDAO.allActivosByLetrasGrupoRegular(regulares);
        Map<LetraGrupoRegular, List<GrupoRegularExamen>> mapGrupos = grupos.stream().collect(Collectors.groupingBy(GrupoRegularExamen::getLetraGrupoRegular));
        for (Map.Entry<LetraGrupoRegular, List<GrupoRegularExamen>> entry : mapGrupos.entrySet()) {
            entry.getKey().setGruposRegularesExamenes(entry.getValue());
        }

        List<SeccionGrupoRegular> secciones = seccionGrupoRegularDAO.allByLetraGrupoRegularAndEstados(regulares, SeccionRolExamenEstadoEnum.ACT);
        Map<LetraGrupoRegular, List<SeccionGrupoRegular>> mapSecciones = secciones.stream().collect(Collectors.groupingBy(SeccionGrupoRegular::getLetraGrupoRegular));
        for (Map.Entry<LetraGrupoRegular, List<SeccionGrupoRegular>> entry : mapSecciones.entrySet()) {
            entry.getKey().setSeccionesGruposRegulares(entry.getValue());
        }

        return regulares;
    }

    @Override
    public List<SeccionGrupoEspecial> allSeccionGrupoEspecialByRolExamenes(RolExamenes rol) {
        return seccionGrupoEspecialDAO.allByRolExamenesForReporte(rol);
    }

    @Override
    public List<CursoMasivoExamen> allCursoMasivoExamenByRolExamenes(RolExamenes rol) {
        List<CursoMasivoExamen> masivos = cursoMasivoExamenDAO.allByRolExamenesForReporte(rol);
        List<AulaCursoMasivo> aulas = aulaCursoMasivoDAO.allByCursosMasivos(masivos);
        Map<CursoMasivoExamen, List<AulaCursoMasivo>> mapAulas = aulas.stream().collect(Collectors.groupingBy(AulaCursoMasivo::getCursoMasivoExamen));
        for (Map.Entry<CursoMasivoExamen, List<AulaCursoMasivo>> entry : mapAulas.entrySet()) {
            entry.getKey().setAulasCursosMasivos(entry.getValue());
        }

        return masivos;
    }

    @Override
    public void infoReporteAulas(Model model, RolExamenes rol) {
        List<FechaHoraGrupoExamen> fechas = fechaHoraGrupoExamenDAO.allByRolExamens(rol);

        List<Date> dias = new ArrayList<>();
        Map<Date, List<Integer>> horasPorDia = new HashMap();

        for (FechaHoraGrupoExamen fecha : fechas) {
            Date dia = fecha.getGrupoHorasExamen().getFecha();
            if (!horasPorDia.containsKey(dia)) {
                dias.add(dia);
                horasPorDia.put(dia, new ArrayList<>());
            }
            horasPorDia.get(dia).add(fecha.getHora().getNumero());
        }

        dias.sort(Comparator.naturalOrder());

        for (Map.Entry<Date, List<Integer>> entry : horasPorDia.entrySet()) {
            entry.getValue().sort(Comparator.naturalOrder());
        }

        Oficina oficinaEstudios = oficinaDAO.findByCode(OficinaEnum.OERA.name());
        List<Aula> aulas = aulaDAO.allByOficinaSupervisora(oficinaEstudios);
        List<Aula> modulos = aulas.stream().map(Aula::getAulaSuperior).distinct().collect(Collectors.toList());

        modulos.sort(Comparator.comparing(Aula::getNombre));

        Map<Aula, List<Aula>> aulasPorModulo = aulas.stream().collect(Collectors.groupingBy(Aula::getAulaSuperior));

        for (Map.Entry<Aula, List<Aula>> entry : aulasPorModulo.entrySet()) {
            entry.getValue().sort(Comparator.comparing(Aula::getCodigo));
        }

        Map<Aula, Map<Date, Map<Integer, List>>> mapOcupacion = new HashMap<>();

        List<CursoMasivoExamen> masivos = this.allCursoMasivoExamenByRolExamenes(rol);
        for (CursoMasivoExamen masivo : masivos) {
            for (AulaCursoMasivo aulasCursosMasivo : masivo.getAulasCursosMasivos()) {
                Aula aula = aulasCursosMasivo.getAula();
                //Date dia = aulasCursosMasivo.getCursoMasivoExamen().getGrupoHorasExamen().getFecha();
                Date dia = (Date) ObjectUtil.getParentTree(aulasCursosMasivo, "cursoMasivoExamen.grupoHorasExamen.fecha");
                if (aula == null || dia == null) {
                    continue;
                }
                Integer horaInicio = aulasCursosMasivo.getCursoMasivoExamen().getGrupoHorasExamen().getHoraInicio().getNumero();
                Integer horaFin = aulasCursosMasivo.getCursoMasivoExamen().getGrupoHorasExamen().getHoraFin().getNumero();
                Assert.isTrue(horaFin >= horaInicio, "Hora de inicio mayor que la hora de fin del examen");
                for (int hora = horaInicio; hora <= horaFin; hora++) {
                    this.agregarOcupacion(mapOcupacion, aula, dia, hora, masivo);
                }
            }
        }

        List<SeccionGrupoEspecial> especiales = this.allSeccionGrupoEspecialByRolExamenes(rol);
        for (SeccionGrupoEspecial especiale : especiales) {
            if (especiale.getEstadoEnum() != SeccionRolExamenEstadoEnum.ACT) {
                continue;
            }
            Aula aula = especiale.getAula();
            Date dia = especiale.getGrupoHorasExamen().getFecha();
            Integer horaInicio = especiale.getGrupoHorasExamen().getHoraInicio().getNumero();
            Integer horaFin = especiale.getGrupoHorasExamen().getHoraFin().getNumero();
            Assert.isTrue(horaFin >= horaInicio, "Hora de inicio mayor que la hora de fin del examen");
            for (int hora = horaInicio; hora <= horaFin; hora++) {
                this.agregarOcupacion(mapOcupacion, aula, dia, hora, especiale);
            }
        }

        List<LetraGrupoRegular> letrasRegulares = this.allLetrasGrupoRegularByRolExamenes(rol);
        for (LetraGrupoRegular letra : letrasRegulares) {
            for (SeccionGrupoRegular seccionRegular : letra.getSeccionesGruposRegulares()) {
                if (seccionRegular.getEstadoEnum() != SeccionRolExamenEstadoEnum.ACT) {
                    continue;
                }
                Aula aula = seccionRegular.getAula();
                Date dia = letra.getGrupoHorasExamen().getFecha();
                Integer horaInicio = letra.getGrupoHorasExamen().getHoraInicio().getNumero();
                Integer horaFin = letra.getGrupoHorasExamen().getHoraFin().getNumero();
                Assert.isTrue(horaFin >= horaInicio, "Hora de inicio mayor que la hora de fin del examen");
                for (int nroHora = horaInicio; nroHora <= horaFin; nroHora++) {
                    this.agregarOcupacion(mapOcupacion, aula, dia, nroHora, seccionRegular);
                }
            }
        }

        model.addAttribute("rol", rol);
        model.addAttribute("dias", dias);
        model.addAttribute("horasPorDia", horasPorDia);
        model.addAttribute("modulos", modulos);
        model.addAttribute("aulasPorModulo", aulasPorModulo);
        model.addAttribute("mapOcupacion", mapOcupacion);
    }

    private void agregarOcupacion(Map<Aula, Map<Date, Map<Integer, List>>> mapOcupacion, Aula aula, Date fecha, Integer numeroHora, Object obj) {
        if (!mapOcupacion.containsKey(aula)) {
            mapOcupacion.put(aula, new HashMap());
        }

        if (!mapOcupacion.get(aula).containsKey(fecha)) {
            mapOcupacion.get(aula).put(fecha, new HashMap());
        }

        if (!mapOcupacion.get(aula).get(fecha).containsKey(numeroHora)) {
            List objetos = new ArrayList();
            objetos.add(obj);
            mapOcupacion.get(aula).get(fecha).put(numeroHora, objetos);
        } else {
            List objetos = mapOcupacion.get(aula).get(fecha).get(numeroHora);
            objetos.add(obj);
        }
    }

    @Override
    public RolExamenes find(Long id) {
        RolExamenes rol = rolExamenesDAO.find(id);
        return rol;
    }
}
