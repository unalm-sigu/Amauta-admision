package pe.edu.lamolina.amauta.controller.academico.pronabec;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.*;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.pronabec.InformacionBeca;
import pe.edu.lamolina.model.pronabec.TipoBeca;
import pe.edu.lamolina.model.seguridad.Usuario;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;


@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class BecasPronabecServiceImp implements BecasPronabecService {

    private final BecasPronabecDAO becasPronabecDAO;
    private final TipoBecaPronabecDAO tipoBecaPronabecDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CarreraDAO carreraDAO;

    PersonaDAO personaDAO;
    MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<InformacionBeca> allByDynatable(DynatableFilter filter) {
        return becasPronabecDAO.allByFilter(filter);
    }

    @Override
    @Transactional
    public List<String> cargarBecados(MultipartFile file, DataSessionPivot ds) {
        List<String> observados = new ArrayList<>();
        List<InformacionBeca> becadosPronabec= new ArrayList<>();
        procesarArchivo(file, observados, becadosPronabec, ds);

        for(InformacionBeca becPro : becadosPronabec) {
            becasPronabecDAO.save(becPro);
        }
        return observados;
    }

    @Override
    public List<InformacionBeca> getHistorialBecas(InformacionBeca infoBeca) {
//        List<InformacionBeca> becas = new ArrayList<>();
//        becas = becasPronabecDAO.finByPersonaId(personaId);
        return  becasPronabecDAO.finByPersonaIds(infoBeca);
    }

    @Override
    public void saveBecado(InformacionBeca informacionBeca, DataSessionPivot ds) {
//        informacionBeca.setPersona(informacionBeca.getPersona());
        informacionBeca.setFechaRegistro(new Date());
        informacionBeca.setUsuario(ds.getUsuario());
        becasPronabecDAO.save(informacionBeca);
    }

    @Override
    public List<Persona> allPersonaAlumno(String nombre, DataSessionPivot ds) {
        return becasPronabecDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void eliminarBecado(Long id) {
        InformacionBeca informacionBecaDB = becasPronabecDAO.find(id);
        Long idBeca = informacionBecaDB.getId();
        if (informacionBecaDB != null) {
            becasPronabecDAO.delete(idBeca);
        } else {
            throw new PhobosException("El registro no fue encontrado.");
        }
    }

    @Override
    @Transactional
    public void anularBecado(Long id) {
        InformacionBeca informacionBecaDB = becasPronabecDAO.find(id);
        Long idBeca = informacionBecaDB.getId();
        if (informacionBecaDB != null) {
            //informacionBecaDB.setEstado("ANULADO");
            informacionBecaDB.setEstado("INACTIVO");
            becasPronabecDAO.updateEstado(informacionBecaDB);

        } else {
            throw new PhobosException("El registro no fue encontrado.");
        }

    }

    @Override
    @Transactional
    public void actualizarInformBecado(InformacionBeca informacionBecaForm, DataSessionPivot ds) {
        InformacionBeca informacionBecaDB = becasPronabecDAO.find(informacionBecaForm.getId());
        if(informacionBecaDB==null){
            throw new PhobosException("No tiene una informacion");
        }
        informacionBecaDB.setTipoBeca(informacionBecaForm.getTipoBeca());
        informacionBecaDB.setFechaFin(informacionBecaForm.getFechaFin());
        informacionBecaDB.setFechaInicio(informacionBecaForm.getFechaInicio());
        informacionBecaDB.setYearConvocatoria(informacionBecaForm.getYearConvocatoria());
        informacionBecaDB.setFechaOtorgamiento(informacionBecaForm.getFechaOtorgamiento());
        informacionBecaDB.setCarrera(informacionBecaForm.getCarrera());
        informacionBecaDB.setCondicion(informacionBecaForm.getCondicion());
        informacionBecaDB.setSituacion(informacionBecaForm.getSituacion());
        informacionBecaDB.setFechaRegistro(new Date());
        informacionBecaDB.setUsuario(ds.getUsuario());
        becasPronabecDAO.update(informacionBecaDB);
    }

    @Override
    public List<MatriculadosBecadosBean> allMatriculadosBecados(CicloAcademico cicloAcademico) {
        List<MatriculadosBecadosBean> listMatriculadosBecados = new ArrayList<>();
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        listMatriculadosBecados = becasPronabecDAO.allMatriculadosBecadosPregrado(cicloAcademico);
        return listMatriculadosBecados;
    }

    @Override
    public List<BecadosFilterBean> filterBecadosExcel(CicloAcademico cicloAcademico,BecadosFilterBean becadosFilterBean) {
        List<BecadosFilterBean> listBecadosExcel = new ArrayList<>();
        //TipoBeca tipoBecaCodigo = tipoBecaPronabecDAO.findByCodigo(tipoBeca.getCodigo());
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloAcademico cicloRegular = cicloAcademicoDAO.findActivo(modalidadEstudio);

        listBecadosExcel = becasPronabecDAO.allBecadosFilterExcel(cicloRegular,modalidadEstudio,becadosFilterBean);
        return listBecadosExcel;
    }

    @Override
    public List<CicloAcademico> allCicloRegular() {
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivoByModalidadEstudio(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.allMenorRegularPreByCantidad(14,cicloAcademico);
    }

    @Override
    public List<BecadosFilterBean> filterActualBecados(CicloAcademico cicloAcademico, BecadosFilterBean becadosFilterBean) {
        List<BecadosFilterBean> listBecadosExcel = new ArrayList<>();
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloAcademico cicloRegular = cicloAcademicoDAO.findActivo(modalidadEstudio);

        listBecadosExcel = becasPronabecDAO.filterActualBecados(cicloRegular,modalidadEstudio,becadosFilterBean);
        return listBecadosExcel;
    }

    @Override
    public List<BecadosFilterBean> filterAnteriorBecados(CicloAcademico cicloAcademico, BecadosFilterBean becadosFilterBean) {
        List<BecadosFilterBean> listBecadosExcel = new ArrayList<>();
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloAcademico cicloRegular = cicloAcademicoDAO.findActivo(modalidadEstudio);

        listBecadosExcel = becasPronabecDAO.filterAnteriorBecados(cicloRegular,modalidadEstudio,becadosFilterBean);
        return listBecadosExcel;
    }

    private void procesarArchivo(MultipartFile file, List<String> observados, List<InformacionBeca> pronabec, DataSessionPivot ds) {
        List<InformacionBeca> becadosPronabec = becasPronabecDAO.all();
        Map<String, InformacionBeca> mapBecadosPronabec = TypesUtil.convertListToMap("key", becadosPronabec);
        Map<Long, String> mapObservados = new HashMap<>();
        Date fechaRegistro = new Date();
        Usuario userRegistro = ds.getUsuario();

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Iterator<Row> rowIterator = workbook.getSheetAt(0).iterator();
            int fila;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                fila = row.getRowNum() + 1;

                // Saltar filas no relevantes
                if (fila <= 12) {
                    continue;
                }

                String nroDNI = getCellValue(0, row);
                String codigoTipoBeca = getCellValue(1, row);
                String yearConvocatoria = getCellValue(2, row);
                String fechaInicioStr = getCellValue(3, row);
                String fechaFinStr = getCellValue(4, row);
                String fechaOtorgamientoStr = getCellValue(5, row);
                String condicion = getCellValue(6, row);
                String situacion = getCellValue(7, row);
                String carrera = getCellValue(8, row);
                String estado = getCellValue(9, row);

                if (validateFields(fila, nroDNI, codigoTipoBeca, yearConvocatoria, mapObservados)) {
                    continue;
                }

                Persona personaBD = personaDAO.findByDocIdentidad(nroDNI);
                TipoBeca tipoBeca = tipoBecaPronabecDAO.findByCodigo(codigoTipoBeca);
                //Carrera carrera = carreraDAO.findByNombre(carreraStr);

                if (personaBD == null) {
                    addObservation(mapObservados, fila, "El nro de DNI " + nroDNI + " no existe.");
                    continue;
                }

                if (tipoBeca == null) {
                    addObservation(mapObservados, fila, "El **codigo** tipo de beca " + codigoTipoBeca + " no existe.");
                    continue;
                }

//                if(carrera == null){
//                    addObservation(mapObservados, fila, "La carrera " + carrera + " no existe.");
//                    continue;
//                }

                if (!validateYearConvocatoria(fila, yearConvocatoria, mapObservados)) {
                    continue;
                }

                Date fechaInicio = parseDate(fila, fechaInicioStr, "inicio", mapObservados);
                if (fechaInicio == null) continue;

                Date fechaFin = parseDate(fila, fechaFinStr, "fin", mapObservados);
                if (fechaFin == null) continue;

                Date fechaOtorgamiento = parseDate(fila, fechaOtorgamientoStr, "otorga", mapObservados);
                if (fechaOtorgamiento == null) continue;

                InformacionBeca becasPro = new InformacionBeca();
                becasPro.setPersona(personaBD);
                becasPro.setTipoBeca(tipoBeca);
                becasPro.setYearConvocatoria(yearConvocatoria);
                becasPro.setFechaInicio(fechaInicio);
                becasPro.setFechaFin(fechaFin);
                becasPro.setFechaRegistro(fechaRegistro);
                becasPro.setUsuario(userRegistro);
                becasPro.setFechaOtorgamiento(fechaOtorgamiento);
                becasPro.setCondicion(condicion);
                becasPro.setSituacion(situacion);
                becasPro.setCarrera(carrera);
                becasPro.setEstado(estado);

                pronabec.add(becasPro);
            }
        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor", ex);
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leído", ex);
        }

        observados.addAll(mapObservados.values());
    }

    private boolean validateFields(int fila, String nroDNI, String codigoTipoBeca, String yearConvocatoria, Map<Long, String> mapObservados) {
            if (StringUtils.isEmpty(nroDNI) || StringUtils.isEmpty(codigoTipoBeca) || StringUtils.isEmpty(yearConvocatoria)) {
                mapObservados.put((long) fila, "La fila " + fila + " tiene campos vacíos o formatos incorrectos.");
                return true;
            }
            return false;
    }

    private boolean validateYearConvocatoria(int fila, String yearConvocatoria, Map<Long, String> mapObservados) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (yearConvocatoria.length() != 4) {
                mapObservados.put((long) fila, "El año de convocatoria " + yearConvocatoria + " no tiene 4 dígitos. (Fila " + fila + ")");
                return false;
            }

            try {
                int year = Integer.parseInt(yearConvocatoria);
                if (year > currentYear) {
                    mapObservados.put((long) fila, "El año de convocatoria " + yearConvocatoria + " es mayor al año actual. (Fila " + fila + ")");
                    return false;
                }
            } catch (NumberFormatException e) {
                mapObservados.put((long) fila, "El año de convocatoria " + yearConvocatoria + " no es un número válido. (Fila " + fila + ")");
                return false;
            }
            return true;
    }

    private Date parseDate(int fila, String dateStr, String tipo, Map<Long, String> mapObservados) {
            if (dateStr == null) return null;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date currentDate = new Date();

            try {
                Date date = sdf.parse(dateStr);
                if (tipo.equals("inicio") && date.after(currentDate)) {
                    mapObservados.put((long) fila, "La fecha de inicio " + dateStr + " es mayor a la fecha actual. (Fila " + fila + ")");
                    return null;
                }
                return date;
            } catch (ParseException e) {
                mapObservados.put((long) fila, "La fecha de " + tipo + " " + dateStr + " no es válida. (Fila " + fila + ")");
                return null;
            }
    }

    private void addObservation(Map<Long, String> mapObservados, int fila, String message) {
            mapObservados.put((long) fila, message + " (Fila " + fila + ")");
        }


    private String getCellValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }

        if (pos == 3 || pos == 4 || pos == 5) {
            return handleDateCell(cell);
        } else {
            return handleStringCell(cell);
        }
    }

    private String handleDateCell(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return formatDate(cell.getDateCellValue());
        } else if (StringUtils.isEmpty(cell.getStringCellValue())) {
            return null;
        } else {
            return parseDateFromString(cell.getStringCellValue().trim());
        }
    }

    private String handleStringCell(Cell cell) {
        cell.setCellType(CellType.STRING);
        String dato = cell.getStringCellValue();
        if (dato != null) {
            dato = sanitizeString(dato);
        }
        return dato;
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private String parseDateFromString(String dateStr) {
        //dateStr = dateStr.trim();
        dateStr = sanitizeString(dateStr);
        SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        try {
            Date parsedDate = sdfInput.parse(dateStr);
            if (parsedDate.before(currentDate)) {
                return sdfOutput.format(parsedDate);
            } else {
                System.out.println("Error: La fecha es mayor o igual a la fecha actual.");
                return sdfOutput.format(parsedDate);
            }
        } catch (ParseException e) {
            System.out.println("Error: El valor no es una fecha válida.");
            return dateStr; // Retornar el string original si no se puede parsear
        }
    }

    private String sanitizeString(String input) {
        String sanitized = StringUtils.replaceChars(input, '\t', ' ');
        sanitized = StringUtils.replaceChars(sanitized, '\r', ' ');
        sanitized = StringUtils.replaceChars(sanitized, '\n', ' ');
        sanitized = StringUtils.replaceChars(sanitized, ',', ' ');
        sanitized = StringUtils.replaceChars(sanitized, '|', ' ');
        sanitized = sanitized.replaceAll("\u00A0", "");
        return StringUtils.trim(sanitized);
    }


}
