package pe.edu.lamolina.pivot.controller.academico.matriculable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_TU;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionTurnosAtencionDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class MatriculableServiceImp implements MatriculableService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    FacultadDAO facultadDAO;
    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    ConfiguracionTurnosAtencionDAO configuracionTurnosAtencionDAO;

    @Autowired
    MatriculableConnector matriculableConector;

    @Override
    public AlumnoResumen allResumenAlumnosByCicloRol(CicloAcademico cicloAcademico, String codigo, List<Long> filtros) {
        return matriculaResumenDAO.findResumenByCicloRolDynateable(cicloAcademico, codigo, filtros);
    }

    @Override
    public List<MatriculaResumen> allAlumnosByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros) {
        return matriculaResumenDAO.allByCicloRolDynatable(filter, cicloAcademico, codigo, filtros);
    }

    @Override
    public MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico) {
        return alumnoDAO.findResumenByCiclo(cicloAcademico);
    }

    @Override
    @Transactional
    public List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos) {
        return modalidadEstudioDAO.allByCodigos(codigos);
    }

    @Override
    @Transactional(readOnly = false)
    public void generar(CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        ModalidadEstudio pre = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        ModalidadEstudio epg = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.EPG);

        List<SituacionAcademica> situacionesPregrado = situacionAcademicaDAO.allByCodes(
                Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_3U, S_2U, S_4U, S_6U, S_TU));
        List<SituacionAcademica> situacionesPosgrado = situacionAcademicaDAO.allByCodes(
                Arrays.asList(S_N, S_1, S_2, S_3, S_5));

        List<Alumno> pregrados = alumnoDAO.allBySituaciones(pre, situacionesPregrado);
        List<Alumno> posgrados = alumnoDAO.allBySituaciones(epg, situacionesPosgrado);

        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(ciclo);
        Map<String, MatriculaResumen> mapMatriculables = TypesUtil.convertListToMap("alumno.codigo", matriculables);

        for (Alumno alumno : pregrados) {
            MatriculaResumen matri = mapMatriculables.get(alumno.getCodigo());
            if (matri != null) {
                continue;
            }

            matri = new MatriculaResumen();
            matri.setAlumno(alumno);
            matri.setCicloAcademico(ciclo);
            matri.setSituacionInicio(alumno.getSituacionAcademica());

            matri.setCreditosMatriculados(0);
            matri.setCreditosRetirados(0);
            matri.setCursosMatriculados(0);
            matri.setCursosRetirados(0);
            matri.setPorcentajeAvance(0);
            matri.setNotaAcumulada("0");
            matri.setNotaAvance("0");
            matri.setNotaFinal("0");
            matri.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumenDAO.save(matri);
        }

        for (Alumno alumno : posgrados) {
            MatriculaResumen matri = mapMatriculables.get(alumno.getCodigo());
            if (matri != null) {
                continue;
            }

            matri = new MatriculaResumen();
            matri.setAlumno(alumno);
            matri.setCicloAcademico(ciclo);
            matri.setSituacionInicio(alumno.getSituacionAcademica());

            matri.setCreditosMatriculados(0);
            matri.setCreditosRetirados(0);
            matri.setCursosMatriculados(0);
            matri.setCursosRetirados(0);
            matri.setPorcentajeAvance(0);
            matri.setNotaAcumulada("0");
            matri.setNotaAvance("0");
            matri.setNotaFinal("0");
            matri.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumenDAO.save(matri);
        }
        CicloAcademico cicloAcademicoUpd = new CicloAcademico();
        cicloAcademicoUpd.setId(ciclo.getId());
        cicloAcademicoUpd.setFechaMatriculables(today.toDate());
        cicloAcademicoDAO.updateFechaMatriculables(cicloAcademicoUpd);
    }

    @Override
    @Transactional(readOnly = false)
    public void generarPrioridad(CicloAcademico ciclo) {
        DateTime today = new DateTime();
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo.getId());

        if (cicloBD.getFechaMatriculables() == null) {
            throw new PhobosException("Primero debe generar los Alumnos matriculables");
        }

        cicloBD.setFechaPrioridades(today.toDate());
        cicloAcademicoDAO.updateFechaPrioridades(cicloBD);

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allActivosRegularesByCicloResumen(cicloBD);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumno.id", alumnosCiclos);
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(cicloBD);

        int cachimbos = 8000;
        int escuela = 10000;
        for (MatriculaResumen matriculable : matriculables) {
            matriculable.setPrioridad(null);
            matriculable.setPuntajePrioridad(null);
            matriculable.setTurnoAtencion(null);

            SituacionAcademica sit = matriculable.getAlumno().getSituacionAcademica();
            if (Arrays.asList(S_8, S_9).contains(sit.getCodigoEnum())) {
                matriculable.setPrioridad(BigDecimal.valueOf(cachimbos));
                matriculable.setPuntajePrioridad(BigDecimal.ZERO);
                cachimbos++;
                continue;
            }

            ModalidadEstudio modalidad = matriculable.getAlumno().getModalidadEstudio();
            if (Arrays.asList(ModalidadEstudioEnum.EPG, ModalidadEstudioEnum.ESP).contains(modalidad.getCodigoEnum())) {
                matriculable.setPrioridad(BigDecimal.valueOf(escuela));
                escuela++;
                continue;
            }

            AlumnoCiclo alumnoCiclo = mapAlumnoCiclo.get(matriculable.getAlumno().getId());
            if (alumnoCiclo != null) {
                matriculableConector.procesarPrioridadAlumno(matriculable, alumnoCiclo);
            }
        }

        List<MatriculaResumen> matriculablesConPuntaje = matriculables.stream()
                .filter(x -> (x.getPuntajePrioridad() != null && x.getPuntajePrioridad().compareTo(BigDecimal.ZERO) != 0))
                .collect(Collectors.toList());
        Collections.sort(matriculablesConPuntaje, (p1, p2) -> p2.getPuntajePrioridad().compareTo(p1.getPuntajePrioridad()));

        List<MatriculaResumen> matriculablesUltimoCiclo = matriculablesConPuntaje.stream()
                .filter(x -> x.getAlumno().getCreditosAprobados() > 180)
                .collect(Collectors.toList());

        int indice = 0;
        for (MatriculaResumen mr : matriculablesUltimoCiclo) {
            indice++;
            mr.setPrioridad(BigDecimal.valueOf(indice));
        }

        for (MatriculaResumen mr : matriculablesConPuntaje) {
            if (mr.getPrioridad() == null) {
                indice++;
                mr.setPrioridad(BigDecimal.valueOf(indice));
            }
        }

        for (MatriculaResumen mr : matriculables) {
            if (mr.getPrioridad() == null) {
                indice++;
                mr.setPrioridad(BigDecimal.valueOf(indice));
            }
        }

        for (MatriculaResumen mr : matriculables) {
            matriculaResumenDAO.update(mr);
        }
    }

    public void asignarTurno(CicloAcademico ciclo) {

    }

    @Override
    public List<ConfiguracionTurnosAtencion> allConfiguracionTurnoByCiclo(CicloAcademico cicloAcademico) {
        return configuracionTurnosAtencionDAO.allByCiclo(cicloAcademico);
    }

    @Override
    @Transactional
    public void procesarTurnoMatricula(CicloAcademico ciclo, Long configuracionTurnoAtencion) {
        DateTime today = new DateTime();
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo.getId());

        if (cicloBD.getFechaPrioridades() == null) {
            throw new PhobosException("Primero debe procesar las prioridades de los Alumnos");
        }

        cicloBD.setFechaTurnosAsignados(today.toDate());
        cicloAcademicoDAO.updateFechaPrioridades(ciclo);

        ConfiguracionTurnosAtencion configuracionTurnosAtencion = configuracionTurnosAtencionDAO.find(configuracionTurnoAtencion);
        List<TurnoAtencion> turnosAtencion = turnoAtencionDAO.allByConfiguracion(configuracionTurnosAtencion);

        for (TurnoAtencion turnoAtencionEach : turnosAtencion) {
            matriculaResumenDAO.updateTurnoAtencion(ciclo, turnoAtencionEach);
        }

    }

    @Override
    @Transactional
    public void loadEgresados(MultipartFile file) {
        logger.debug("Service File {}");
        String rutaFile = saveEgresados(file);
        cargarEgresados(rutaFile);
    }

    private String saveEgresados(MultipartFile file) {
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

    private List<Egresado> cargarEgresados(String rutaFile) {

        List<Egresado> lista;
        try {
            lista = new ArrayList<>();
            FileInputStream fis = new FileInputStream(rutaFile);
            String extension = FilenameUtils.getExtension(rutaFile);

            if (!extension.equals("xlsx")) {
                throw new PhobosException("El archivo debe tener la extensión .xlsx");
            }

            Workbook myWorkBook = new XSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String codigoAlumno = getCellValue(1, row);
                String codigoCarrera = getCellValue(3, row);
                String codigoFacultad = getCellValue(2, row);
                String codigoCiclo = getCellValue(5, row);
                Integer creditosAcumulados = TypesUtil.getInt(getCellValue(5, row));
                Integer creditosAprobadosAcumulados = TypesUtil.getInt(getCellValue(8, row));
                Integer puntajeAcumulado = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(9, row)) ? getCellValue(9, row) : null);
                BigDecimal promedioPonderadoAcumulado = TypesUtil.getBigDecimal(getCellValue(10, row));
                //  BigDecimal promedioAcumuladoMerito= TypesUtil.getBigDecimal(getCellValue(10, row));
                BigDecimal promedioGraduacion = TypesUtil.getBigDecimal(StringUtils.isNotBlank(getCellValue(11, row)) ? getCellValue(11, row) : null);
                Integer omg = TypesUtil.getInt(getCellValue(14, row));
                Integer omgf = TypesUtil.getInt(getCellValue(12, row));
                Integer omgCarrera = TypesUtil.getInt(getCellValue(13, row));
                Integer cuadrohonorCiclo = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(28, row)) ? getCellValue(28, row) : null);
                Integer quintoSuperiorCiclo = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(27, row)) ? getCellValue(27, row) : null);
                Integer tercioSuperiorCiclo = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(26, row)) ? getCellValue(26, row) : null);

                Integer cuadroHonorFacultad = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(25, row)) ? getCellValue(25, row) : null);
                Integer quintoSupFacultad = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(18, row)) ? getCellValue(18, row) : null);
                Integer tercioSupFacultad = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(15, row)) ? getCellValue(15, row) : null);

                Integer cuadroHonorCarrera = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(24, row)) ? getCellValue(24, row) : null);
                Integer quintoSupCarrera = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(19, row)) ? getCellValue(19, row) : null);
                Integer tercioSupCarrera = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(15, row)) ? getCellValue(15, row) : null);
                /*
                Date fechaEgresado;
                GradoAcademico grado;
                Date fechaGraduacion;
                TituloAcademico tituloAcademico;
                Date fechaTitulacion;

                Usuario usuarioRegistroEgresado;
                Date fechaRegistroEgresado;
                Usuario usuarioRegistroGraduado;
                Date fechaRegistroGraduado;
                Usuario usuarioRegistroTitulado;
                Date fechaRegistroTitulado;
                 */

                Egresado egresado = new Egresado();

                egresado.setCreditosAcumulados(creditosAcumulados);
                egresado.setCreditosAprobadosAcumulados(creditosAprobadosAcumulados);
                egresado.setCuadroHonorCarrera(cuadroHonorCarrera);
                egresado.setCuadroHonorCiclo(cuadrohonorCiclo);
                egresado.setCuadroHonorFacultad(cuadroHonorFacultad);
                egresado.setEsPrincipal(BigDecimal.ZERO.intValue());

                // egresado.setFechaEgresado(fechaEgresado);
                // egresado.setFechaGraduacion(fechaGraduacion);
                // egresado.setFechaRegistroEgresado(fechaRegistroEgresado);
                // egresado.setFechaRegistroGraduado(fechaRegistroGraduado);
                //egresado.setFechaRegistroTitulado(fechaRegistroTitulado);
                // egresado.setFechaTitulacion(fechaTitulacion);
                // egresado.setGrado(grado);
                egresado.setOrdenMeritoCarrera(omgCarrera);
                egresado.setOrdenMeritoCiclo(omg);
                egresado.setOrdenMeritoFacultad(omgf);
                egresado.setPromedioAcumulado(promedioPonderadoAcumulado);
                //  egresado.setPromedioAcumuladoMerito(promedioGraduacion);
                egresado.setPromedioGraduacion(promedioGraduacion);
                egresado.setPuntajeAcumulado(puntajeAcumulado);
                egresado.setQuintoSuperiorCarrera(quintoSupCarrera);
                egresado.setQuintoSuperiorCiclo(quintoSuperiorCiclo);
                egresado.setQuintoSuperiorFacultad(quintoSupFacultad);
                egresado.setTercioSuperiorCarrera(tercioSupCarrera);
                egresado.setTercioSuperiorCiclo(tercioSuperiorCiclo);
                egresado.setTercioSuperiorFacultad(tercioSupFacultad);

                // egresado.setTitulo(titulo);
                // egresado.setUserRegistroEgresado(Long.MIN_VALUE);
                // egresado.setUserRegistroGraduado(Long.MIN_VALUE);
                // egresado.setUserRegistroTitulado(Long.MIN_VALUE);
                matriculableConector.procesarEgresado(codigoAlumno, codigoCarrera, codigoFacultad, codigoCiclo, egresado);
            }
            logger.debug("Se han leido un total de {} filas", loop);
        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
        return lista;
    }

    private String getCellValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String dato = cell.getStringCellValue();
        if (dato != null) {
            dato = StringUtils.replaceChars(dato, '\t', ' ');
            dato = StringUtils.replaceChars(dato, '\r', ' ');
            dato = StringUtils.replaceChars(dato, '\n', ' ');
            dato = StringUtils.replaceChars(dato, ',', ' ');
            dato = StringUtils.replaceChars(dato, '|', ' ');
            dato = StringUtils.trim(dato);
        }
        return dato;
    }

    private BigDecimal getPuntaje(String dato, Integer slace) {
        if (StringUtils.isEmpty(dato)) {
            return null;
        }
        return new BigDecimal(dato).setScale(slace, RoundingMode.DOWN);
    }

}
