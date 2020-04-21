package pe.edu.lamolina.amauta.controller.oficinas.matricula.restriccionmatricula;

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
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;
import pe.edu.lamolina.model.enums.DeudaAlumnoEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.dao.academico.DeudaMaterialAlumnoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;

@Service
@Transactional(readOnly = true)
public class RestriccionMatriculaServiceImp implements RestriccionMatriculaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DeudaMaterialAlumnoDAO deudaAlumnoDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    private List returnEmpty(DynatableFilter filter) {
        filter.setFiltered(0);
        filter.setTotal(0);
        return new ArrayList();
    }

    private List<Oficina> findOficinas(DataSessionPivot ds) {
        List<Oficina> oficinas = ds.getOficinas();
        if (oficinas.stream().filter(x -> x.isOficinaOera()).findAny().orElse(null) != null) {
            oficinas = this.allOficina();
        }
        return oficinas;
    }

    @Override
    public List<DeudaMaterialAlumno> allDeudaAlumno(DynatableFilter filter, DataSessionPivot ds) {
        List<Oficina> list = new ArrayList<>();
        if (filter.getQueries() == null) {
            list = this.findOficinas(ds);
            return deudaAlumnoDAO.allByDynatableTipoDeuda(filter, list);
        }

        String oficina = (String) filter.getQueries().get("oficina");
        if (StringUtils.isEmpty(oficina)) {
            list = this.findOficinas(ds);
            return deudaAlumnoDAO.allByDynatableTipoDeuda(filter, list);
        }

        Oficina tipo = new Oficina(oficina);
        list.add(tipo);
        return deudaAlumnoDAO.allByDynatableTipoDeuda(filter, list);
    }

    @Override
    public List<Oficina> allOficina() {
        return oficinaDAO.all();
    }

    @Override
    @Transactional
    public void anularDeuda(DeudaMaterialAlumno deudaForm, DataSessionPivot ds) {
        DeudaMaterialAlumno deudaBD = deudaAlumnoDAO.find(deudaForm.getId());
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
    public List<String> cargarDeudas(MultipartFile file, Oficina oficina, DataSessionPivot ds) {
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());

        if (oficinaBD == null) {
            throw new PhobosException("El tipo de deuda seleccionado no es válido");
        }

        List<String> observados = new ArrayList<>();
        List<DeudaMaterialAlumno> deudas = new ArrayList<>();
        String ruta = guardarArchivo(file);

        procesarArchivo(ruta, oficinaBD, observados, deudas, ds);

        for (DeudaMaterialAlumno deuda : deudas) {
            deudaAlumnoDAO.save(deuda);
        }
        return observados;
    }

    private String guardarArchivo(MultipartFile file) {
        try {
            String fileName = TypesUtil.getUnixTime() + "." + TypesUtil.getClean(file.getOriginalFilename());
            FileHelper.createDirectory(GlobalConstantine.TMP_DIR);
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;

            FileHelper.saveToDisk(file, absoluteName);
            return absoluteName;
        } catch (IOException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        }
    }

    private boolean deudaRepetida(DeudaMaterialAlumno deuda) {
        return deudaAlumnoDAO.findByTipoAlumno(deuda.getOficina(), deuda.getAlumno()) != null;
    }

    private void procesarArchivo(String ruta, Oficina oficina, List<String> observados, List<DeudaMaterialAlumno> deudas, DataSessionPivot ds) {
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
                    return;
                }

                Alumno alumnoBD = alumnoDAO.findByCodigo(nroMatricula);

                if (alumnoBD == null) {
                    String error = " (Fila " + fila + ")";
                    mapObservados.put(Long.parseLong(fila + ""), "No existe un alumno con el número de matrícula " + nroMatricula + error);
                }

                DeudaMaterialAlumno deuda = new DeudaMaterialAlumno();
                deuda.setOficina(oficina);
                deuda.setAlumno(alumnoBD);
                deuda.setDescripcion(descripcion);
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
    public void levantarDeuda(DeudaMaterialAlumno deuda, DataSessionPivot ds) {
        DeudaMaterialAlumno deudaBD = deudaAlumnoDAO.find(deuda.getId());

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
    public void guardarDeuda(DeudaMaterialAlumno deudaForm) {
        DeudaMaterialAlumno deudaBD = deudaAlumnoDAO.find(deudaForm.getId());
        if (deudaBD == null) {
            throw new PhobosException("Deuda no encontrada");
        }

        deudaBD.setDescripcion(deudaForm.getDescripcion());
        deudaAlumnoDAO.update(deudaBD);

    }

    @Override
    @Transactional
    public void save(DeudaMaterialAlumno deuda, DataSessionPivot ds) {
        deuda.setEstado(DeudaAlumnoEstadoEnum.REST);
        deuda.setFechaRegistro(new Date());
        deuda.setUserRegistro(ds.getUsuario());
        deudaAlumnoDAO.save(deuda);
    }
}
