package pe.edu.lamolina.pivot.controller.oficinas.matricula.omisoeleccion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoOmisoEleccionDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class OmisoEleccionServiceImp implements OmisoEleccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoOmisoEleccionDAO alumnoOmisoEleccionDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Override
    @Transactional
    public List<String> cargarDeudas(MultipartFile file, String codigo, DataSessionPivot ds) {
        List<CicloAcademico> cicloAcademicos = cicloAcademicoDAO.allByCodigo(codigo);

        List<String> observados = new ArrayList<>();
        List<AlumnoOmisoEleccion> alumnoOmisiones = new ArrayList<>();
//        String ruta = guardarArchivo(file);

        procesarArchivo(file, cicloAcademicos, observados, alumnoOmisiones, ds);

        for (AlumnoOmisoEleccion deuda : alumnoOmisiones) {
            alumnoOmisoEleccionDAO.save(deuda);
        }
        return observados;
    }

    private String guardarArchivo(MultipartFile file) {
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

    private void procesarArchivo(MultipartFile file, List<CicloAcademico> cicloAcademicos, List<String> observados, List<AlumnoOmisoEleccion> deudas, DataSessionPivot ds) {
        List<AlumnoOmisoEleccion> alumnoOmisoEleccions = alumnoOmisoEleccionDAO.allByCiclo(cicloAcademicos);
        Map<String, AlumnoOmisoEleccion> mapAlumno = TypesUtil.convertListToMap("key", alumnoOmisoEleccions);
        Map<Long, CicloAcademico> mapCiclos = TypesUtil.convertListToMap("modalidadEstudio.id", cicloAcademicos);
        Map<Long, String> mapObservados = new HashMap<>();
        Set<String> registrados = new HashSet<>();
        Date fechaRegistro = new Date();
        Usuario userRegistro = ds.getUsuario();
        try {
//            FileInputStream fis = new FileInputStream(ruta);

            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            Iterator<Row> rowIterator = workbook.getSheetAt(0).iterator();
            int fila;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                fila = row.getRowNum() + 1;
                if (row.getRowNum() < 1) {
                    continue;
                }

                String nroMatricula = getCellValue(0, row);
                String nombre = getCellValue(1, row);
                String motivo = getCellValue(2, row);
                String multa = getCellValue(3, row);

                if (StringUtils.isEmpty(nroMatricula) || StringUtils.isEmpty(motivo) || StringUtils.isEmpty(multa)) {
                    mapObservados.put(Long.parseLong("" + fila), "La fila  " + fila + " tiene campos vacíos.");
                    return;
                }

                Alumno alumnoBD = alumnoDAO.findByCodigo(nroMatricula);

                if (alumnoBD == null) {
                    mapObservados.put(Long.parseLong("" + fila), "La matricula  " + nroMatricula + " no existe. ( Fila " + fila + ")");
                    continue;
                }
                CicloAcademico cicloAcademico = mapCiclos.get(alumnoBD.getModalidadEstudio().getId());
                String key = alumnoBD.getId() + "-" + cicloAcademico.getId() + "-" + motivo;
                if (registrados.contains(key) || mapAlumno.get(key) != null) {
                    if (!mapObservados.containsKey(alumnoBD.getId())) {
                        mapObservados.put(alumnoBD.getId(), "El alumno con código " + nroMatricula + " ya tiene registrada una deuda de este tipo. (Fila " + fila + ")");
                    }
                    continue;
                }

                AlumnoOmisoEleccion omision = new AlumnoOmisoEleccion();
                omision.setCicloAcademico(cicloAcademico);
                omision.setAlumno(alumnoBD);
                omision.setEstadoEnum(DeudaEstadoEnum.DEU);
                omision.setMotivo(motivo);
                omision.setMulta(new BigDecimal(multa));
                omision.setFechaRegistro(fechaRegistro);
                omision.setUserRegistro(userRegistro);
                registrados.add(alumnoBD.getId() + "-" + cicloAcademico.getId() + "-" + motivo);
                deudas.add(omision);
            }

            observados.addAll(new ArrayList<>(mapObservados.values()));

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
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

    @Override
    public List<Alumno> allDeudaAlumno(DynatableFilter filter) {

        List<AlumnoOmisoEleccion> alumnoOmisoEleccions = alumnoOmisoEleccionDAO.allOrder(filter);
        Map<Long, List<AlumnoOmisoEleccion>> map = TypesUtil.convertListToMapList("alumno.id", alumnoOmisoEleccions);
        List<Alumno> alumnos = alumnoOmisoEleccions.stream().map(x -> x.getAlumno()).distinct().collect(Collectors.toList());

        for (Alumno alumno : alumnos) {
            List<AlumnoOmisoEleccion> alumnoOmisoEle = map.get(alumno.getId());
            alumno.setAlumnoOmisoEleccions(alumnoOmisoEle);
        }
        return alumnos;
    }

    @Override
    @Transactional
    public void saveOmision(AlumnoOmisoEleccion omisoEleccion, DataSessionPivot ds) {
        CicloAcademico cicloAcademicoMod = cicloAcademicoDAO.findByCodigoModalidadEstudio(omisoEleccion.getCicloAcademico().getCodigo(), omisoEleccion.getAlumno().getModalidadEstudio());
        Assert.isNotNull(cicloAcademicoMod, "Solo se puede agregar a alumnos de pregrado o posgrado.");
        omisoEleccion.setCicloAcademico(cicloAcademicoMod);
        AlumnoOmisoEleccion alumnoOmisoEleccionDB = alumnoOmisoEleccionDAO.findByAlumnoCicloMotivo(omisoEleccion);
        Assert.isNull(alumnoOmisoEleccionDB, "El alumno ya cuenta con un deuda para el ciclo asignado.");
        omisoEleccion.setEstadoEnum(DeudaEstadoEnum.DEU);
        omisoEleccion.setFechaRegistro(new Date());
        omisoEleccion.setUserRegistro(ds.getUsuario());
        alumnoOmisoEleccionDAO.save(omisoEleccion);
    }

    @Override
    @Transactional
    public void anularOmision(List<AlumnoOmisoEleccion> omisoEleccion, DataSessionPivot ds) {
        String motivoAnula = omisoEleccion.get(0).getMotivoAnulacion();
        for (AlumnoOmisoEleccion alumnoOmisoEleccion : omisoEleccion) {
            if (alumnoOmisoEleccion.getSeleccionado()) {
                alumnoOmisoEleccion.setEstadoEnum(DeudaEstadoEnum.ANU);
                alumnoOmisoEleccion.setMotivoAnulacion(motivoAnula);
                alumnoOmisoEleccion.setFechaAnulacion(new Date());
                alumnoOmisoEleccion.setUserAnulacion(ds.getUsuario());
                alumnoOmisoEleccionDAO.updateAnulacion(alumnoOmisoEleccion);
            }

        }
    }

    @Override
    public List<CicloAcademico> allCicloAcademico(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.allRegularPre(4, cicloAcademico);
    }

    @Override
    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds) {

        return alumnoDAO.allByName(nombre);
    }
}
