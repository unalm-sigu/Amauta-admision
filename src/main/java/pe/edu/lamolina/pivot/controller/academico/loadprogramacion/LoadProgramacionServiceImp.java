package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoCursoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.EstadoCivil;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.EstadoCivilDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.general.UbicacionDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class LoadProgramacionServiceImp implements LoadProgramacionService {

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    ProgDataService progDataService;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    EstadoCivilDAO estadoCivilDAO;
    @Autowired
    PaisDAO paisDAO;
    @Autowired
    UbicacionDAO ubicacionDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;
    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    TipoRepitenciaDAO tipoRepitenciaDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    VisorLoadProgramacion visor;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Map<String, String> loadArchivosHorario(MultipartFile[] files) {
        List<String> nombreFiles = Arrays.asList("GruposSecciones.xls", "Secciones.xls", "Personas.xls", "Docentes.xls", "DocentesSecciones.xls", "Alumnos.xls", "Matricula.xls", "HorarioGrupos.xls", "HorarioSecciones.xls", "Cursos.xls");

        Map<String, String> mapRutaFiles = new HashMap();
        if (files.length != 10) {
            throw new PhobosException("Deben ser 10 archivos de carga");
        }
        for (MultipartFile file : files) {
            String nombre = file.getOriginalFilename();
            mapRutaFiles.put(nombre, nombre);
        }
        for (String name : nombreFiles) {
            String nfile = mapRutaFiles.get(name);
            if (nfile == null) {
                throw new PhobosException("No está enviando el archivo " + name);
            }
        }

        for (MultipartFile file : files) {
            String nombre = file.getOriginalFilename();
            String ruta = saveFile(file);
            mapRutaFiles.put(nombre, ruta);
        }
        return mapRutaFiles;
    }

    @Async
    @Override
    @Transactional
    public void inicioProcesarArchivos(Map<String, String> rutasFiles, CicloAcademico ciclo, DataSessionPivot ds) {
        try {
            procesarArchivos(rutasFiles, ciclo, ds);
        } catch (Exception e) {
            e.printStackTrace();
            visor.agregarLog("error", "error", "error: " + e.getLocalizedMessage(), false, "error");
        }
    }

    private void procesarArchivos(Map<String, String> mapRutaFiles, CicloAcademico ciclo, DataSessionPivot ds) {

        visor.iniciar();
        logger.debug("CICLO  {} {} {} ", ciclo.getId(), ciclo.getYear(), ciclo.getNumeroCiclo());
        visor.agregarLog("ciclo", "inicio", "Ciclo " + ciclo.getId() + " " + ciclo.getYear() + " " + ciclo.getNumeroCiclo(), false, "info");

        String rutaFileGpoSecciones = mapRutaFiles.get("GruposSecciones.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileGpoSecciones: " + rutaFileGpoSecciones, false, "info");

        String rutaFileSecciones = mapRutaFiles.get("Secciones.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileSecciones: " + rutaFileSecciones, false, "info");

        String rutaFilePersonas = mapRutaFiles.get("Personas.xls");
        visor.agregarLog("inicio", "inicio", "rutaFilePersonas: " + rutaFilePersonas, false, "info");

        String rutaFileProfes = mapRutaFiles.get("Docentes.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileProfes: " + rutaFileProfes, false, "info");

        String rutaFileProfeSecciones = mapRutaFiles.get("DocentesSecciones.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileProfeSecciones: " + rutaFileProfeSecciones, false, "info");

        String rutaFileAlumno = mapRutaFiles.get("Alumnos.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileAlumno: " + rutaFileAlumno, false, "info");

        String rutaFileAlumnoSecciones = mapRutaFiles.get("Matricula.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileAlumnoSecciones: " + rutaFileAlumnoSecciones, false, "info");

        String rutaFileHorarioGrupos = mapRutaFiles.get("HorarioGrupos.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileHorarioGrupos: " + rutaFileHorarioGrupos, false, "info");

        String rutaFileHorarioSecciones = mapRutaFiles.get("HorarioSecciones.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileHorarioSecciones: " + rutaFileHorarioSecciones, false, "info");

        String rutaFileCursos = mapRutaFiles.get("Cursos.xls");
        visor.agregarLog("inicio", "inicio", "rutaFileCursos: " + rutaFileCursos, false, "info");

        List<GrupoSeccion> gruposSecciones = crearGruposSecciones(rutaFileGpoSecciones);
        visor.inicializar("gpoSecc", gruposSecciones.size());

        List<Seccion> secciones = crearSecciones(rutaFileSecciones);
        visor.inicializar("secc", secciones.size());

        List<Persona> personas = crearPersonas(rutaFilePersonas);
        visor.inicializar("per", personas.size());

        List<Docente> docentes = crearDocentes(rutaFileProfes);
        visor.inicializar("doc", docentes.size());

        List<DocenteSeccion> docentesSecciones = crearDocenteSecciones(rutaFileProfeSecciones);
        visor.inicializar("docSecc", docentesSecciones.size());

        List<Alumno> alumnos = crearAlumnos(rutaFileAlumno);
        visor.inicializar("alu", alumnos.size());

        List<MatriculaSeccion> matriculaSecciones = crearMatriculasSecciones(rutaFileAlumnoSecciones);
        visor.inicializar("aluSecc", matriculaSecciones.size());

        List<Persona> personasDB = personaDAO.all();
        Map<String, List<Persona>> mapKeyPersonas = TypesUtil.convertListToMapList("key", personasDB);
        Map<Long, Persona> mapIdPersonas = TypesUtil.convertListToMap("id", personasDB);
        Map<String, Persona> mapDNIPersonas = new LinkedHashMap();
        for (Persona persona : personasDB) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            if (persona.getTipoDocumento() != null && persona.getNumeroDocIdentidad() != null) {
                mapDNIPersonas.put(persona.getIdentificacion(), persona);
            }
        }

        Map<String, AlumnoBlocked> mapBloqueados = new LinkedHashMap();
        progDataService.revisarBloqueados(mapBloqueados);

        long t1 = System.currentTimeMillis();
        logger.debug("savePersonas");
        this.savePersonas(personas, mapKeyPersonas, mapDNIPersonas, ds);
        long t2 = System.currentTimeMillis();
        logger.debug("\tsavePersonas ejecutado en {} mseg", (t2 - t1));

        for (Persona persona : personas) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            if (mapIdPersonas.get(persona.getId()) == null) {
                mapIdPersonas.put(persona.getId(), persona);
            }
        }

        List<Alumno> alumnosDB = alumnoDAO.all();
        Map<String, Alumno> mapAlumnos = TypesUtil.convertListToMap("codigo", alumnosDB);
        for (Alumno alumno : alumnosDB) {
            Persona persona = mapIdPersonas.get(alumno.getPersona().getId());
            if (persona != null) {
                alumno.setPersona(persona);
            }
        }
        List<SituacionAcademica> situaciones = situacionAcademicaDAO.all();
        Map<String, SituacionAcademica> mapSituaciones = TypesUtil.convertListToMap("codigo", situaciones);

        t1 = System.currentTimeMillis();
        Map<String, Curso> mapCursos = cursoDAO.all().stream().filter(x -> x.getCodigo() != null).collect(Collectors.toMap(x -> x.getCodigo(), x -> x, (a, b) -> a));
        Map<String, DepartamentoAcademico> mapDepartamentosAcademicos = departamentoAcademicoDAO.all().stream().filter(x -> x.getCodigo() != null).collect(Collectors.toMap(x -> x.getCodigo(), x -> x, (a, b) -> a));
        t2 = System.currentTimeMillis();

        crearCursos(rutaFileCursos, mapCursos, mapDepartamentosAcademicos);

        t1 = System.currentTimeMillis();
        logger.debug("saveAlumnos");
        this.saveAlumnos(alumnos, mapKeyPersonas, mapDNIPersonas, mapIdPersonas, mapAlumnos, mapSituaciones, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\tsaveAlumnos ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataDocentes");
        Map<String, Docente> mapDocentes = this.saveDocentes(docentes, mapKeyPersonas, mapDNIPersonas, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataDocentes ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisionPreviaGpoSecciones");
        progDataService.revisionPreviaGpoSecciones(gruposSecciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisionPreviaGpoSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("codigo2NullGpoSeccion");
        progDataService.codigo2NullGpoSeccion(ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\tcodigo2NullGpoSeccion ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("codigo2NullSeccion");
        progDataService.codigo2NullSeccion(ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\tcodigo2NullSeccion ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataGpoSecciones");
        Map<String, GrupoSeccion> mapGpoSecciones = progDataService.loadDataGpoSecciones(gruposSecciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataGpoSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataSecciones");
        Map<String, Seccion> mapSecciones = progDataService.loadDataSecciones(secciones, ciclo, mapGpoSecciones, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataDocentesSecciones");
        Map<String, DocenteSeccion> mapDocenteSecciones = progDataService.loadDataDocentesSecciones(docentesSecciones, mapSecciones, mapDocentes, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataDocentesSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarDocenteSecciones");
        progDataService.revisarDocenteSecciones(mapDocenteSecciones, ciclo, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarDocenteSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataMatriculados");
        Map<String, MatriculaResumen> mapResumenes = loadDataMatriculados(matriculaSecciones, mapSecciones, ciclo, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataMatriculados ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarAlumnosMatriculados");
        revisarAlumnosMatriculados(ciclo, mapResumenes, mapBloqueados);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarAlumnosMatriculados ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarSecciones");
        progDataService.revisarSecciones(secciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarSecciones");
        progDataService.revisarGrupoSecciones(gruposSecciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("Obtener mapas para horarios");
        Map<Integer, Dia> mapDias = diaDAO.all().stream().collect(Collectors.toMap(x -> x.getNumeroDia(), x -> x));
        Map<Integer, Hora> mapHoras = horaDAO.all().stream().collect(Collectors.toMap(x -> x.getNumero(), x -> x));
        Map<String, GrupoHoras> mapGrupos = grupoHorasDAO.all().stream().collect(Collectors.toMap(x -> x.getCodigo(), x -> x));
        Map<String, Aula> mapAulas = aulaDAO.all().stream().collect(Collectors.toMap(x -> x.getCodigo(), x -> x));
        t2 = System.currentTimeMillis();
        logger.debug("\tObtener mapas para horarios ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("horariosSeccion");
        List<HorarioSeccion> horariosSeccion = crearHorarioSecciones(rutaFileHorarioSecciones, mapSecciones, mapDias, mapHoras, mapAulas, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\thorariosSeccion ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("horariosGrupo");
        List<DiaHoraGrupo> horariosGrupo = crearHorarioGrupos(rutaFileHorarioGrupos, mapDias, mapHoras, mapGrupos, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\thorariosGrupo ejecutado en {} mseg", (t2 - t1));
        visor.agregarLog("fin", "fin", "Carga finalizada", false, "fin");

    }

    private Map<String, Docente> saveDocentes(
            List<Docente> docentes,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
        Map<String, Docente> mapDocentes = new LinkedHashMap();

        List<TipoDocIdentidad> tiposDoc = tipoDocIdentidadDAO.all();
        Map<String, TipoDocIdentidad> mapTiposDoc = MapUtil.storeItems("simbolo", tiposDoc);
        List<EstadoCivil> estadosCiviles = estadoCivilDAO.all();
        Map<String, EstadoCivil> mapEstadoCivil = MapUtil.storeItems("codigo", estadosCiviles);
        List<Pais> paises = paisDAO.all();
        Map<String, Pais> mapPaises = MapUtil.storeItems("codigo", paises);
        List<Ubicacion> ubigeos = ubicacionDAO.all();
        Map<String, Ubicacion> mapUbicacion = MapUtil.storeItems("codigo", ubigeos);

        List<DepartamentoAcademico> dptos = departamentoAcademicoDAO.all();
        Map<String, DepartamentoAcademico> mapDptos = MapUtil.storeItems("codigo", dptos);

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);

        long loop = 1;
        visor.inicializar("loadDocente", docentes.size());
        for (Docente docente : docentes) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            logger.debug("Guardando docente {} de {}", loop, docentes.size());
            Docente profe = mapDocentes.get(docente.getCodigo());
            if (profe != null) {
                continue;
            }

            Persona persona = docente.getPersona();
            loadInfoPersona(persona, mapTiposDoc, mapEstadoCivil, mapPaises, mapUbicacion);
            List<Persona> personasVinculadas = progDataService.allPersonasByPer(persona, mapKeyPersonas, mapDNIPersonas, ds);
            persona = progDataService.savePersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            String emailCia = progDataService.extraerEmailCompania(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            Persona perso = progDataService.extraerDocumentoIdentidad(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            progDataService.changeDocumentoIdentidad(persona, personasVinculadas, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, mapKeyPersonas, mapDNIPersonas, ds);

            docente.setPersona(persona);
            docente = progDataService.saveDocente(docente, modalidad, mapDptos, ds);
            mapDocentes.put(docente.getCodigo(), docente);
            loop++;
        }

        progDataService.anularDocentes(mapDocentes, modalidad, ds);

        return mapDocentes;
    }

    private void saveAlumnos(
            List<Alumno> alumnos,
            Map<String, List<Persona>> mapKeyPersonas,
            Map<String, Persona> mapDNIPersonas,
            Map<Long, Persona> mapIdPersonas,
            Map<String, Alumno> mapAlumnos,
            Map<String, SituacionAcademica> mapSituaciones, DataSessionPivot ds) {

        List<TipoDocIdentidad> tiposDoc = tipoDocIdentidadDAO.all();
        Map<String, TipoDocIdentidad> mapTiposDoc = MapUtil.storeItems("simbolo", tiposDoc);
        List<EstadoCivil> estadosCiviles = estadoCivilDAO.all();
        Map<String, EstadoCivil> mapEstadoCivil = MapUtil.storeItems("codigo", estadosCiviles);
        List<Pais> paises = paisDAO.all();
        Map<String, Pais> mapPaises = MapUtil.storeItems("codigo", paises);
        List<Ubicacion> ubigeos = ubicacionDAO.all();
        Map<String, Ubicacion> mapUbicacion = MapUtil.storeItems("codigo", ubigeos);

        long loop = 1;
        for (Alumno alumno : alumnos) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            logger.debug("Guardando alumno {} de {}", loop, alumnos.size());
            Persona persona = alumno.getPersona();
            loadInfoPersona(persona, mapTiposDoc, mapEstadoCivil, mapPaises, mapUbicacion);
            List<Persona> personasVinculadas = progDataService.allPersonasByPer(persona, mapKeyPersonas, mapDNIPersonas, ds);
            Persona perxoma = null;
            try {
                System.out.println("persona = progDataService.savePersona >>> " + persona.getId() + " :::: " + persona.getKey());
                perxoma = progDataService.savePersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            } catch (Exception e) {
                if (perxoma != null) {
                    System.out.println("cayo 111 :::: " + perxoma.getId() + " :::: " + perxoma.getKey());
                }
                System.out.println("cayo 222 :::: " + persona.getId() + " :::: " + persona.getKey());
                e.printStackTrace();
                throw new PhobosException(e.getLocalizedMessage());
            }

            String emailCia = progDataService.extraerEmailCompania(perxoma, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            logger.debug("\temail-cia {}", emailCia);
            Persona perso = progDataService.extraerDocumentoIdentidad(perxoma, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            progDataService.changeDocumentoIdentidad(perxoma, personasVinculadas, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, mapKeyPersonas, mapDNIPersonas, ds);

            if (mapIdPersonas.get(perxoma.getId()) == null) {
                mapIdPersonas.put(perxoma.getId(), persona);
            }

            alumno.setPersona(perxoma);
            progDataService.saveAlumno(alumno, mapIdPersonas, mapAlumnos, mapSituaciones, ds);
            loop++;
        }

    }

    private void savePersonas(List<Persona> personas, Map<String, List<Persona>> mapKeyPersonas, Map<String, Persona> mapDNIPersonas, DataSessionPivot ds) {
        List<TipoDocIdentidad> tiposDoc = tipoDocIdentidadDAO.all();
        Map<String, TipoDocIdentidad> mapTiposDoc = MapUtil.storeItems("simbolo", tiposDoc);
        List<EstadoCivil> estadosCiviles = estadoCivilDAO.all();
        Map<String, EstadoCivil> mapEstadoCivil = MapUtil.storeItems("codigo", estadosCiviles);
        List<Pais> paises = paisDAO.all();
        Map<String, Pais> mapPaises = MapUtil.storeItems("codigo", paises);
        List<Ubicacion> ubigeos = ubicacionDAO.all();
        Map<String, Ubicacion> mapUbicacion = MapUtil.storeItems("codigo", ubigeos);

        long loop = 1;
        for (Persona persona : personas) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            logger.debug("Guardando persona {} de {}", loop, personas.size());
            visor.agregarLog("per", "savePersonas", "Guardando persona " + persona.getKey(), true, "info");
            loadInfoPersona(persona, mapTiposDoc, mapEstadoCivil, mapPaises, mapUbicacion);
            List<Persona> personasVinculadas = progDataService.allPersonasByPer(persona, mapKeyPersonas, mapDNIPersonas, ds);
            progDataService.savePersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            String emailCia = progDataService.extraerEmailCompania(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            Persona perso = progDataService.extraerDocumentoIdentidad(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            progDataService.changeDocumentoIdentidad(persona, personasVinculadas, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, mapKeyPersonas, mapDNIPersonas, ds);
            loop++;
        }
    }

    private void revisarAlumnosMatriculados(CicloAcademico ciclo, Map<String, MatriculaResumen> mapResumenes, Map<String, AlumnoBlocked> mapBloqueados) {
        List<MatriculaResumen> alumnosResumen = new ArrayList(mapResumenes.values());
        int loop = 1;
        for (MatriculaResumen aluResumen : alumnosResumen) {
            Alumno alumno = aluResumen.getAlumno();
            System.out.println(loop + ".- " + alumno.getCodigo() + " :::: ");
            loop++;
        }

        visor.inicializar("aluRes", alumnosResumen.size());
        for (MatriculaResumen aluResumen : alumnosResumen) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }
            progDataService.revisarAlumnoMatriculado(aluResumen);
        }

        logger.debug("\trevisarAlumnosMatriculados envio {} alumnos a ser revisados", alumnosResumen.size());
        int procesadosAntes = -1;
        long t1 = System.currentTimeMillis();
        long t10 = System.currentTimeMillis();
        boolean verError = true;
        boolean iniciarTimer = false;
        for (;;) {

            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            boolean salir = true;
            boolean ver = false;
            boolean errorVisto = false;

            int procesados = 0;
            if (ver) {
                logger.debug("Tenemos un total de " + alumnosResumen.size() + " elementos");
            }
            if (iniciarTimer) {
                long t6 = System.currentTimeMillis();
                if (t6 - t10 > 5000) {
                    verError = true;
                }
            }
            for (MatriculaResumen matriResumen : alumnosResumen) {
                Alumno alumno = matriResumen.getAlumno();
                if (matriResumen.getProcesado() == 0) {
                    salir = false;
                    if (matriResumen.getFechaInicioProceso() == null) {
                        continue;
                    }

                    long t4 = System.currentTimeMillis();
                    long t3 = matriResumen.getFechaInicioProceso().getTime();
                    long dd = t4 - t3;
                    if (dd > 5000 && verError) {
                        logger.debug("\tResumen A sin procesar por " + dd + "mseg alumno:" + alumno.getCodigo());
                        errorVisto = true;
                    }
                } else {
                    procesados++;
                }
            }
            if (ver) {
                ver = false;
            }
            if (errorVisto) {
                verError = false;
                iniciarTimer = true;
                t10 = System.currentTimeMillis();
            }
            if (salir) {
                break;
            }
            if (procesadosAntes != procesados) {
                logger.debug("\trevisarAlumnosMatriculados procesados {} de {}", procesados, alumnosResumen.size());
                t1 = System.currentTimeMillis();
            } else {
                long t2 = System.currentTimeMillis();
                long dd = t2 - t1;
                if (dd > 5000) {
                    for (MatriculaResumen matriResumen : alumnosResumen) {
                        Alumno alumno = matriResumen.getAlumno();
                        if (matriResumen.getProcesado() == 0) {
                            logger.debug("\tResumen B sin procesar por " + dd + "mseg alumno:" + alumno.getCodigo());
                        }
                    }
                    t1 = System.currentTimeMillis();
                }

            }

            procesadosAntes = procesados;
        }
    }

    private Map<String, MatriculaResumen> loadDataMatriculados(List<MatriculaSeccion> matriculasSecciones, Map<String, Seccion> mapSecciones, CicloAcademico ciclo, DataSessionPivot ds) {

        List<MatriculaResumen> resumenesBD = matriculaResumenDAO.allByCiclo(ciclo);
        Map<String, MatriculaResumen> mapResumenes = TypesUtil.convertListToMap("alumno.codigo", resumenesBD);
        Map<String, MatriculaResumen> mapResumenesById = TypesUtil.convertListToMap("id", resumenesBD);
        for (MatriculaResumen mr : resumenesBD) {
            mr.setMatriculaCurso(new ArrayList());
            mr.setMatriculaSeccion(new ArrayList());
        }

        List<MatriculaSeccion> matriSeccionesBD = matriculaSeccionDAO.allByCiclo(ciclo);
        for (MatriculaSeccion ms : matriSeccionesBD) {
            MatriculaResumen mr = mapResumenesById.get(ms.getMatriculaResumen().getId());
            mr.getMatriculaSeccion().add(ms);
            ms.setMatriculaResumen(mr);
        }

        List<MatriculaCurso> matriCursosBD = matriculaCursoDAO.allByCiclo(ciclo);
        for (MatriculaCurso mc : matriCursosBD) {
            MatriculaResumen mr = mapResumenesById.get(mc.getMatriculaResumen().getId());
            mr.getMatriculaCurso().add(mc);
            mc.setMatriculaResumen(mr);
        }

        for (MatriculaSeccion matriSecc : matriculasSecciones) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }
            progDataService.loadDataMatriculados(matriSecc, mapResumenes, mapSecciones, ciclo, ds);
        }

        long t1 = System.currentTimeMillis();
        int procesadosAntes = -1;
        for (;;) {
            if (visor.isStop()) {
                throw new PhobosException("Carga detenida intespestivamente");
            }

            long t2 = System.currentTimeMillis();
            boolean salir = true;
            boolean ver = false;
            int procesados = 0;
            if (ver) {
                System.out.println("Tenemos un total de " + matriculasSecciones.size() + " elementos");
            }
            for (MatriculaSeccion matriSecc : matriculasSecciones) {
                if (matriSecc.getProcesado() == 0) {
                    salir = false;
                    long t4 = System.currentTimeMillis();
                    long t3 = matriSecc.getFechaInicioProceso() == null ? System.currentTimeMillis() : matriSecc.getFechaInicioProceso().getTime();
                    long dd = t3 - t4;
                    if (dd > 5000) {
                        System.out.println("\tElemento sin procesar por " + dd + "mseg alumno:" + matriSecc.getCodigoAlumno() + " seccion:" + matriSecc.getCodigoSeccion());
                    }
                } else {
                    procesados++;
                }
            }
            if (ver) {
                ver = false;
            }
            if (salir) {
                break;
            }
            if (procesadosAntes != procesados) {
                //logger.debug("\tloadDataMatriculados procesados {} de {}", procesados, matriculasSecciones.size());
            }
            if (t2 - t1 > 5000) {
                ver = true;
                t1 = System.currentTimeMillis();
            }

            procesadosAntes = procesados;
        }
        return mapResumenes;
    }

    private List<DiaHoraGrupo> crearHorarioGrupos(
            String rutaFile,
            Map<Integer, Dia> mapDias,
            Map<Integer, Hora> mapHoras,
            Map<String, GrupoHoras> mapGrupos,
            CicloAcademico ciclo) {

        List<DiaHoraGrupo> horarios = new ArrayList<>();

        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Map<String, List<DiaHoraGrupo>> mapGpoHorarios = new LinkedHashMap();

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String cicloCod = getCellStringValue(1, row);
                String gpo = getCellStringValue(2, row);
                String hdia = getCellStringValue(3, row);
                String diaNum = getCellStringValue(4, row);
                String horaNum = getCellStringValue(5, row);

                if (StringUtils.isEmpty(cicloCod)) {
                    break;
                }

                if (visor.isStop()) {
                    throw new PhobosException("Carga detenida intespestivamente");
                }

                Dia dia = mapDias.get(Integer.parseInt(diaNum));
                Hora hora = mapHoras.get(Integer.parseInt(horaNum));
                GrupoHoras grupo = mapGrupos.get(gpo);
                DiaHoraGrupo hdiaGpo = new DiaHoraGrupo(ciclo, grupo, dia, hora);

                List<DiaHoraGrupo> diasHorasGpo = mapGpoHorarios.get(gpo);
                if (diasHorasGpo == null) {
                    diasHorasGpo = new ArrayList();
                    mapGpoHorarios.put(gpo, diasHorasGpo);
                }
                diasHorasGpo.add(hdiaGpo);

            }

            visor.inicializar("horGpo", mapGpoHorarios.size());

            List<DiaHoraGrupo> hdiaGpoTodosBD = diaHoraGrupoDAO.allByCiclo(ciclo);
            Map<Long, List<DiaHoraGrupo>> mapHorarioGpos = TypesUtil.convertListToMapList("grupoHorario.id", hdiaGpoTodosBD);
            for (Map.Entry<String, List<DiaHoraGrupo>> entry : mapGpoHorarios.entrySet()) {
                String gpo = entry.getKey();
                GrupoHoras grupo = mapGrupos.get(gpo);
                List<DiaHoraGrupo> hdiaGpo = entry.getValue();
                List<DiaHoraGrupo> hdiaGpoBD = mapHorarioGpos.get(grupo.getId());
                hdiaGpoBD = (hdiaGpoBD == null) ? new ArrayList() : hdiaGpoBD;
                ListsInspector inspector = TypesUtil.analizeLists(hdiaGpoBD, hdiaGpo, "key");

                List<DiaHoraGrupo> nuevos = inspector.getNewList();
                List<DiaHoraGrupo> muertos = inspector.getDeadList();

                logger.debug("\tNuevos grupos por agregar {}", nuevos.size());
                int cont = 0;
                for (DiaHoraGrupo nuevo : nuevos) {
                    logger.debug("\t({}, {}) {} {} {}", cont++, nuevos.size(), nuevo.getGrupoHorario().getCodigo(), nuevo.getDia().getNumeroDia(), nuevo.getHora().getNumero());
                    diaHoraGrupoDAO.save(nuevo);
                    horarios.add(nuevo);
                }

                diaHoraGrupoDAO.deleteAllInList(muertos);
                visor.agregarLog("horGpo", "saveHorGpo", "Guardando horario de " + gpo, true, "info");

            }

            return horarios;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
    }

    private List<HorarioSeccion> crearHorarioSecciones(String rutaFile,
            Map<String, Seccion> mapSecciones,
            Map<Integer, Dia> mapDias,
            Map<Integer, Hora> mapHoras,
            Map<String, Aula> mapAulas,
            CicloAcademico cicloAcademico) {

        List<HorarioSeccion> horarios = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Map<String, List<HorarioSeccion>> mapSeccHorarios = new LinkedHashMap();
            Map<String, List<HorarioAula>> mapAulaHorarios = new LinkedHashMap();

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String clave = getCellStringValue(2, row);
                String diaNum = getCellStringValue(4, row);
                String horaNum = getCellStringValue(5, row);
                String aulaCod = getCellStringValue(6, row);

                if (StringUtils.isEmpty(clave)) {
                    break;
                }

                if (visor.isStop()) {
                    throw new PhobosException("Carga detenida intespestivamente");
                }

                Seccion seccion = mapSecciones.get(clave);
                Dia dia = mapDias.get(Integer.parseInt(diaNum));
                Hora hora = mapHoras.get(Integer.parseInt(horaNum));
                Aula aula = mapAulas.get(aulaCod);
                if (aula == null && !StringUtils.isEmpty(aulaCod) && cicloAcademico.getCodigo().compareTo("201710") >= 0) {
                    throw new PhobosException("Aula " + aulaCod + " no se halló en la base de datos");
                }

                List<HorarioSeccion> horarioSecc = mapSeccHorarios.get(clave);
                if (horarioSecc == null) {
                    horarioSecc = new ArrayList();
                    mapSeccHorarios.put(clave, horarioSecc);
                }

                HorarioSeccion horario = new HorarioSeccion(seccion, dia, hora, aula);
                horarioSecc.add(horario);

                if (aula != null) {
                    List<HorarioAula> horariosAula = mapAulaHorarios.get(clave);
                    if (horariosAula == null) {
                        horariosAula = new ArrayList();
                        mapAulaHorarios.put(clave, horariosAula);
                    }

                    HorarioAula horarioAula = new HorarioAula(seccion, dia, hora, aula);
                    horariosAula.add(horarioAula);
                }

            }

            visor.inicializar("horSecc", mapSeccHorarios.size());
            visor.inicializar("horAula", mapAulaHorarios.size());

            logger.debug("{} horarioSeccion leídos", mapSeccHorarios.size());
            logger.debug("{} horarioAula leídos", mapAulaHorarios.size());
            List<HorarioSeccion> horarioSeccCicloBD = horarioSeccionDAO.allByCiclo(cicloAcademico);
            List<HorarioAula> horarioAulaCicloBD = horarioAulaDAO.allByCiclo(cicloAcademico);

            Map<Long, List<HorarioSeccion>> mapHorariosSecciones = TypesUtil.convertListToMapList("seccion.id", horarioSeccCicloBD);
            Map<Long, List<HorarioAula>> mapHorariosAulas = TypesUtil.convertListToMapList("seccion.id", horarioAulaCicloBD);

            for (Map.Entry<String, List<HorarioSeccion>> entry : mapSeccHorarios.entrySet()) {
                String clave = entry.getKey();
                logger.debug("clave {} de horarioSeccion", clave);
                Seccion seccion = mapSecciones.get(clave);
                if (seccion == null) {
                    visor.agregarLog("horSecc", "saveHorSecc", "Horario-seccion no se puede grabar para seccion " + clave + " no existente", false, "error");
                    logger.debug("\tNo existe PTM!!!!");
                }
                List<HorarioSeccion> horarioSecc = entry.getValue();
                List<HorarioSeccion> horarioSeccBD = mapHorariosSecciones.get(seccion.getId());
                horarioSeccBD = (horarioSeccBD == null) ? new ArrayList() : horarioSeccBD;
                ListsInspector inspector = TypesUtil.analizeLists(horarioSeccBD, horarioSecc, "key");

                List<HorarioSeccion> nuevos = inspector.getNewList();
                List<HorarioSeccion> muertos = inspector.getDeadList();

                int contador = 0;
                for (HorarioSeccion nuevo : nuevos) {
                    logger.debug("\t ( {} / {} ) Agregando horario-seccion {} {} {}", contador++, nuevos.size(), nuevo.getDia().getNumeroDia(), nuevo.getHora().getNumero(), nuevo.getSeccion().getCodigo());
                    horarioSeccionDAO.save(nuevo);
                    horarios.add(nuevo);
                }

                horarioSeccionDAO.deleteAllInList(muertos);

                List<HorarioSeccion> existentesBD = inspector.getOldListDB();
                List<HorarioSeccion> existentesForm = inspector.getOldListForm();
                Map<String, HorarioSeccion> mapHorarioSeccBD = existentesBD.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));
                Map<String, HorarioSeccion> mapHorarioSeccForm = existentesForm.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));

                for (Map.Entry<String, HorarioSeccion> entry2 : mapHorarioSeccBD.entrySet()) {
                    HorarioSeccion hsBD = entry2.getValue();
                    HorarioSeccion hsForm = mapHorarioSeccForm.get(entry2.getKey());
                    hsBD.setAula(hsForm.getAula());
                    logger.debug("\tActualizando horario-seccion {} {} {}", hsBD.getDia().getNumeroDia(), hsBD.getHora().getNumero(), hsBD.getSeccion().getCodigo());
                    horarioSeccionDAO.update(hsBD);
                    horarios.add(hsBD);
                }
                visor.agregarLog("horSecc", "saveHorSecc", "horarios-seccion actualizados para " + seccion.getCodigo(), true, "info");
            }

            for (Map.Entry<String, List<HorarioAula>> entry : mapAulaHorarios.entrySet()) {
                String clave = entry.getKey();
                logger.debug("clave {} de horarioSeccion", clave);
                Seccion seccion = mapSecciones.get(clave);
                if (seccion == null) {
                    visor.agregarLog("horSecc", "saveHorSecc", "Horario-aula no se puede grabar para seccion " + clave + " no existente", false, "error");
                    logger.debug("\tNo existe PTM!!!!");
                }
                List<HorarioAula> horarioSecc = entry.getValue();
                List<HorarioAula> horarioAulaBD = mapHorariosAulas.get(seccion.getId());
                horarioAulaBD = (horarioAulaBD == null) ? new ArrayList() : horarioAulaBD;
                ListsInspector inspector = TypesUtil.analizeLists(horarioAulaBD, horarioSecc, "key");

                List<HorarioAula> nuevos = inspector.getNewList();
                List<HorarioAula> muertos = inspector.getDeadList();

                int contador = 0;
                for (HorarioAula nuevo : nuevos) {
                    logger.debug("\t ( {} / {} ) Agregando horario-aula {} {} {}", contador++, nuevos.size(), nuevo.getDia().getNumeroDia(), nuevo.getHora().getNumero(), nuevo.getSeccion().getCodigo());
                    horarioAulaDAO.save(nuevo);
                }

                horarioAulaDAO.deleteAllInList(muertos);

                List<HorarioAula> existentesBD = inspector.getOldListDB();
                List<HorarioAula> existentesForm = inspector.getOldListForm();
                Map<String, HorarioAula> mapHorarioAulaBD = existentesBD.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));
                Map<String, HorarioAula> mapHorarioAulaForm = existentesForm.stream().collect(Collectors.toMap(x -> x.getKey(), x -> x));

                for (Map.Entry<String, HorarioAula> entry2 : mapHorarioAulaBD.entrySet()) {
                    HorarioAula hsBD = entry2.getValue();
                    HorarioAula hsForm = mapHorarioAulaForm.get(entry2.getKey());
                    hsBD.setSeccion(hsForm.getSeccion());
                    logger.debug("\tActualizando horario-aula {} {} {}", hsBD.getDia().getNumeroDia(), hsBD.getHora().getNumero(), hsBD.getAula().getCodigo());
                    horarioAulaDAO.update(hsBD);
                }
                visor.agregarLog("horAula", "saveHorAula", "horarios-aula actualizados para " + seccion.getCodigo(), true, "info");
            }

            return horarios;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
    }

    private void crearCursos(String rutaFileCursos, Map<String, Curso> mapCursos, Map<String, DepartamentoAcademico> mapDepartamentosAcademicos) {

        try {
            FileInputStream fis = new FileInputStream(rutaFileCursos);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String curCodigo = getCellStringValue(1, row);
                if (StringUtils.isEmpty(curCodigo)) {
                    break;
                }
            }
            visor.inicializar("cur", loop);

            loop = 0;
            rowIterator = mySheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                if (visor.isStop()) {
                    throw new PhobosException("Carga detenida intespestivamente");
                }

                String curCodigo = getCellStringValue(1, row);
                String curNuevo = getCellStringValue(2, row);
                String nombre = getCellStringValue(3, row);
                String depCodigo = getCellStringValue(4, row);
                String curCredit = getCellStringValue(5, row);
                String curCrevar = getCellStringValue(6, row);
                String curTeoria = getCellStringValue(7, row);
                String curPracti = getCellStringValue(8, row);
                String tCurso = getCellStringValue(9, row);
                String tipo = getCellStringValue(10, row);

                Integer curCreditTeo = getCellIntegerValue(12, row);
                Integer curCreditPra = getCellIntegerValue(13, row);

                if (mapCursos.get(curNuevo) != null) {
                    visor.agregarLog("cur", "saveCursos", "Curso " + curNuevo + " ya existe", true, "info");
                    Curso cursoDb = mapCursos.get(curNuevo);
                    cursoDb.setCreditosTeoria(curCreditTeo);
                    cursoDb.setCreditosPractica(curCreditPra);
                    cursoDAO.update(cursoDb);
                    continue;
                }

                Curso curso = new Curso();
                curso.setEstadoEnum(EstadoEnum.ACT);
                curso.setCodigo(curNuevo);
                curso.setNombre(nombre);
                curso.setCreditos(Integer.parseInt(curCredit));
                curso.setDepartamentoAcademico(mapDepartamentosAcademicos.get(depCodigo));
                curso.setCodigoAnterior1(curCodigo);
                if (!StringUtils.isEmpty(curCrevar)) {
                    curso.setCreditosVariables(Integer.parseInt(curCrevar));
                }
                curso.setHorasTeoria(Integer.parseInt(curTeoria));
                curso.setHorasPractica(Integer.parseInt(curPracti));

                if (tCurso.compareTo("TT") == 0) {
                    curso.setTipoCurso(TipoCursoEnum.TEO.name());
                } else if (tCurso.compareTo("TP") == 0) {
                    curso.setTipoCurso(TipoCursoEnum.TEOPRA.name());
                } else if (tCurso.compareTo("PP") == 0) {
                    curso.setTipoCurso(TipoCursoEnum.PRA.name());
                }

                cursoDAO.save(curso);
                mapCursos.put(curso.getCodigo(), curso);
                visor.agregarLog("cur", "saveCursos", "Curso " + curNuevo + " nuevo guardado", true, "info");
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
    }

    private List<Alumno> crearAlumnos(String rutaFile) {
        List<Alumno> alumnnos = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String codigo = getCellStringValue(1, row);
                if (StringUtils.isEmpty(codigo)) {
                    break;
                }

                String codigoEspecialidad = getCellStringValue(2, row);
                String codigoPostgrado = getCellStringValue(3, row);
                String situacion = getCellStringValue(4, row);
                String emailCia = getCellStringValue(5, row);
                String paterno = getCellStringValue(8, row);
                String materno = getCellStringValue(9, row);
                String nombres = getCellStringValue(10, row);
                String tipoDocumento = getCellStringValue(11, row);
                String numeroDoc = getCellStringValue(12, row);
                String cicloInicio = getCellStringValue(13, row);
                String cicloActivo = getCellStringValue(14, row);
                Date fechaNace = getCellDateValue(15, row);
                String ubigeoNac = getCellStringValue(16, row);
                String paisNac = getCellStringValue(17, row);
                String nacionalidad = getCellStringValue(18, row);
                String estadoCivil = getCellStringValue(19, row);
                String emailPersonal = getCellStringValue(20, row);
                String celular = getCellStringValue(21, row);
                String telefono = getCellStringValue(22, row);
                String ubigeoDomicilio = getCellStringValue(24, row);
                String domicilio = getCellStringValue(25, row);

                if (StringUtils.isEmpty(tipoDocumento)) {
                    tipoDocumento = "DNI";
                }
                if (StringUtils.isEmpty(emailCia)) {
                    emailCia = null;
                }

                Persona persona = new Persona(paterno, materno, nombres, numeroDoc, tipoDocumento);
                persona.setFechaNacer(fechaNace);
                persona.setUbigeoNacer(ubigeoNac);
                persona.setCodigoPaisNacer(paisNac);
                persona.setCodigoNacionalidad(nacionalidad);
                persona.setCodigoEstadoCivil(estadoCivil);
                persona.setEmail(emailPersonal);
                persona.setEmailCompania(emailCia);
                persona.setTelefono(telefono);
                persona.setCelular(celular);
                persona.setUbigeoDomicilio(ubigeoDomicilio);
                persona.setDireccion(domicilio);

                Alumno alumno = new Alumno(codigo, codigoEspecialidad, codigoPostgrado, situacion, emailCia);
                alumno.setPersona(persona);
                alumno.setCodigoCicloIngreso(cicloInicio);
                alumno.setCodigoCicloActivo(cicloActivo);
                alumnnos.add(alumno);
            }
            logger.debug("Se han leido un total de {} alumnos", loop);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return alumnnos;
    }

    private List<MatriculaSeccion> crearMatriculasSecciones(String rutaFile) {
        List<MatriculaSeccion> matriculasSecciones = new ArrayList();
        List<String> rev = new ArrayList();
        List<String> poste = new ArrayList();
        List<MatriculaSeccion> posteMS = new ArrayList();

        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String ciclo = getCellStringValue(1, row);
                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                String codigoAlumno = getCellStringValue(2, row);
                String codigoSeccion = getCellStringValue(3, row);
                Integer creditos = Integer.valueOf(getCellStringValue(4, row));

                MatriculaSeccion alumnoSecc = new MatriculaSeccion(codigoAlumno, codigoSeccion, creditos);
                if (rev.contains(codigoAlumno)) {
                    poste.add(codigoAlumno);
                    posteMS.add(alumnoSecc);
                    continue;
                }

                if (!poste.isEmpty()) {
                    String y = poste.get(0);
                    MatriculaSeccion ms = posteMS.get(0);
                    if (!rev.contains(y) && !y.equals(codigoAlumno)) {
                        poste.remove(0);
                        posteMS.remove(0);
                        rev.add(y);
                        matriculasSecciones.add(ms);
                        if (rev.size() > 25) {
                            rev.remove(0);
                        }
                    }
                }

                rev.add(codigoAlumno);
                matriculasSecciones.add(alumnoSecc);
                if (rev.size() > 25) {
                    rev.remove(0);
                }
            }

            if (!poste.isEmpty()) {
                matriculasSecciones.addAll(posteMS);
            }
            logger.debug("Se han leido un total de {} alumnos-secciones", loop);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return matriculasSecciones;
    }

    private List<DocenteSeccion> crearDocenteSecciones(String rutaFile) {
        List<DocenteSeccion> docenteSecciones = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String ciclo = getCellStringValue(1, row);
                String codigoDocente = getCellStringValue(2, row);
                String codigoSeccion = getCellStringValue(3, row);
                Integer principal = Integer.valueOf(getCellStringValue(4, row));
                String carga = getCellStringValue(9, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                DocenteSeccion profeSecc = new DocenteSeccion(principal, codigoDocente, codigoSeccion, carga);
                docenteSecciones.add(profeSecc);

            }
            logger.debug("Se han leido un total de {} profesores-secciones", loop);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return docenteSecciones;
    }

    private List<Docente> crearDocentes(String rutaFile) {
        List<Docente> docentes = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String nro = getCellStringValue(0, row);
                String tipoDocumento = getCellStringValue(1, row);
                String numeroDoc = getCellStringValue(2, row);
                String codigo = getCellStringValue(3, row);
                String dpto = getCellStringValue(4, row);
                String paterno = getCellStringValue(5, row);
                String materno = getCellStringValue(6, row);
                String nombres = getCellStringValue(7, row);

                if (StringUtils.isEmpty(nro)) {
                    break;
                }

                Persona persona = new Persona(paterno, materno, nombres, numeroDoc, tipoDocumento);
                Docente docente = new Docente(codigo, tipoDocumento, numeroDoc, dpto);
                docente.setPersona(persona);
                docentes.add(docente);
            }
            logger.debug("Se han leido un total de {} personas", loop);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return docentes;
    }

    private List<Persona> crearPersonas(String rutaFile) {
        List<Persona> personas = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String paterno = getCellStringValue(1, row);
                if (StringUtils.isEmpty(paterno)) {
                    break;
                }

                String materno = getCellStringValue(2, row);
                String nombres = getCellStringValue(3, row);
                String tipoDoc = getCellStringValue(4, row);
                String numeroDoc = getCellStringValue(5, row);
                String emailCia = getCellStringValue(6, row);
                Date fechaNace = getCellDateValue(7, row);
                String ubigeoNac = getCellStringValue(8, row);
                String paisNac = getCellStringValue(9, row);
                String nacionalidad = getCellStringValue(10, row);
                String estadoCivil = getCellStringValue(11, row);
                String emailPersonal = getCellStringValue(12, row);
                String celular = getCellStringValue(13, row);
                String telefono = getCellStringValue(14, row);
                String ubigeoDomicilio = getCellStringValue(16, row);
                String domicilio = getCellStringValue(17, row);

                if (StringUtils.isEmpty(tipoDoc)) {
                    tipoDoc = "DNI";
                }

                Persona persona = new Persona(paterno, materno, nombres, numeroDoc, tipoDoc);
                persona.setFechaNacer(fechaNace);
                persona.setUbigeoNacer(ubigeoNac);
                persona.setCodigoPaisNacer(paisNac);
                persona.setCodigoNacionalidad(nacionalidad);
                persona.setCodigoEstadoCivil(estadoCivil);
                persona.setEmail(emailPersonal);
                persona.setEmailCompania(emailCia);
                persona.setTelefono(telefono);
                persona.setCelular(celular);
                persona.setUbigeoDomicilio(ubigeoDomicilio);
                persona.setDireccion(domicilio);
                personas.add(persona);
            }
            logger.debug("Se han leido un total de {} personas", loop);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return personas;
    }

    private List<Seccion> crearSecciones(String rutaFile) {
        List<Seccion> secciones = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            List<Carrera> carreras = carreraDAO.all();
            Map<String, Carrera> mapCarreras = TypesUtil.convertListToMap("codigo", carreras);

            List<Facultad> facultades = facultadDAO.all();
            Map<String, Facultad> mapFacultad = TypesUtil.convertListToMap("codigo", facultades);

            List<TipoRepitencia> repitencias = tipoRepitenciaDAO.allByCode(Arrays.asList("REP", "ING", "NREP"));
            Map<String, TipoRepitencia> mapTipoRepitencia = TypesUtil.convertListToMap("letra", repitencias);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String ciclo = getCellStringValue(1, row);
                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                String clave = getCellStringValue(2, row);
                String gpo = getCellStringValue(3, row);
                String aula = getCellStringValue(4, row);
                String gclave = getCellStringValue(5, row);
                String tclave = getCellStringValue(6, row);
                String clave2 = getCellStringValue(8, row);
                Integer vacantes = getCellIntegerValue(10, row);
                Integer matriculados = getCellIntegerValue(11, row);

                Integer restriccionCapa = getCellIntegerValue(12, row);

                String espCodigo = getCellStringValue(13, row);
                String facCodigo = getCellStringValue(14, row);
                String espGrado = getCellStringValue(15, row);

                String condicion = getCellStringValue(16, row);

                Seccion seccion = new Seccion(clave, clave2, gpo, aula, gclave, tclave, vacantes, matriculados, restriccionCapa);

                List<RestriccionCarrera> restricionesCarrera = new ArrayList();

                if (!StringUtils.isEmpty(espCodigo)) {
                    String[] carrerasPregrado = espCodigo.split("/");
                    for (String codeCarrera : carrerasPregrado) {
                        Carrera carrera = mapCarreras.get(codeCarrera);
                        if (carrera == null) {
                            continue;
                        }
                        RestriccionCarrera restriccionCarrera = new RestriccionCarrera();
                        restriccionCarrera.setCarrera(carrera);
                        restriccionCarrera.setSeccion(seccion);
                        restricionesCarrera.add(restriccionCarrera);
                    }
                }

                if (!StringUtils.isEmpty(espGrado)) {
                    String[] carrerasPosgrado = espGrado.split("/");
                    for (String codeCarrera : carrerasPosgrado) {
                        Carrera carrera = mapCarreras.get(codeCarrera);
                        if (carrera == null) {
                            continue;
                        }
                        RestriccionCarrera restriccionCarrera = new RestriccionCarrera();
                        restriccionCarrera.setCarrera(carrera);
                        restriccionCarrera.setSeccion(seccion);
                        restricionesCarrera.add(restriccionCarrera);
                    }
                }

                List<RestriccionFacultad> restriccionesFacultad = new ArrayList();
                if (!StringUtils.isEmpty(facCodigo)) {
                    String[] codigosFacultad = facCodigo.split("/");
                    for (String codeFacultad : codigosFacultad) {
                        Facultad facultad = mapFacultad.get(codeFacultad);
                        if (facultad == null) {
                            continue;
                        }
                        RestriccionFacultad restriccionFacultad = new RestriccionFacultad();
                        restriccionFacultad.setFacultad(facultad);
                        restriccionFacultad.setSeccion(seccion);
                        restriccionesFacultad.add(restriccionFacultad);
                    }
                }

                List<RestriccionRepitencia> restriccionesRepitencia = new ArrayList();
                if (!StringUtils.isEmpty(condicion)) {
                    String[] condiciones = condicion.split("/");
                    for (String cond : condiciones) {
                        TipoRepitencia tipo = mapTipoRepitencia.get(cond);
                        if (tipo == null) {
                            continue;
                        }
                        RestriccionRepitencia restriccion = new RestriccionRepitencia();
                        restriccion.setTipoRepitencia(tipo);
                        restriccion.setSeccion(seccion);
                        restriccionesRepitencia.add(restriccion);
                    }
                }

                seccion.setRestriccionesCarrera(restricionesCarrera);
                seccion.setRestriccionesFacultad(restriccionesFacultad);
                seccion.setRestriccionesRepitencia(restriccionesRepitencia);

                System.out.println(seccion.getCodigo() + " vacantes: " + seccion.getVacantes());
                System.out.println("\t" + " matriculados: " + seccion.getMatriculados());
                secciones.add(seccion);
            }
            logger.debug("Se han leido un total de {} secciones", loop);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return secciones;
    }

    private List<GrupoSeccion> crearGruposSecciones(String rutaFile) {
        List<GrupoSeccion> gpoSecciones = new ArrayList();
        try {

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String ciclo = getCellStringValue(1, row);
                String gclave = getCellStringValue(2, row);
                String curso = getCellStringValue(3, row);
                String anexo = getCellStringValue(5, row);
                String dirigido = getCellStringValue(6, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                GrupoSeccion gpoSecc = new GrupoSeccion(gclave, curso, anexo, dirigido);
                gpoSecciones.add(gpoSecc);
            }
            logger.debug("Se han leido un total de {} grupos-secciones", loop);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new PhobosException("El archivo no puede ser leido");
        }

        return gpoSecciones;
    }

    private String saveFile(MultipartFile file) {
        try {
            String fileName = TypesUtil.getUnixTime() + "." + TypesUtil.getClean(file.getOriginalFilename());
            FileHelper.createDirectory(Constantine.TMP_DIR);
            String absoluteName = Constantine.TMP_DIR + fileName;
            FileHelper.saveToDisk(file, absoluteName);
            return absoluteName;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new PhobosException("Archivo no puede ser guardado en el servidor");
        }
    }

    private String getCellStringValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String dato = cell.getStringCellValue();
        if (dato == null) {
            return null;
        }

        dato = StringUtils.replaceChars(dato, '\t', ' ');
        dato = StringUtils.replaceChars(dato, '\r', ' ');
        dato = StringUtils.replaceChars(dato, '\n', ' ');
        dato = StringUtils.replaceChars(dato, ',', ' ');
        dato = StringUtils.replaceChars(dato, '|', ' ');
        dato = StringUtils.replaceChars(dato, '´', '\'');
        dato = dato.replaceAll("\\s{2,}", " ").trim();

        if (dato.equals(".")) {
            return "";
        }
        if (dato.equals("-")) {
            return "";
        }
        if (dato.equals(",")) {
            return "";
        }

        return dato;
    }

    private Integer getCellIntegerValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue()).intValue();
        }

        cell.setCellType(Cell.CELL_TYPE_STRING);
        String dato = cell.getStringCellValue();
        if (dato == null) {
            return null;
        }

        dato = StringUtils.replaceChars(dato, '\t', ' ');
        dato = StringUtils.replaceChars(dato, '\r', ' ');
        dato = StringUtils.replaceChars(dato, '\n', ' ');
        dato = StringUtils.replaceChars(dato, ',', ' ');
        dato = StringUtils.replaceChars(dato, '|', ' ');
        dato = StringUtils.replaceChars(dato, '´', '\'');
        dato = dato.replaceAll("\\s{2,}", " ").trim();

        if (dato.equals(".")) {
            return 0;
        }
        if (dato.equals("-")) {
            throw new PhobosException("Valor de integer desconocido");
        }
        if (dato.equals(",")) {
            return 0;
        }
        if (StringUtils.isEmpty(dato)) {
            return null;
        }

        return Integer.valueOf(dato);
    }

    private Date getCellDateValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            if (cell.getStringCellValue().equals("")) {
                return null;
            }
            System.out.println("CONTE::: <<" + cell.getStringCellValue() + ">>");
        }
        Date dato = cell.getDateCellValue();
        return dato;
    }

    private void loadInfoPersona(
            Persona persona,
            Map<String, TipoDocIdentidad> mapTiposDoc,
            Map<String, EstadoCivil> mapEstadoCivil,
            Map<String, Pais> mapPaises,
            Map<String, Ubicacion> mapUbicacion) {
        TipoDocIdentidad tipoDoc = mapTiposDoc.get(persona.getCodigoTipoDocumento());
        if (tipoDoc == null) {
            persona.setCodigoTipoDocumento("DNI");
            tipoDoc = mapTiposDoc.get(persona.getCodigoTipoDocumento());
        }
        persona.setTipoDocumento(tipoDoc);

        Pais paisNacer = mapPaises.get(persona.getCodigoPaisNacer());
        Pais paisNacionalidad = mapPaises.get(persona.getCodigoNacionalidad());
        Ubicacion ubigeoNacer = mapUbicacion.get(persona.getUbigeoNacer());
        Ubicacion ubigeoDomicilio = mapUbicacion.get(persona.getUbigeoDomicilio());
        EstadoCivil estadoCivil = mapEstadoCivil.get(persona.getCodigoEstadoCivil());

        persona.setPaisNacer(paisNacer);
        persona.setNacionalidad(paisNacionalidad);
        persona.setUbicacionNacer(ubigeoNacer);
        persona.setUbicacionDomicilio(ubigeoDomicilio);
        persona.setEstadoCivil(estadoCivil);

        String domicilio = persona.getDireccion();
        if (domicilio != null) {
            domicilio = domicilio.trim();
        }
        if (StringUtils.isEmpty(domicilio)) {
            domicilio = null;
            persona.setDireccion(domicilio);
        }
    }

}
