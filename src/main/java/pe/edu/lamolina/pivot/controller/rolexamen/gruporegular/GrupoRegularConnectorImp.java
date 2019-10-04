package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.DocenteRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.DocenteCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GrupoRegularConnectorImp implements GrupoRegularConnector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    AulaCursoMasivoDAO aulaCursoMasivoDAO;

    @Autowired
    DocenteCursoMasivoDAO docenteCursoMasivoDAO;

    @Autowired
    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    RolExamenesDAO rolexamenesDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savedLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        letraGrupoRegularDAO.save(letraGrupoRegular);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void crearLetraGrupoRegularByLetra(
            LetraGrupoRegular letraGrupoRegular,
            List<CursoMasivoExamen> cursosMasivosByLetra,
            List<SeccionGrupoEspecial> seccionesGpoEspecialByLetra,
            Map<String, List<Seccion>> mapSeccionesByLetra,
            List<Seccion> seccionesEspeciales,
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<CursoMasivoExamen> cursosMasivosAll,
            List<SeccionGrupoEspecial> seccionesGrupoEspecialAllqqqq,
            DataSessionPivot ds) {

        final int AFORO_INCREMENTO = 5;
        long ini = System.currentTimeMillis();
        List<Seccion> seccionesByLetra = mapSeccionesByLetra.get(letraGrupoRegular.getLetra());
        if (seccionesByLetra == null) {
            return;
        }
        List<DocenteSeccion> docentesPrincipales = docenteSeccionDAO.allPrincipalesBySecciones(seccionesByLetra);
        letraGrupoRegular.setContadorSecciones(BigDecimal.ZERO.intValue());

        for (Seccion seccion : seccionesByLetra) {
            System.out.println("Evaluado seccion " + seccion.getId() + " letra " + letraGrupoRegular.getLetra());
            this.rolExamenesLogger.addMessageLevel3("Evaluando la sección %s", seccion.getCodigo2());
            Seccion seccionClone = seccion.clone();
            List<DocenteSeccion> docenteSecciones = docentesPrincipales.stream().filter(x -> x.getSeccion().equals(seccionClone)).collect(Collectors.toList());
            Assert.isFalse(docenteSecciones.isEmpty(), String.format("La sección (%s) de código %s, no tiene docente principal", seccionClone.getId(), seccionClone.getCodigo2()));
            Assert.isTrue(docenteSecciones.size() == 1, String.format("La sección (%s) de código %s, tiene mas de un docente principal", seccionClone.getId(), seccionClone.getCodigo2()));
            seccionClone.setDocenteSeccion(docenteSecciones);

            boolean result = false;
            result = this.procesarSeccionesByLetra(
                    letraGrupoRegular,
                    cursosMasivosByLetra,
                    seccionesGpoEspecialByLetra,
                    seccionClone,
                    seccionesByLetra,
                    letrasGruposRegulares,
                    cursosMasivosAll,
                    ds);
            if (result) {
                this.rolExamenesLogger.addMessageLevel3("La sección %s fue asignada correctamente.", seccion.getCodigo2());
            }

            if (!result) {
                seccionesEspeciales.add(seccionClone);
            }
        }
        long end = System.currentTimeMillis();

        long milis = end - ini;
        logger.debug("Termino en Segundos {}, MiliSeconds {}", TimeUnit.MILLISECONDS.toSeconds(milis), milis);
    }

    @Override
    public Map<Long, List<Aula>> aulasAgrupadasPorModulo(Aula aulaSeccionOriginal) {
        Map<Long, List<Aula>> mapAulasAgrupadasPorModulo = TypesUtil.convertListToMapList("aulaSuperior.id", this.rolExamenesLogger.getAulasOera());
        Map<Long, List<Aula>> mapAulasAgrupadasPorModuloOrdered = new LinkedHashMap();
        boolean hasAulaSuperior = ObjectUtil.getParentTree(aulaSeccionOriginal, "aulaSuperior.id") != null;
        if (hasAulaSuperior) {
            List<Aula> aulas = mapAulasAgrupadasPorModulo.get(aulaSeccionOriginal.getAulaSuperior().getId());
            if (aulas == null) {
                aulas = new ArrayList<Aula>();
            }
            mapAulasAgrupadasPorModuloOrdered.put(aulaSeccionOriginal.getAulaSuperior().getId(), aulas);
        }
        for (Map.Entry<Long, List<Aula>> entry : mapAulasAgrupadasPorModulo.entrySet()) {
            Long key = entry.getKey();
            List<Aula> value = entry.getValue();
            if (value == null) {
                value = new ArrayList<>();
            }
            if (hasAulaSuperior && key.compareTo(aulaSeccionOriginal.getAulaSuperior().getId()) == 0) {
                continue;
            }
            mapAulasAgrupadasPorModuloOrdered.put(key, value);
        }
        return mapAulasAgrupadasPorModuloOrdered;
    }

    private Aula buscarAulaOeraBySeccion(
            Seccion seccion,
            LetraGrupoRegular letraGrupoRegular,
            Map<Long, List<Aula>> aulasAgrupadasPorModulo,
            List<CursoMasivoExamen> cursosMasivosExamenByLetra,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<Seccion> seccionesByLetra,
            Integer inicio,
            Integer fin,
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<CursoMasivoExamen> cursosMasivosExamenByAll,
            DataSessionPivot ds) {

        //logger.info("Entro a buscar aula");
        Seccion seccionClone = seccion.clone();
        Aula aulaSeccionOriginal = seccion.getAula();
        GrupoHorasExamen grupoHorasExamen = letraGrupoRegular.getGrupoHorasExamen();

        for (Map.Entry<Long, List<Aula>> entry : aulasAgrupadasPorModulo.entrySet()) {
            List<Aula> aulasByModulo = entry.getValue();
            AULA_EACH:
            for (Aula aula : aulasByModulo) {
                if (!(aula.getAforo() >= inicio && aula.getAforo() <= fin)) {
                    continue;
                }
                for (String diaHora : grupoHorasExamen.getDiaHoraList()) {
                    if (aula.getDiaHoraList().contains(diaHora)) {
                        continue AULA_EACH;
                    }
                }
                seccionClone.setAula(aula);
                this.rolExamenesLogger.addMessageLevel3("Buscará disponibilidad en el aula %s", aula.getCodigo());
                boolean result = this.procesarSeccionesByLetra(
                        letraGrupoRegular,
                        cursosMasivosExamenByLetra,
                        seccionesGrupoEspecial,
                        seccionClone,
                        seccionesByLetra,
                        letrasGruposRegulares,
                        cursosMasivosExamenByAll, ds);
                if (result) {
                    return aula;
                }
            }
        }
        return aulaSeccionOriginal;
    }

    @Override
    public boolean checkDisponibilidadAula(Aula aula, GrupoHorasExamen grupoHorasExamen) {
        List<Aula> aulasWithHorarios = this.rolExamenesLogger.getAulas();
        Aula aulaCompare = aulasWithHorarios.stream().filter(x -> x.equals(aula))
                .findFirst().orElse(null);

        for (String diaHora : grupoHorasExamen.getDiaHoraList()) {
            if (aulaCompare.getDiaHoraList().contains(diaHora)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean procesarSeccionesByLetra(
            LetraGrupoRegular letraGrupoRegular,
            List<CursoMasivoExamen> cursosMasivosByLetra,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Seccion seccion,
            List<Seccion> seccionesByLetraOnlyInformative,
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<CursoMasivoExamen> cursosMasivosAll,
            DataSessionPivot ds) {

        List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);

        List<Alumno> alumnos = matriculadosPorSeccion.stream().map(x -> x.getMatriculaResumen().getAlumno()).collect(Collectors.toList());
        List<Aula> aulas = Arrays.asList(seccion.getAula());
        List<Docente> docentes = Arrays.asList(seccion.getDocenteSeccion().get(0).getDocente());

        boolean validacionCursosMasivosCorrecta = this.validarCursosMasivos(cursosMasivosByLetra, docentes, aulas, alumnos);
        boolean validacionGrupoRegularCorrecta = this.validarGrupoRegular(letraGrupoRegular, alumnos, docentes, aulas);
        boolean validacionGrupoEspecialCorrecta = this.validarGrupoEspecial(seccionesGrupoEspecial, alumnos, docentes, aulas);
        boolean validacionCruceAulas = this.validarCrucesAulas(letraGrupoRegular, aulas);
        boolean validacionTripleExamen = this.validarMaximoExamenesByAlumno(
                letraGrupoRegular,
                alumnos,
                letraGrupoRegular.getGrupoHorasExamen().getFecha(),
                cursosMasivosAll,
                seccionesGrupoEspecial,
                letrasGruposRegulares);

        if (!validacionGrupoRegularCorrecta || !validacionCursosMasivosCorrecta
                || !validacionGrupoEspecialCorrecta || !validacionTripleExamen || !validacionCruceAulas) {
            return false;
        }

        letraGrupoRegular.setContadorSecciones(letraGrupoRegular.getContadorSecciones() + 1);
        this.crearSeccionGrupoRegular(seccion, letraGrupoRegular, matriculadosPorSeccion, ds);
        return true;
    }

    private boolean validarCrucesAulas(LetraGrupoRegular letraGrupoRegular, List<Aula> aulas) {
        Date fecha = letraGrupoRegular.getGrupoHorasExamen().getFecha();

        Map<Long, List<HorarioAula>> mapHorariosAula = this.rolExamenesLogger.getHorarioAulas();

        boolean aulaConConflicto = true;
        for (Aula aula : aulas) {
            int cruces = 0;
            TipoHorarioAulaEnum tipo = null;
            List<HorarioAula> horariosAulas = TypesUtil.getListNotNull(mapHorariosAula.get(aula.getId()));
            for (FechaHoraGrupoExamen fechaHorGru : letraGrupoRegular.getGrupoHorasExamen().getFechasHorasGruposExamen()) {

                for (HorarioAula ha : horariosAulas) {
                    if (ha.getDia().getId().compareTo(fechaHorGru.getDia().getId()) != 0) {
                        continue;
                    }
                    if (ha.getHora().getId().compareTo(fechaHorGru.getHora().getId()) != 0) {
                        continue;
                    }
                    if (fecha.compareTo(ha.getFechaInicio()) >= 0 && fecha.compareTo(ha.getFechaFin()) <= 0) {
                        aulaConConflicto = false;
                        tipo = ha.getTipoEnum();
                        cruces++;
                    }
                }
            }
            if (cruces > 0) {
                System.out.println("\tCruce con aula " + aula.getId() + " - tipo " + tipo.name());
            }
        }

        return aulaConConflicto;

    }

    private boolean validarMaximoExamenesByAlumno(
            LetraGrupoRegular letraGrupoRegular,
            List<Alumno> alumnos,
            Date fecha,
            List<CursoMasivoExamen> cursosMasivosExamen,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<LetraGrupoRegular> letrasGruposRegulares) {

        //System.out.println("analizando triple-examen de " + letraGrupoRegular.getLetra());
        List<CursoMasivoExamen> cursosMasivosFecha = new ArrayList();
        for (CursoMasivoExamen cursoMasivo : cursosMasivosExamen) {
            if (cursoMasivo.getGrupoHorasExamen() == null) {
                continue;
            }
            Date fechaExamen = cursoMasivo.getGrupoHorasExamen().getFecha();
            if (fecha.equals(fechaExamen)) {
                cursosMasivosFecha.add(cursoMasivo);
            }
        }
        //System.out.println("\tcursos masivos con misma fecha " + cursosMasivosFecha.size());

        List<SeccionGrupoEspecial> seccionEspecialesFecha = new ArrayList();
        for (SeccionGrupoEspecial seccionGE : seccionesGrupoEspecial) {
            if (seccionGE.getGrupoHorasExamen() == null) {
                continue;
            }
            Date fechaExamen = seccionGE.getGrupoHorasExamen().getFecha();
            if (fecha.equals(fechaExamen)) {
                seccionEspecialesFecha.add(seccionGE);
            }
        }
        //System.out.println("\tgrupos especiales con misma fecha " + seccionEspecialesFecha.size());

        List<SeccionGrupoRegular> seccionGpoRegFecha = new ArrayList();
        for (LetraGrupoRegular letraGR : letrasGruposRegulares) {
            if (letraGR.getLetra().equals(letraGrupoRegular.getLetra())) {
                continue;
            }
            if (letraGR.getGrupoHorasExamen() == null) {
                continue;
            }
            Date fechaExamen = letraGR.getGrupoHorasExamen().getFecha();
            if (fecha.equals(fechaExamen)) {
                List<SeccionGrupoRegular> seccionGR = letraGR.getSeccionesGruposRegulares();
                seccionGpoRegFecha.addAll(seccionGR);
            }
        }
        //System.out.println("\tgrupos regulares con misma fecha " + seccionGpoRegFecha.size());

        int alumnoTriples = 0;
        for (Alumno alumno : alumnos) {
            int contador = 0;
            String masivos = "";
            for (CursoMasivoExamen cursoMasivoExamen : cursosMasivosFecha) {
                List<AlumnoCursoMasivo> alumnosCursoM = cursoMasivoExamen.getAlumnosCursosMasivos();
                for (AlumnoCursoMasivo alumnoCursoMasivo : alumnosCursoM) {
                    if (alumnoCursoMasivo.getAlumno().getId() == alumno.getId().longValue()) {
                        masivos += cursoMasivoExamen.getId() + ",";
                        contador++;
                    }
                }
            }
            if (contador >= 2) {
                //return false;
            }

            String especiales = "";
            for (SeccionGrupoEspecial seccionGE : seccionEspecialesFecha) {
                List<AlumnoGrupoEspecial> alumnosGE = seccionGE.getAlumnosGrupoEspecial();
                for (AlumnoGrupoEspecial alumnoGE : alumnosGE) {
                    if (alumnoGE.getAlumno().getId() == alumno.getId().longValue()) {
                        especiales += seccionGE.getSeccion().getId() + ",";
                        contador++;
                    }
                }
            }

            if (contador >= 2) {
                //return false;
            }

            String regulares = "";
            for (SeccionGrupoRegular seccionGR : seccionGpoRegFecha) {
                List<AlumnoGrupoRegular> alumnosGR = seccionGR.getAlumnosGruposRegulares();
                for (AlumnoGrupoRegular alumnoGR : alumnosGR) {
                    if (alumnoGR.getAlumno().getId() == alumno.getId().longValue()) {
                        regulares += seccionGR.getSeccion().getId() + ",";
                        contador++;
                    }
                }
            }
            if (contador >= 2) {
                //return false;
                alumnoTriples++;
                System.out.print("\tAlumno " + alumno.getId() + " con " + contador + " examenes ");
                if (!masivos.equals("")) {
                    System.out.print("masivos={" + masivos + "} ");
                }
                if (!especiales.equals("")) {
                    System.out.print("especiales={" + especiales + "} ");
                }
                if (!regulares.equals("")) {
                    System.out.print("regulares={" + regulares + "} ");
                }
                System.out.println("");
            }
        }

        return alumnoTriples == 0;
    }

    @Override
    public boolean validarGrupoRegular(GrupoHorasExamen grupoHorasExamen,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas) {
        if (grupoHorasExamen == null) {
            return true;
        }
        LetraGrupoRegular letraGrupoRegular = letraGrupoRegularDAO.findByGrupoHorasExamen(grupoHorasExamen);
        if (letraGrupoRegular == null) {
            return true;
        }
        this.fillActiveInfoLetrasGruposRegulares(Arrays.asList(letraGrupoRegular));
        return this.validarGrupoRegular(letraGrupoRegular, alumnos, docentes, aulas);
    }

    @Override
    public boolean validarGrupoRegular(
            LetraGrupoRegular letraGrupoRegular,
            List<Alumno> alumnos,
            List<Docente> docentes,
            List<Aula> aulas) {

        List<SeccionGrupoRegular> seccionesGruposRegularesByLetra = letraGrupoRegular.getSeccionesGruposRegulares();
        //System.out.println("Revisando " + seccionesGruposRegularesByLetra.size() + " secciones regulares de " + letraGrupoRegular.getLetra());
        Map<Long, Aula> mapAulas = new LinkedHashMap();
        for (SeccionGrupoRegular seccionGR : seccionesGruposRegularesByLetra) {
            Aula aula = seccionGR.getAula();
            if (aula != null) {
                mapAulas.put(aula.getId(), aula);
            }
        }

        //validar conflicto alumno
        boolean alumnoConflicto = false;

        //  MATRICULAS_BY_SEC:
        //for (Alumno alumno : alumnos) {
        for (SeccionGrupoRegular seccionGrupoRegular : seccionesGruposRegularesByLetra) {
            int alumnosCruce = 0;
            if (seccionGrupoRegular.getAlumnosGruposRegulares() != null) {
                for (AlumnoGrupoRegular alumnoGR : seccionGrupoRegular.getAlumnosGruposRegulares()) {
                    for (Alumno alumno : alumnos) {
                        if (alumno.equals(alumnoGR.getAlumno())) {
                            rolExamenesLogger.cruceAlumno(alumno, letraGrupoRegular, seccionGrupoRegular.getSeccion());
                            alumnosCruce++;
                        }
                    }
                }
            }
            if (alumnosCruce > 0) {
                alumnoConflicto = true;

                System.out.println("\tCruce alumnos seccion-reg:"
                        + seccionGrupoRegular.getSeccion().getId() + " "
                        + letraGrupoRegular.getLetra() + " - alumnos:" + alumnosCruce);
            }
        }
        //}

        //validar conflicto docentes
        boolean docenteConflicto = false;
//        for (Docente docente : docentes) {
//            SeccionGrupoRegular seccionGrupoRegularWithDocente = seccionesGruposRegularesByLetra.stream()
//                    .filter(x -> x.getDocente().equals(docente)).findFirst().orElse(null);
//            if (seccionGrupoRegularWithDocente != null) {
//                docenteConflicto = true;
//                rolExamenesLogger.cruceDocente(docente, letraGrupoRegular, seccionGrupoRegularWithDocente.getSeccion());
//                // break;
//            }
//        }

        //valida conflicto aula
        Date fecha = letraGrupoRegular.getGrupoHorasExamen().getFecha();
//        DateTime today = new DateTime(fecha);
//        Date lun = today.withDayOfWeek(1).toDate();
//        Date dom = today.withDayOfWeek(7).toDate();

        List<Aula> aulasCompares = new ArrayList(mapAulas.values());
        //System.out.println("\tTiene " + aulasCompares.size() + " aulas para comparar");

        boolean aulaConConflicto = false;
        for (Aula aula : aulas) {
            if (aula.getId().compareTo(1211L) == 0) {
                logger.debug("");
            }

            for (Aula aulaCompare : aulasCompares) {
                if (aula.getId().compareTo(aulaCompare.getId()) != 0) {
                    continue;
                }
                if (aulaCompare.getHorariosAula().isEmpty()) {
                    //System.out.println("\taula " + aulaCompare.getId() + " no tiene horario-aula");
                    continue;
                }
                //System.out.println("\thorario:");
                for (HorarioAula ha : aulaCompare.getHorariosAula()) {
                    //System.out.print(ha.getDia().getId() + ":" + ha.getHora().getId() + ":" + new DateTime(ha.getFechaInicio()).toString("dd/MM/yyyy") + " - ");
                }
                //System.out.println("");

                /*if (rolExamenesLogger.getAulas() != null) {
                    for (Aula au : rolExamenesLogger.getAulas()) {
                        if (au.getId().compareTo(aula.getId()) == 0) {
                            aulaCompare = au;
                            break;
                        }
                    }
                }*/
                //Aula aulaCompare = this.rolExamenesLogger.getAulas().stream().filter(x -> x.equals(aula)).findFirst().orElse(null);
                //logger.debug("aula {}", aula.getId());
                //if (aulaCompare != null) {
                //System.out.println("comparando-contra: " + fecha);
                for (FechaHoraGrupoExamen fechaHorGru : letraGrupoRegular.getGrupoHorasExamen().getFechasHorasGruposExamen()) {
                    //System.out.print(fechaHorGru.getDia().getId() + "-" + fechaHorGru.getHora().getId() + " / ");
                }
                //System.out.println("");

                for (FechaHoraGrupoExamen fechaHorGru : letraGrupoRegular.getGrupoHorasExamen().getFechasHorasGruposExamen()) {

                    for (HorarioAula ha : aulaCompare.getHorariosAula()) {
                        if (ha.getDia().getId().compareTo(fechaHorGru.getDia().getId()) != 0) {
                            continue;
                        }
                        if (ha.getHora().getId().compareTo(fechaHorGru.getHora().getId()) != 0) {
                            continue;
                        }
                        if (fecha.compareTo(ha.getFechaInicio()) >= 0 && fecha.compareTo(ha.getFechaFin()) <= 0) {
                            aulaConConflicto = true;
                            break;
                        }
                    }

//                    HorarioAula horarioAula = aulaCompare.getHorariosAula()
//                            .stream()
//                            .filter(x -> x.getDia().getId().compareTo(fechaHorGru.getDia().getId()) == 0)
//                            .filter(x -> x.getHora().getId().compareTo(fechaHorGru.getHora().getId()) == 0)
//                            .filter(x -> x.getFechaInicio().compareTo(lun) >= 0)
//                            .filter(x -> x.getFechaInicio().compareTo(dom) <= 0)
//                            .findFirst().orElse(null);
//                    if (horarioAula != null) {
//                        aulaConConflicto = true;
//                        break;
//                        //rolExamenesLogger.cruceAula(aula, letraGrupoRegular, horarioAula.getSeccion());
//                    }
                }
                //}

                /*
            SeccionGrupoRegular seccionGrupoRegularWithAula = seccionesGruposRegularesByLetra.stream()
                    .filter(x -> x.getAula().equals(aula)).findFirst().orElse(null);
            if (seccionGrupoRegularWithAula != null) {
                aulaConConflicto = true;
                rolExamenesLogger.cruceAula(aula, letraGrupoRegular, seccionGrupoRegularWithAula.getSeccion());
                //  break;
            }*/
            }

            Aula aulaCompare = null;

        }

        if (alumnoConflicto || docenteConflicto || aulaConConflicto) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validarGrupoEspecial(GrupoHorasExamen grupoHorasExamen, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        if (grupoHorasExamen == null) {
            return true;
        }
        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, SeccionRolExamenEstadoEnum.ACT);
        // seccionesGrupoEspecial.removeIf(x -> x.getGrupoHorasExamen() == null || !x.getGrupoHorasExamen().equals(grupoHorasExamen));
        this.fillActiveInfoGrupoEspecial(seccionesGrupoEspecial);
        return this.validarGrupoEspecial(seccionesGrupoEspecial, alumnos, docentes, aulas);
    }

    @Override
    public boolean validarGrupoEspecial(
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<Alumno> alumnos,
            List<Docente> docentes,
            List<Aula> aulas) {

        //validar conflicto alumno
        boolean alumnoConflicto = false;
        //  MATRICULAS_BY_SEC:
        for (Alumno alumno : alumnos) {
            for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecial) {
                AlumnoGrupoEspecial alumnoSeccionEspecialFound = seccionGrupoEspecial.getAlumnosGrupoEspecial()
                        .stream().filter(x -> x.getAlumno().getId().equals(alumno.getId())).findFirst().orElse(null);
                if (alumnoSeccionEspecialFound != null) {
                    alumnoConflicto = true;
                    rolExamenesLogger.cruceAlumno(alumno, seccionGrupoEspecial);
                    // break MATRICULAS_BY_SEC;
                }
            }
        }

        //validar conflicto docentes
        boolean docenteConflicto = false;
//        for (Docente docente : docentes) {
//            SeccionGrupoEspecial seccionGrupoEspecialWithDocente = seccionesGrupoEspecial.stream()
//                    .filter(x -> x.getDocente().getId().equals(docente.getId())).findFirst().orElse(null);
//            if (seccionGrupoEspecialWithDocente != null) {
//                docenteConflicto = true;
//                rolExamenesLogger.cruceDocente(docente, seccionGrupoEspecialWithDocente);
//                // break;
//            }
//        }

        //valida conflicto aula
        boolean aulaConConflicto = false;
        for (Aula aula : aulas) {
            SeccionGrupoEspecial seccionGrupoEspecialWithAula = seccionesGrupoEspecial.stream()
                    .filter(x -> x.getAula().getId().equals(aula.getId())).findFirst().orElse(null);
            if (seccionGrupoEspecialWithAula != null) {
                aulaConConflicto = true;
                rolExamenesLogger.cruceAula(aula, seccionGrupoEspecialWithAula);
                //  break;
            }
        }

        if (docenteConflicto || aulaConConflicto || alumnoConflicto) {
            return false;
        }
        return true;
    }

    @Override
    public void fillActiveInfoCursosMasivos(List<CursoMasivoExamen> cursosMasivoLista) {
        List<AulaCursoMasivo> aulasCursosMasivos = aulaCursoMasivoDAO.allByCursosMasivos(cursosMasivoLista);
        List<DocenteCursoMasivo> docentesCursoMasivo = docenteCursoMasivoDAO.allByCursosMasivos(cursosMasivoLista, DocenteRolExamenEstadoEnum.ACT);
        List<AlumnoCursoMasivo> alumnosCursosMasivos = alumnoCursoMasivoDAO.allByCursosMasivos(cursosMasivoLista, AlumnoRolExamenEstadoEnum.ACT);

        Map<Long, List<AulaCursoMasivo>> mapAulaCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", aulasCursosMasivos);
        Map<Long, List<DocenteCursoMasivo>> mapDocenteCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", docentesCursoMasivo);
        Map<Long, List<AlumnoCursoMasivo>> mapAlumnosCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", alumnosCursosMasivos);

        for (CursoMasivoExamen cursoMasivo : cursosMasivoLista) {
            cursoMasivo.setAulasCursosMasivos(TypesUtil.getListNotNull(mapAulaCursoMasivoByCursoMasivo.get(cursoMasivo.getId())));
            cursoMasivo.setDocentesCursosMasivos(TypesUtil.getListNotNull(mapDocenteCursoMasivoByCursoMasivo.get(cursoMasivo.getId())));
            cursoMasivo.setAlumnosCursosMasivos(TypesUtil.getListNotNull(mapAlumnosCursoMasivoByCursoMasivo.get(cursoMasivo.getId())));
        }
    }

    @Override
    public void fillActiveInfoGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial) {
        List<AlumnoGrupoEspecial> alumnosGruposEspeciales = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum.ACT);
        Map<Long, List<AlumnoGrupoEspecial>> mapAlumnosGruposEspecialesBySecGpoEspecial = TypesUtil.convertListToMapList("seccionGrupoEspecial.id", alumnosGruposEspeciales);
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecial) {
            List<AlumnoGrupoEspecial> alumnosGrupoEspecial = TypesUtil.getListNotNull(mapAlumnosGruposEspecialesBySecGpoEspecial.get(seccionGrupoEspecial.getId()));
            seccionGrupoEspecial.setAlumnosGrupoEspecial(alumnosGrupoEspecial);
        }
    }

    @Override
    public void fillActiveInfoLetrasGruposRegulares(List<LetraGrupoRegular> letrasGruposRegulares) {
        //  List<FechaHoraGrupoExamen> fechasHorasExamens = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(gruposHorasExamen);
        List<SeccionGrupoRegular> seccionesGrupoRegular = seccionGrupoRegularDAO.allByLetraGrupoRegularAndEstados(letrasGruposRegulares, SeccionRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoRegular> alumnosGrupoRegular = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionesGrupoRegular, AlumnoRolExamenEstadoEnum.ACT);

        Map<Long, List<SeccionGrupoRegular>> mapSeccionesGpoRegular = TypesUtil.convertListToMapList("letraGrupoRegular.id", seccionesGrupoRegular);
        Map<Long, List<AlumnoGrupoRegular>> mapAlumnosGrupoRegular = TypesUtil.convertListToMapList("seccionGrupoRegular.id", alumnosGrupoRegular);
        //    Map< Long, List<FechaHoraGrupoExamen>> mapFechasHorasExamenes = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHorasExamens);

        List<Seccion> secciones = seccionesGrupoRegular.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        List<Aula> aulas = seccionesGrupoRegular.stream().filter(x -> x.getAula() != null).map(x -> x.getAula()).collect(Collectors.toList());
        List<HorarioAula> horarioAulas = horarioAulaDAO.allByAulas(aulas, secciones);
        Map<Long, List<HorarioAula>> mapHorarioAulas = TypesUtil.convertListToMapList("aula.id", horarioAulas);
        for (Aula aula : aulas) {
            aula.setHorariosAula(TypesUtil.getListNotNull(mapHorarioAulas.get(aula.getId())));
        }

        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            //  List<FechaHoraGrupoExamen> fechasHorasGruposByGrupoHoraExamen = mapFechasHorasExamenes.get(letraGruposRegular.getGrupoHorasExamen().getId());
            //    letraGruposRegular.getGrupoHorasExamen().setFechasHorasGruposExamen(fechasHorasGruposByGrupoHoraExamen);
            //    letraGruposRegular.getGrupoHorasExamen().setSemanaExamen(fechasHorasGruposByGrupoHoraExamen.get(0).getSemanaExamen());

            List<SeccionGrupoRegular> seccionGrupoRegularByLetra = TypesUtil.getListNotNull(mapSeccionesGpoRegular.get(letraGruposRegular.getId()));
            for (SeccionGrupoRegular seccionGrupoRegular : seccionGrupoRegularByLetra) {
                List<AlumnoGrupoRegular> alumnosGrupoRegularBySeccionGpoReg = mapAlumnosGrupoRegular.get(seccionGrupoRegular.getId());
                seccionGrupoRegular.setAlumnosGruposRegulares(alumnosGrupoRegularBySeccionGpoReg);
            }
            //letraGruposRegular.setSeccionesGruposRegulares(seccionesGrupoRegular);
            letraGruposRegular.setSeccionesGruposRegulares(seccionGrupoRegularByLetra);
        }
    }

    @Override
    public boolean validarCursosMasivos(GrupoHorasExamen grupoHorasExamen, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        List<CursoMasivoExamen> cursosMasivosByRolExamen = cursoMasivoExamenDAO.allByGrupoHorasExamen(grupoHorasExamen, EstadoCursoMasivoEnum.ACT);
        this.fillActiveInfoCursosMasivos(cursosMasivosByRolExamen);
        return validarCursosMasivos(cursosMasivosByRolExamen, docentes, aulas, alumnos);
    }

    @Override
    public boolean validarCursosMasivos(RolExamenes rolExamenes,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        List<CursoMasivoExamen> cursosMasivosByRolExamen = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        this.fillActiveInfoCursosMasivos(cursosMasivosByRolExamen);
        return validarCursosMasivos(cursosMasivosByRolExamen, docentes, aulas, alumnos);
    }

    @Override
    public boolean validarCursosMasivos(
            List<CursoMasivoExamen> cursosMasivos,
            List<Docente> docentes,
            List<Aula> aulas,
            List<Alumno> alumnos) {

        if (cursosMasivos.isEmpty()) {
            return true;
        }

        boolean docenteConflicto = false;
        boolean aulaConConflicto = false;
        boolean alumnoConflicto = false;

        for (CursoMasivoExamen cursoMasivoByRolExamen : cursosMasivos) {

            //validar conflicto docentes
            /*
            for (Docente docente : docentes) {
                DocenteCursoMasivo docenteCursoMasivo = cursoMasivoByRolExamen.getDocentesCursosMasivos().stream()
                        .filter(x -> x.getDocente().equals(docente))
                        .findFirst().orElse(null);
                if (docenteCursoMasivo != null) {
                    docenteConflicto = true;
                    rolExamenesLogger.cruceDocente(docente, cursoMasivoByRolExamen.getCurso());
                    //  break;
                }
            }
            // **/
            //Validar Aula
            if (cursoMasivoByRolExamen.getAulasCursosMasivos() != null && !cursoMasivoByRolExamen.getAulasCursosMasivos().isEmpty()) {
                for (Aula aula : aulas) {
                    AulaCursoMasivo aulaCursoMasivo = cursoMasivoByRolExamen.getAulasCursosMasivos().stream().
                            filter(x -> x.getAula().equals(aula)).findFirst().orElse(null);
                    if (aulaCursoMasivo != null) {
                        System.out.println("\tCruce aula curso-masivo "
                                + cursoMasivoByRolExamen.getId() + ":"
                                + cursoMasivoByRolExamen.getGrupoHorasExamen().getGrupoHoras().getCodigo() + " - aula:" + aula.getId());
                        aulaConConflicto = true;
                        rolExamenesLogger.cruceAula(aula, cursoMasivoByRolExamen.getCurso());
                    }
                }
            }

            int alumnosCruces = 0;
            for (Alumno alumno : alumnos) {
                AlumnoCursoMasivo alumnoCursoMasivo = cursoMasivoByRolExamen.getAlumnosCursosMasivos().stream()
                        .filter(x -> x.getAlumno().equals(alumno))
                        .findFirst().orElse(null);
                if (alumnoCursoMasivo != null) {
                    alumnosCruces++;
                    alumnoConflicto = true;
                    rolExamenesLogger.cruceAlumno(alumno, cursoMasivoByRolExamen.getCurso());
                }
            }

            if (alumnosCruces > 0) {
                System.out.println("\tCruce alumnos curso-masivo "
                        + cursoMasivoByRolExamen.getId() + ":"
                        + cursoMasivoByRolExamen.getGrupoHorasExamen().getGrupoHoras().getCodigo() + " - alumnos:" + alumnosCruces);
            }

        }
        if (docenteConflicto || aulaConConflicto || alumnoConflicto) {
            return false;
        }
        return true;
    }

    private void crearSeccionGrupoRegular(Seccion seccion,
            LetraGrupoRegular letraGrupoRegular,
            List<MatriculaSeccion> matriculadosPorSeccion,
            DataSessionPivot ds) {

        SeccionGrupoRegular seccionGrupoRegular = this.crearObjectSeccionGrupoRegular(seccion, letraGrupoRegular, ds);
        Aula aulaSeccionLogger = this.rolExamenesLogger.getAulas()
                .stream().filter(x -> x.equals(seccion.getAula())).findFirst().orElse(null);

        for (FechaHoraGrupoExamen fechaHoraGrupoExamen : letraGrupoRegular.getGrupoHorasExamen().getFechasHorasGruposExamen()) {
            HorarioAula horarioAula = new HorarioAula(fechaHoraGrupoExamen, seccion);
            horarioAula.setSeccionGrupoRegular(seccionGrupoRegular);
            horarioAula.setRolExamenes(letraGrupoRegular.getRolExamenes());
            //  horarioAulaDAO.save(horarioAula);
            aulaSeccionLogger.getHorariosAula().add(horarioAula.clone());
            if (seccionGrupoRegular.getHorariosAula() == null) {
                seccionGrupoRegular.setHorariosAula(new ArrayList<>());
            }
            seccionGrupoRegular.getHorariosAula().add(horarioAula);
        }
        letraGrupoRegular.getSeccionesGruposRegulares().add(seccionGrupoRegular);

        GrupoRegularExamen grupoRegularExamen = letraGrupoRegular.getGruposRegularesExamenes()
                .stream().filter(x -> x.getGrupoHoras().equals(seccion.getGrupoHoras()))
                .findFirst().orElse(null);

        if (grupoRegularExamen == null) {
            grupoRegularExamen = new GrupoRegularExamen();
            grupoRegularExamen.setEstadoEnum(GrupoHorasRolExamenEstadoEnum.ACT);
            grupoRegularExamen.setGrupoHoras(seccion.getGrupoHoras());
            grupoRegularExamen.setLetraGrupoRegular(letraGrupoRegular);
            grupoRegularExamen.setFechaRegistro(ds.getFechaAccionAudit());
            grupoRegularExamen.setUserRegistro(ds.getUsuario());
            letraGrupoRegular.getGruposRegularesExamenes().add(grupoRegularExamen);
        }

        matriculadosPorSeccion.forEach(x -> {
            AlumnoGrupoRegular alumnoGrupoRegular = this.crearObjectAlumnoGrupoRegular(x.getMatriculaResumen().getAlumno(), seccionGrupoRegular, ds);
            seccionGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
        });
    }

    @Override
    public SeccionGrupoRegular crearObjectSeccionGrupoRegular(Seccion seccion, LetraGrupoRegular letraGrupoRegular, DataSessionPivot ds) {
        SeccionGrupoRegular seccionGrupoRegular = new SeccionGrupoRegular();
        seccionGrupoRegular.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
        seccionGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
        seccionGrupoRegular.setSeccion(seccion);
        seccionGrupoRegular.setAula(seccion.getAula());
        seccionGrupoRegular.setDocente(seccion.getDocenteSeccion().get(0).getDocente());
        seccionGrupoRegular.setFechaRegistro(ds.getFechaAccionAudit());
        seccionGrupoRegular.setUserRegistro(ds.getUsuario());
        seccionGrupoRegular.setAlumnosGruposRegulares(new ArrayList<>());
        return seccionGrupoRegular;
    }

    @Override
    public AlumnoGrupoRegular crearObjectAlumnoGrupoRegular(Alumno alumno, SeccionGrupoRegular seccionGrupoRegular, DataSessionPivot ds) {
        AlumnoGrupoRegular alumnoGrupoRegular = new AlumnoGrupoRegular();
        alumnoGrupoRegular.setAlumno(alumno);
        alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
        alumnoGrupoRegular.setFechaRegistro(ds.getFechaAccionAudit());
        alumnoGrupoRegular.setSeccionGrupoRegular(seccionGrupoRegular);
        //   alumnoGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
        alumnoGrupoRegular.setUserRegistro(ds.getUsuario());
        return alumnoGrupoRegular;
    }

    @Override
    public void validarSituacionBeforeOr(String accion, String situacion, Boolean... or) {
        String msg = String.format("Solo se puede %s antes de generar %s", accion, situacion);
        Assert.isTrue(Arrays.asList(or).contains(true), msg);
    }

    @Override
    public void validarSituacion(String accion, String situacion, Boolean... or) {
        String msg = String.format("Solo se puede %s al configurar %s", accion, situacion);
        Assert.isTrue(Arrays.asList(or).contains(true), msg);
    }

    @Override
    public RolExamenesLogger validacionActivarDocente(GrupoHorasExamen grupoHorasExamen, Docente docente) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.iniciarGeneric();

        List<DocenteCursoMasivo> docentesCursosMasivos = new ArrayList<>();
        List<SeccionGrupoRegular> seccionGrupoRegulares = new ArrayList<>();
        List<SeccionGrupoEspecial> seccionGrupoEspeciales = new ArrayList<>();
        if (grupoHorasExamen != null) {
            docentesCursosMasivos = docenteCursoMasivoDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, DocenteRolExamenEstadoEnum.ACT);
            seccionGrupoRegulares = seccionGrupoRegularDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, SeccionRolExamenEstadoEnum.ACT);
            seccionGrupoEspeciales = seccionGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, SeccionRolExamenEstadoEnum.ACT);
        }
        List<DocenteCursoMasivo> docentesCursosMasivosFound = docentesCursosMasivos.stream()
                .filter(x -> x.getDocente().equals(docente))
                .collect(Collectors.toList());

        List<SeccionGrupoRegular> seccionGrupoRegularesFound = seccionGrupoRegulares.stream()
                .filter(x -> x.getDocente().equals(docente))
                .collect(Collectors.toList());

        List<SeccionGrupoEspecial> seccionGrupoEspecial = seccionGrupoEspeciales.stream()
                .filter(x -> x.getDocente().equals(docente))
                .collect(Collectors.toList());

        for (DocenteCursoMasivo docenteCursoMasivoEach : docentesCursosMasivosFound) {
            rolExamenesLogger.cruceDocente(docente, docenteCursoMasivoEach.getCursoMasivoExamen().getCurso());
        }
        for (SeccionGrupoRegular seccionGrupoRegularEach : seccionGrupoRegularesFound) {
            rolExamenesLogger.cruceDocente(docente, seccionGrupoRegularEach.getLetraGrupoRegular(), seccionGrupoRegularEach.getSeccion());
        }
        for (SeccionGrupoEspecial seccionGrupoEspecialEach : seccionGrupoEspecial) {
            rolExamenesLogger.cruceDocente(docente, seccionGrupoEspecialEach);
        }
        return rolExamenesLogger;
    }

    @Override
    public RolExamenesLogger validacionActivarAlumno(GrupoHorasExamen grupoHorasExamen, Alumno alumno) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.iniciarGeneric();

        List<AlumnoCursoMasivo> alumnosCursosMasivos = alumnoCursoMasivoDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoRegular> alumnosGrupoRegular = alumnoGrupoRegularDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoEspecial> alumnosGrupoEspecial = alumnoGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);

        List<AlumnoCursoMasivo> alumnosCursosMasivosConflicts = alumnosCursosMasivos.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());
        List<AlumnoGrupoRegular> alumnosGrupoRegularConflicts = alumnosGrupoRegular.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());
        List<AlumnoGrupoEspecial> alumnosSeccionEspecialConflics = alumnosGrupoEspecial.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());

        for (AlumnoCursoMasivo aCursoMasivo : alumnosCursosMasivosConflicts) {
            rolExamenesLogger.cruceAlumno(alumno, aCursoMasivo.getCursoMasivoExamen().getCurso());
        }

        for (AlumnoGrupoRegular aGrupoRegular : alumnosGrupoRegularConflicts) {
            rolExamenesLogger.cruceAlumno(
                    alumno,
                    aGrupoRegular.getSeccionGrupoRegular().getLetraGrupoRegular(),
                    aGrupoRegular.getSeccionGrupoRegular().getSeccion());
        }
        for (AlumnoGrupoEspecial aGrupoEspecial : alumnosSeccionEspecialConflics) {
            rolExamenesLogger.cruceAlumno(alumno, aGrupoEspecial.getSeccionGrupoEspecial());
        }

        return rolExamenesLogger;
    }

    @Override
    public List<Aula> allAulasOeraWithHorarioByRolExamenes(RolExamenes rolExamenes, OficinaEnum oficinaEnum) {
        CicloAcademico cicloAcademicoRol = rolExamenes.getEventoCicloAcademico().getCicloAcademico();
        List<SemanaExamen> semanasExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        List<HorarioAula> horariosAulasFull = new ArrayList<>();
        for (SemanaExamen semanaExamen : semanasExamen) {
            List<HorarioAula> horariosAulasBySemana = horarioAulaDAO.allOcupadasByCicloAndSemanaExamen(cicloAcademicoRol, semanaExamen);
            horariosAulasFull.addAll(horariosAulasBySemana);
        }

        List<Aula> aulas = null;
        if (oficinaEnum != null) {
            aulas = aulaDAO.allByOficinaSupervisora(oficinaEnum, EstadoEnum.ACT);
        } else {
            aulas = aulaDAO.allByEstado(EstadoEnum.ACT);
        }

        //      List<HorarioAula> horariosAula = horarioAulaDAO.allByCicloOrderByDiaHora(rolExamenes.getEventoCicloAcademico().getCicloAcademico()); //o, EstadoHorarioAulaEnum.ACT
        Map<Long, List<HorarioAula>> mapHorariosAulasByAula = TypesUtil.convertListToMapList("aula.id", horariosAulasFull);
        for (Aula aula : aulas) {
            List<HorarioAula> horariosAulaByAula = mapHorariosAulasByAula.get(aula.getId());
            if (horariosAulaByAula != null) {
                aula.setHorariosAula(horariosAulaByAula);
            } else {
                aula.setHorariosAula(new ArrayList<>());
            }
        }

        List<Aula> aulasConAforo = aulas.stream().filter(x -> x.getAforo() != null).collect(Collectors.toList());
        List<Aula> aulasSinAforo = aulas.stream().filter(x -> x.getAforo() == null).collect(Collectors.toList());
        Collections.sort(aulasConAforo, (p1, p2) -> p1.getAforo().compareTo(p2.getAforo()));
        aulasConAforo.addAll(aulasSinAforo);
//        List<String> aulasIds = aulasConAforo.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
//        logger.debug(String.join(",", aulasIds));
        return aulasConAforo;
    }

    public void restoreHorariosAulas(RolExamenes rolExamenes,
            Seccion seccion,
            Aula aula,
            TipoGrupoRolExamenesEnum tipoGrupoRolExamenesEnum,
            CursoMasivoExamen cursoMasivoExamen) {
        CicloAcademico cicloAcademico = rolExamenes.getEventoCicloAcademico().getCicloAcademico();
        List<SemanaExamen> semanas = semanaExamenDAO.allByRolExamenes(rolExamenes);

        final RolExamenes firstRolExamen = rolexamenesDAO.findByCicloAndEstadoAndEventoAcademico(cicloAcademico, null, EventoAcademicoEnum.EXAMEN_PARC);
        EventoCicloAcademico dictadoClases = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_PRE);

        List<HorarioAula> horariosAulasByCiclo = horarioAulaDAO.allForRolExamenesByCicloAcademico(rolExamenes.getEventoCicloAcademico().getCicloAcademico());
        if (seccion != null && aula != null) {
            horariosAulasByCiclo.removeIf(x -> !x.getSeccion().equals(seccion));
            horariosAulasByCiclo.removeIf(x -> !x.getAula().equals(aula));
        }
        if (rolExamenes.getEventoCicloAcademico().getEventoAcademico().isExamenFinal()) {
            horariosAulasByCiclo.removeIf(x
                    -> x.getFechaFinDateTime().toLocalDate().isBefore(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate())
                    || x.getFechaFinDateTime().toLocalDate().isEqual(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate()));
        }
        for (SemanaExamen semana : semanas) {
            List<HorarioAula> horariosAulasFull = horarioAulaDAO.allByCicloAndSemanaExamenLimitByHours(rolExamenes.getEventoCicloAcademico(), semana);
            if (rolExamenes.getEventoCicloAcademico().getEventoAcademico().isExamenFinal()) {
                horariosAulasFull.removeIf(x
                        -> x.getFechaFinDateTime().toLocalDate().isBefore(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate())
                        || x.getFechaFinDateTime().toLocalDate().isEqual(firstRolExamen.getEventoCicloAcademico().getFechaFinDateTime().toLocalDate()));
            }
            Map<Long, List<HorarioAula>> mapHorariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horariosAulasFull);
            for (Map.Entry<Long, List<HorarioAula>> entry : mapHorariosBySeccion.entrySet()) {
                Seccion iSeccion = new Seccion(entry.getKey());
                List<HorarioAula> horariosAulasBySeccion = entry.getValue();

                Map<Long, List<HorarioAula>> mapHorariosBySeccionAndDia = TypesUtil.convertListToMapList("dia.id", horariosAulasBySeccion);
                for (Map.Entry<Long, List<HorarioAula>> entry1 : mapHorariosBySeccionAndDia.entrySet()) {
                    Dia dia = new Dia(entry1.getKey());
                    //  List<HorarioAula> horariosAulasBySeccionAndDia = entry1.getValue();
                    List<HorarioAula> horariosAulasBySeccionAndDia = horariosAulasByCiclo.stream()
                            .filter(x -> x.getSeccion().equals(iSeccion))
                            .filter(x -> x.getDia().equals(dia))
                            .filter(x -> !x.isTipoExamen())
                            .collect(Collectors.toList());
                    for (HorarioAula horarioAula : horariosAulasBySeccionAndDia) {
                        if (horarioAula.getFechaFinDateTime().plusDays(1).toLocalDate()
                                .equals(semana.getFechaInicioDateTime().toLocalDate())) {
                            horarioAula.setFechaFin(dictadoClases.getFechaFin());
                            horarioAulaDAO.update(horarioAula);
                        }
                        if (horarioAula.getFechaInicioDateTime().plusDays(-1).toLocalDate()
                                .equals(semana.getFechaFinDateTime().toLocalDate())) {
                            horarioAulaDAO.delete(horarioAula);
                        }
                    }

                }
            }
            //to delete
            if (TipoGrupoRolExamenesEnum.CUR_MAS.equals(tipoGrupoRolExamenesEnum)) {
                List<HorarioAula> horariosAulasByCicloOcupadas = horarioAulaDAO.allByCursoMasivo(cursoMasivoExamen);
                for (HorarioAula horariosAulasByCicloOcupada : horariosAulasByCicloOcupadas) {
                    horarioAulaDAO.delete(horariosAulasByCicloOcupada);

                }
            }
        }

    }

}
