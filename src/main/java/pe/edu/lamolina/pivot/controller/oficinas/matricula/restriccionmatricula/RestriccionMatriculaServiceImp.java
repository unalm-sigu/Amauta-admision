package pe.edu.lamolina.pivot.controller.oficinas.matricula.restriccionmatricula;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import pe.edu.lamolina.model.academico.DeudaAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaAlumno;
import pe.edu.lamolina.model.enums.DeudaAlumnoEstadoEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.DeudaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoDeudaAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class RestriccionMatriculaServiceImp implements RestriccionMatriculaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    TipoDeudaAlumnoDAO tipoDeudaAlumnoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Override
    public List<DeudaAlumno> allDeudaAlumno(DynatableFilter filter) {
        return deudaAlumnoDAO.allByDynatable(filter);
    }

    @Override
    public List<TipoDeudaAlumno> allTipoDeudaAlumno() {
        return tipoDeudaAlumnoDAO.all();
    }

    @Override
    @Transactional
    public void anularDeuda(DeudaAlumno deudaForm, DataSessionPivot ds) {
        DeudaAlumno deudaBD = deudaAlumnoDAO.find(deudaForm.getId());
        if (deudaBD == null) {
            throw new PhobosException("Deuda no encontrada");
        }

        deudaBD.setMotivoAnulacion(deudaForm.getMotivoAnulacion());
        deudaBD.setFechaDesactiva(new Date());
        deudaBD.setUserDesactivacion(ds.getUsuario());
        deudaBD.setEstado(DeudaAlumnoEstadoEnum.ANU);
        deudaAlumnoDAO.update(deudaBD);

    }

    @Override
    @Transactional
    public List<String> cargarDeudas(MultipartFile file, TipoDeudaAlumno tipo, DataSessionPivot ds) {
        TipoDeudaAlumno tipoBD = tipoDeudaAlumnoDAO.find(tipo.getId());

        if (tipoBD == null) {
            throw new PhobosException("El tipo de deuda seleccionado no es válido");
        }

        List<String> observados = new ArrayList<>();
        List<DeudaAlumno> deudas = new ArrayList<>();
        String ruta = guardarArchivo(file);

        procesarArchivo(ruta, tipoBD, observados, deudas, ds);

        for (DeudaAlumno deuda : deudas) {
            deudaAlumnoDAO.save(deuda);
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

    private boolean deudaRepetida(DeudaAlumno deuda) {
        return deudaAlumnoDAO.findByTipoAlumno(deuda.getTipoDeuda(), deuda.getAlumno()) != null;
    }

    private void procesarArchivo(String ruta, TipoDeudaAlumno tipo, List<String> observados, List<DeudaAlumno> deudas, DataSessionPivot ds) {
        Map<Long, String> mapObservados = new HashMap<>();
        Set<Long> registrados = new HashSet<>();
        Date fechaRegistro = new Date();
        Usuario userRegistro = ds.getUsuario();
        try {
            FileInputStream fis = new FileInputStream(ruta);
            String extension = FilenameUtils.getExtension(ruta);

            if (extension.equals("xls")) {
                throw new PhobosException("El archivo debe tener la extensión .xlsx");
            }

            Workbook wb = WorkbookFactory.create(fis);
            Iterator<Row> rowIterator = wb.getSheetAt(0).iterator();
            int fila;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                fila = row.getRowNum() + 1;
                if (row.getRowNum() < 1) {
                    continue;
                }

                String nroMatricula = getCellValue(0, row);
                String nombre = getCellValue(1, row);
                String descripcion = getCellValue(2, row);

                if (StringUtils.isEmpty(nroMatricula) || StringUtils.isEmpty(descripcion)) {
                    continue;
                }

                Alumno alumnoBD = alumnoDAO.findByCodigo(nroMatricula);

                if (alumnoBD == null) {
                    String error = " (Fila " + fila + ")";
                    throw new PhobosException("No existe un alumno con el número de matrícula " + nroMatricula + error);
                }

                DeudaAlumno deuda = new DeudaAlumno();
                deuda.setTipoDeuda(tipo);
                deuda.setAlumno(alumnoBD);
                deuda.setDescripcion(descripcion);

                if (registrados.contains(alumnoBD.getId()) || deudaRepetida(deuda)) {
                    if (!mapObservados.containsKey(alumnoBD.getId())) {
                        mapObservados.put(alumnoBD.getId(), "El alumno con código " + nroMatricula + " ya tiene registrada una deuda. (Fila " + fila + ")");
                    }
                    continue;
                }

                deuda.setEstado(DeudaAlumnoEstadoEnum.REST);
                deuda.setFechaRegistro(fechaRegistro);
                deuda.setUserRegistro(userRegistro);
                registrados.add(alumnoBD.getId());
                deudas.add(deuda);
            }

            observados.addAll(new ArrayList<>(mapObservados.values()));

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        } catch (InvalidFormatException ex) {
            throw new PhobosException("El formato del archivo no es el correcto");
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
    @Transactional
    public void levantarDeuda(DeudaAlumno deuda, DataSessionPivot ds) {
        DeudaAlumno deudaBD = deudaAlumnoDAO.find(deuda.getId());

        if (deudaBD == null) {
            throw new PhobosException("Deuda no encontrada");
        }

        deudaBD.setEstado(DeudaAlumnoEstadoEnum.LEV);
         deudaBD.setFechaDesactiva(new Date());
        deudaBD.setUserDesactivacion(ds.getUsuario());
        deudaAlumnoDAO.update(deudaBD);
    }

    @Override
    @Transactional
    public void guardarDeuda(DeudaAlumno deudaForm) {
        DeudaAlumno deudaBD = deudaAlumnoDAO.find(deudaForm.getId());
        if (deudaBD == null) {
            throw new PhobosException("Deuda no encontrada");
        }

        deudaBD.setDescripcion(deudaForm.getDescripcion());
        deudaAlumnoDAO.update(deudaBD);

    }
}
