package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.EstadoCivil;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.EstadoCivilDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.general.UbicacionDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ProgramaHorarioServiceImp implements ProgramaHorarioService {

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

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
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void loadArchivosHorario(MultipartFile[] files, CicloAcademico ciclo, DataSessionPivot ds) {
        logger.debug("CICLO  {} {} {} ", ciclo.getId(), ciclo.getYear(), ciclo.getNumeroCiclo());

        String rutaFileGpoSecciones = saveFile(files[0]);
        String rutaFileSecciones = saveFile(files[1]);
        String rutaFilePersonas = saveFile(files[2]);
        String rutaFileProfes = saveFile(files[3]);
        String rutaFileProfeSecciones = saveFile(files[4]);
//        String rutaFileAlumno = saveFile(files[5]);
//        String rutaFileAlumnoSecciones = saveFile(files[6]);
        String rutaFileHorarioGrupos = saveFile(files[7]);
        String rutaFileHorarioSecciones = saveFile(files[8]);

        List<GrupoSeccion> gruposSecciones = crearGruposSecciones(rutaFileGpoSecciones);
        List<Seccion> secciones = crearSecciones(rutaFileSecciones);
        List<Persona> personas = crearPersonas(rutaFilePersonas);
        List<Docente> docentes = crearDocentes(rutaFileProfes);
        List<DocenteSeccion> docentesSecciones = crearDocenteSecciones(rutaFileProfeSecciones);
//        List<Alumno> alumnos = crearAlumnos(rutaFileAlumno);
//        List<MatriculaSeccion> matriculaSecciones = crearMatriculasSecciones(rutaFileAlumnoSecciones);

        List<Persona> personasDB = personaDAO.all();
        Map<String, List<Persona>> mapKeyPersonas = TypesUtil.convertListToMapList("key", personasDB);
        Map<Long, Persona> mapIdPersonas = TypesUtil.convertListToMap("id", personasDB);
        Map<String, Persona> mapDNIPersonas = new LinkedHashMap();
        for (Persona persona : personasDB) {
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

//        t1 = System.currentTimeMillis();
//        logger.debug("saveAlumnos");
//        this.saveAlumnos(alumnos, mapKeyPersonas, mapDNIPersonas, mapIdPersonas, mapAlumnos, mapSituaciones, ds);
//        t2 = System.currentTimeMillis();
//        logger.debug("\tsaveAlumnos ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataDocentes");
        Map<String, Docente> mapDocentes = this.saveDocentes(docentes, mapKeyPersonas, mapDNIPersonas, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataDocentes ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataGpoSecciones");
        Map<String, GrupoSeccion> mapGpoSecciones = progDataService.loadDataGpoSecciones(gruposSecciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataGpoSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataSecciones");
        Map<String, Seccion> mapSecciones = progDataService.loadDataSecciones(secciones, ciclo, mapGpoSecciones);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataDocentesSecciones");
        Map<String, DocenteSeccion> mapDocenteSecciones = progDataService.loadDataDocentesSecciones(docentesSecciones, mapSecciones, mapDocentes);
        t2 = System.currentTimeMillis();
        logger.debug("\tloadDataDocentesSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarDocenteSecciones");
        progDataService.revisarDocenteSecciones(mapDocenteSecciones, ciclo, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarDocenteSecciones ejecutado en {} mseg", (t2 - t1));

//        t1 = System.currentTimeMillis();
//        logger.debug("loadDataMatriculados");
//        Map<String, MatriculaResumen> mapResumenes = loadDataMatriculados(matriculaSecciones, mapSecciones, ciclo, ds);
//        t2 = System.currentTimeMillis();
//        logger.debug("\tloadDataMatriculados ejecutado en {} mseg", (t2 - t1));

//        t1 = System.currentTimeMillis();
//        logger.debug("revisarAlumnosMatriculados");
//        revisarAlumnosMatriculados(ciclo, mapResumenes, mapBloqueados);
//        t2 = System.currentTimeMillis();
//        logger.debug("\trevisarAlumnosMatriculados ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarSecciones");
        progDataService.revisarSecciones(secciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarGrupoSecciones");
        progDataService.revisarGrupoSecciones(gruposSecciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarGrupoSecciones ejecutado en {} mseg", (t2 - t1));

        progDataService.detenerRevisionBloqueado();

        Map<Integer, Dia> dias = diaDAO.all().stream().collect(Collectors.toMap(x -> x.getNumeroDia(), x -> x));
        Map<Integer, Hora> horas = horaDAO.all().stream().collect(Collectors.toMap(x -> x.getNumero(), x -> x));
        Map<String, GrupoHoras> grupos = grupoHorasDAO.all().stream().collect(Collectors.toMap(x -> x.getCodigo(), x -> x));

        List<HorarioSeccion> horariosSeccion = crearHorarioSecciones(rutaFileHorarioSecciones, mapSecciones, dias, horas, ciclo);
        List<DiaHoraGrupo> horariosGrupo = crearHorarioGrupos(rutaFileHorarioGrupos, dias, horas, grupos, ciclo);

        t1 = System.currentTimeMillis();
        logger.debug("revisarHorarioSecciones");
        progDataService.revisarHorarioSecciones(horariosSeccion, ciclo);
        
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarHorarioSecciones ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("revisarHorarioGrupos");

        progDataService.revisarHorarioGrupos(horariosGrupo, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarHorarioGrupos ejecutado en {} mseg", (t2 - t1));
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
        for (Docente docente : docentes) {
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
            logger.debug("Guardando alumno {} de {}", loop, alumnos.size());
            Persona persona = alumno.getPersona();
            loadInfoPersona(persona, mapTiposDoc, mapEstadoCivil, mapPaises, mapUbicacion);
            List<Persona> personasVinculadas = progDataService.allPersonasByPer(persona, mapKeyPersonas, mapDNIPersonas, ds);
            persona = progDataService.savePersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            String emailCia = progDataService.extraerEmailCompania(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            logger.debug("\temail-cia {}", emailCia);
            Persona perso = progDataService.extraerDocumentoIdentidad(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            progDataService.changeDocumentoIdentidad(persona, personasVinculadas, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, mapKeyPersonas, mapDNIPersonas, ds);

            if (mapIdPersonas.get(persona.getId()) == null) {
                mapIdPersonas.put(persona.getId(), persona);
            }

            alumno.setPersona(persona);
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
            logger.debug("Guardando persona {} de {}", loop, personas.size());
            loadInfoPersona(persona, mapTiposDoc, mapEstadoCivil, mapPaises, mapUbicacion);
            List<Persona> personasVinculadas = progDataService.allPersonasByPer(persona, mapKeyPersonas, mapDNIPersonas, ds);
            progDataService.savePersona(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            //progDataService.savePersona(persona, mapTiposDoc, mapKeyPersonas, mapDNIPersonas, ds);
            String emailCia = progDataService.extraerEmailCompania(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            Persona perso = progDataService.extraerDocumentoIdentidad(persona, personasVinculadas, mapKeyPersonas, mapDNIPersonas, ds);
            progDataService.changeDocumentoIdentidad(persona, personasVinculadas, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, mapKeyPersonas, mapDNIPersonas, ds);
            loop++;
        }

    }

    private void revisarAlumnosMatriculados(CicloAcademico ciclo, Map<String, MatriculaResumen> mapResumenes, Map<String, AlumnoBlocked> mapBloqueados) {
        List<MatriculaResumen> alumnosResumen = matriculaResumenDAO.allByCiclo(ciclo);
        int loop = 1;
        for (MatriculaResumen aluResumen : alumnosResumen) {
            Alumno alumno = aluResumen.getAlumno();
            System.out.println(loop + ".- " + alumno.getCodigo() + " :::: ");
            loop++;
        }

        for (MatriculaResumen aluResumen : alumnosResumen) {
            progDataService.revisarAlumnoMatriculado(aluResumen, mapResumenes, mapBloqueados);
        }

        logger.debug("\trevisarAlumnosMatriculados envio {} alumnos a ser revisados", alumnosResumen.size());
        int procesadosAntes = -1;
        long t1 = System.currentTimeMillis();
        long t10 = System.currentTimeMillis();
        boolean verError = true;
        boolean iniciarTimer = false;
        for (;;) {
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
        Map<String, MatriculaResumen> mapResumenes = new LinkedHashMap();
        for (MatriculaSeccion matriSecc : matriculasSecciones) {
            progDataService.loadDataMatriculados(matriSecc, mapResumenes, mapSecciones, ciclo, ds);
        }

        long t1 = System.currentTimeMillis();
        int procesadosAntes = -1;
        for (;;) {
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
                logger.debug("\tloadDataMatriculados procesados {} de {}", procesados, matriculasSecciones.size());
                //t1 = System.currentTimeMillis();
            }
            if (t2 - t1 > 5000) {
                ver = true;
                t1 = System.currentTimeMillis();
            }

            procesadosAntes = procesados;
        }
        return mapResumenes;
    }

    private List<DiaHoraGrupo> crearHorarioGrupos(String rutaFile, Map<Integer, Dia> dias, Map<Integer, Hora> horas, Map<String, GrupoHoras> grupos, CicloAcademico ciclo) {
        List<DiaHoraGrupo> horarios = new ArrayList<>();

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

                String cicloCod = getCellStringValue(1, row);
                String gpo = getCellStringValue(2, row);
                String hdia = getCellStringValue(3, row);
                String diaNum = getCellStringValue(4, row);
                String horaNum = getCellStringValue(5, row);

                Dia dia = dias.get(Integer.parseInt(diaNum));
                Hora hora = horas.get(Integer.parseInt(horaNum));
                GrupoHoras grupo = grupos.get(gpo);
                DiaHoraGrupo diaHoraGrupo = diaHoraGrupoDAO.findByCicloAcademicoGrupoHorasDiaHora(ciclo, grupo, dia, hora);

                if (diaHoraGrupo == null) {
                    diaHoraGrupo = new DiaHoraGrupo();
                    diaHoraGrupo.setCicloAcademico(ciclo);
                    diaHoraGrupo.setDia(dia);
                    diaHoraGrupo.setGrupoHorario(grupo);
                    diaHoraGrupo.setHora(hora);
                }
                horarios.add(diaHoraGrupo);

            }
            return horarios;
        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
    }

    private List<HorarioSeccion> crearHorarioSecciones(String rutaFile, Map<String, Seccion> secciones, Map<Integer, Dia> dias, Map<Integer, Hora> horas, CicloAcademico cicloAcademico) {
        List<HorarioSeccion> horarios = new ArrayList<>();
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

                String clave = getCellStringValue(2, row);
                String hdia = getCellStringValue(3, row);
                String diaNum = getCellStringValue(4, row);
                String horaNum = getCellStringValue(5, row);
                String aulaCod = getCellStringValue(6, row);

                CicloAcademico ciclo = cicloAcademico;

                Seccion seccion = secciones.get(clave);
                Dia dia = dias.get(Integer.parseInt(diaNum));
                Hora hora = horas.get(Integer.parseInt(horaNum));
                HorarioSeccion horario = horarioSeccionDAO.findBySeccionDiaHora(seccion, dia, hora);
                if (horario == null) {
                    horario = new HorarioSeccion();
                    horario.setSeccion(seccion);
                    horario.setDia(dia);
                    horario.setHora(hora);
                }
                horarios.add(horario);

            }
            return horarios;
        } catch (FileNotFoundException ex) {
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

                if (StringUtils.isEmpty(codigo)) {
                    break;
                }

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
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }

        return alumnnos;
    }

    private List<MatriculaSeccion> crearMatriculasSecciones(String rutaFile) {
        List<MatriculaSeccion> matriculasSecciones = new ArrayList();
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
                String codigoAlumno = getCellStringValue(2, row);
                String codigoSeccion = getCellStringValue(3, row);
                Integer creditos = Integer.valueOf(getCellStringValue(4, row));

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                MatriculaSeccion alumnoSecc = new MatriculaSeccion(codigoAlumno, codigoSeccion, creditos);
                matriculasSecciones.add(alumnoSecc);
            }
            logger.debug("Se han leido un total de {} alumnos-secciones", loop);

        } catch (FileNotFoundException ex) {
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

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                DocenteSeccion profeSecc = new DocenteSeccion(principal, codigoDocente, codigoSeccion);
                docenteSecciones.add(profeSecc);

            }
            logger.debug("Se han leido un total de {} profesores-secciones", loop);

        } catch (FileNotFoundException ex) {
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

                if (StringUtils.isEmpty(paterno)) {
                    break;
                }

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

                Seccion seccion = new Seccion(clave, clave2, gpo, aula, gclave, tclave, vacantes, matriculados);
                secciones.add(seccion);
            }
            logger.debug("Se han leido un total de {} secciones", loop);

        } catch (FileNotFoundException ex) {
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

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                GrupoSeccion gpoSecc = new GrupoSeccion(gclave, curso, anexo);
                gpoSecciones.add(gpoSecc);
            }
            logger.debug("Se han leido un total de {} grupos-secciones", loop);

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
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
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
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

    private BigDecimal getCellNumericValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return new BigDecimal(cell.getNumericCellValue());
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
            return BigDecimal.ZERO;
        }
        if (dato.equals("-")) {
            throw new PhobosException("Valor de integer desconocido");
        }
        if (dato.equals(",")) {
            return BigDecimal.ZERO;
        }
        if (StringUtils.isEmpty(dato)) {
            return null;
        }

        return new BigDecimal(dato);
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
