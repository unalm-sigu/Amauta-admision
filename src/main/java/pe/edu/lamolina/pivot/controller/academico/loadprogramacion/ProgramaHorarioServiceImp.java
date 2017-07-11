package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.model.academico.MatriculaSeccion;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void loadArchivosHorario(MultipartFile[] files, CicloAcademico ciclo, DataSessionPivot ds) {
        String rutaFileGpoSecciones = saveFile(files[0]);
        String rutaFileSecciones = saveFile(files[1]);
        String rutaFilePersonas = saveFile(files[2]);
        String rutaFileProfes = saveFile(files[3]);
        String rutaFileProfeSecciones = saveFile(files[4]);
        String rutaFileAlumno = saveFile(files[5]);
        String rutaFileAlumnoSecciones = saveFile(files[6]);

        List<GrupoSeccion> gruposSecciones = crearGruposSecciones(rutaFileGpoSecciones);
        List<Seccion> secciones = crearSecciones(rutaFileSecciones);
        List<Persona> personas = crearPersonas(rutaFilePersonas);
        List<Docente> docentes = crearDocentes(rutaFileProfes);
        List<DocenteSeccion> docentesSecciones = crearDocenteSecciones(rutaFileProfeSecciones);
        List<Alumno> alumnos = crearAlumnos(rutaFileAlumno);
        List<MatriculaSeccion> matriculaSecciones = crearMatriculasSecciones(rutaFileAlumnoSecciones);

        Map<String, AlumnoBlocked> mapBloqueados = new LinkedHashMap();
        progDataService.revisarBloqueados(mapBloqueados);

        long t1 = System.currentTimeMillis();
        logger.debug("savePersonas");
        this.savePersonas(personas, ds);
        long t2 = System.currentTimeMillis();
        logger.debug("\tsavePersonas ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("saveAlumnos");
        this.saveAlumnos(alumnos, ds);
        t2 = System.currentTimeMillis();
        logger.debug("\tsaveAlumnos ejecutado en {} mseg", (t2 - t1));

        t1 = System.currentTimeMillis();
        logger.debug("loadDataDocentes");
        Map<String, Docente> mapDocentes = this.saveDocentes(docentes, ds);
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
        logger.debug("revisarGrupoSecciones");
        progDataService.revisarGrupoSecciones(gruposSecciones, ciclo);
        t2 = System.currentTimeMillis();
        logger.debug("\trevisarGrupoSecciones ejecutado en {} mseg", (t2 - t1));

        progDataService.detenerRevisionBloqueado();

    }

    private Map<String, Docente> saveDocentes(List<Docente> docentes, DataSessionPivot ds) {
        Map<String, Docente> mapDocentes = new LinkedHashMap();

        List<TipoDocIdentidad> tiposDoc = tipoDocIdentidadDAO.all();
        Map<String, TipoDocIdentidad> mapTiposDoc = MapUtil.storeItems("simbolo", tiposDoc);

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
            persona = progDataService.savePersona(persona, mapTiposDoc, ds);
            String emailCia = progDataService.extraerEmailCompania(persona, ds);
            Persona perso = progDataService.extraerDocumentoIdentidad(persona, ds);
            progDataService.changeDocumentoIdentidad(persona, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, ds);

            docente.setPersona(persona);
            docente = progDataService.saveDocente(docente, modalidad, mapDptos, ds);
            mapDocentes.put(docente.getCodigo(), docente);
            loop++;
        }

        progDataService.anularDocentes(mapDocentes, modalidad, ds);

        return mapDocentes;
    }

    private void saveAlumnos(List<Alumno> alumnos, DataSessionPivot ds) {
        List<TipoDocIdentidad> tiposDoc = tipoDocIdentidadDAO.all();
        Map<String, TipoDocIdentidad> mapTiposDoc = MapUtil.storeItems("simbolo", tiposDoc);
        long loop = 1;
        for (Alumno alumno : alumnos) {
            logger.debug("Guardando alumno {} de {}", loop, alumnos.size());
//            if (loop < 5320) {
//                loop++;
//                continue;
//            }
            Persona persona = alumno.getPersona();
            persona = progDataService.savePersona(persona, mapTiposDoc, ds);
            String emailCia = progDataService.extraerEmailCompania(persona, ds);
            Persona perso = progDataService.extraerDocumentoIdentidad(persona, ds);
            progDataService.changeDocumentoIdentidad(persona, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, ds);

            alumno.setPersona(persona);
            progDataService.saveAlumno(alumno, ds);
            loop++;
        }

    }

    private void savePersonas(List<Persona> personas, DataSessionPivot ds) {
        List<TipoDocIdentidad> tiposDoc = tipoDocIdentidadDAO.all();
        Map<String, TipoDocIdentidad> mapTiposDoc = MapUtil.storeItems("simbolo", tiposDoc);
        long loop = 1;
        for (Persona persona : personas) {
            logger.debug("Guardando persona {} de {}", loop, personas.size());
            progDataService.savePersona(persona, mapTiposDoc, ds);
            String emailCia = progDataService.extraerEmailCompania(persona, ds);
            Persona perso = progDataService.extraerDocumentoIdentidad(persona, ds);
            progDataService.changeDocumentoIdentidad(persona, perso.getTipoDocumento(), perso.getNumeroDocIdentidad(), emailCia, ds);
            loop++;
        }

    }

    private void revisarAlumnosMatriculados(CicloAcademico ciclo, Map<String, MatriculaResumen> mapResumenes, Map<String, AlumnoBlocked> mapBloqueados) {
        List<MatriculaResumen> alumnosResumen = matriculaResumenDAO.allByCiclo(ciclo);
        for (MatriculaResumen aluResumen : alumnosResumen) {
            progDataService.revisarAlumnoMatriculado(aluResumen, mapResumenes, mapBloqueados);
        }

        logger.debug("\trevisarAlumnosMatriculados envio {} alumnos a ser revisados", alumnosResumen.size());
        int procesadosAntes = -1;
        for (;;) {
            boolean salir = true;
            int procesados = 0;
            for (MatriculaResumen aluResumen : alumnosResumen) {
                if (aluResumen.getProcesado() == 0) {
                    salir = false;
                } else {
                    procesados++;
                }
            }
            if (salir) {
                break;
            }

            if (procesadosAntes != procesados) {
                logger.debug("\trevisarAlumnosMatriculados procesados {} de {}", procesados, alumnosResumen.size());
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
                    if (ver) {
                        System.out.println("\tElemento sin procesar alumno:" + matriSecc.getCodigoAlumno() + " seccion:" + matriSecc.getCodigoSeccion());
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
                t1 = System.currentTimeMillis();
            } else {
                if (t2 - t1 > 5000) {
                    ver = true;
                    t1 = System.currentTimeMillis();
                }
            }
            procesadosAntes = procesados;
        }
        return mapResumenes;
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

                String codigo = getCellValue(1, row);
                String codigoEspecialidad = getCellValue(2, row);
                String codigoPostgrado = getCellValue(3, row);
                String situacion = getCellValue(4, row);
                String email = getCellValue(5, row);
                String paterno = getCellValue(8, row);
                String materno = getCellValue(9, row);
                String nombres = getCellValue(10, row);
                String tipoDocumento = getCellValue(11, row);
                String numeroDoc = getCellValue(12, row);

                if (StringUtils.isEmpty(codigo)) {
                    break;
                }

                if (StringUtils.isEmpty(tipoDocumento)) {
                    tipoDocumento = "DNI";
                }
                if (StringUtils.isEmpty(email)) {
                    email = null;
                }

                Persona persona = new Persona(paterno, materno, nombres, numeroDoc, tipoDocumento);
                Alumno alumno = new Alumno(codigo, codigoEspecialidad, codigoPostgrado, situacion, email);
                alumno.setPersona(persona);
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

                String ciclo = getCellValue(1, row);
                String codigoAlumno = getCellValue(2, row);
                String codigoSeccion = getCellValue(3, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                MatriculaSeccion alumnoSecc = new MatriculaSeccion(codigoAlumno, codigoSeccion);
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

                String ciclo = getCellValue(1, row);
                String codigoDocente = getCellValue(2, row);
                String codigoSeccion = getCellValue(3, row);
                Integer principal = Integer.valueOf(getCellValue(4, row));

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

                String nro = getCellValue(0, row);
                String tipoDocumento = getCellValue(1, row);
                String numeroDoc = getCellValue(2, row);
                String codigo = getCellValue(3, row);
                String dpto = getCellValue(4, row);
                String paterno = getCellValue(5, row);
                String materno = getCellValue(6, row);
                String nombres = getCellValue(7, row);

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

                String paterno = getCellValue(1, row);
                String materno = getCellValue(2, row);
                String nombres = getCellValue(3, row);
                String tipoDoc = getCellValue(4, row);
                String numeroDoc = getCellValue(5, row);

                if (StringUtils.isEmpty(paterno)) {
                    break;
                }

                if (StringUtils.isEmpty(tipoDoc)) {
                    tipoDoc = "DNI";
                }

                Persona persona = new Persona(paterno, materno, nombres, numeroDoc, tipoDoc);
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

                String ciclo = getCellValue(1, row);
                String clave = getCellValue(2, row);
                String gpo = getCellValue(3, row);
                String aula = getCellValue(4, row);
                String gclave = getCellValue(5, row);
                String tclave = getCellValue(6, row);
                String clave2 = getCellValue(8, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                Seccion seccion = new Seccion(clave, clave2, gpo, aula, gclave, tclave);
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

                String ciclo = getCellValue(1, row);
                String gclave = getCellValue(2, row);
                String curso = getCellValue(3, row);

                if (StringUtils.isEmpty(ciclo)) {
                    break;
                }

                GrupoSeccion gpoSecc = new GrupoSeccion(gclave, curso);
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

    private String getCellValue(int pos, Row row) {
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

   
    
    

}
