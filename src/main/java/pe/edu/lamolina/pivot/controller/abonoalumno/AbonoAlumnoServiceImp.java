package pe.edu.lamolina.pivot.controller.abonoalumno;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.POST;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoArchivoEnum;
import pe.edu.lamolina.model.finanzas.AbonoPostulante;
import pe.edu.lamolina.model.finanzas.AlumnoPagoVerano;
import pe.edu.lamolina.model.finanzas.CargaAbonos;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CicloPostulaDAO;
import pe.edu.lamolina.pivot.dao.finanza.AbonoPostulanteDAO;
import pe.edu.lamolina.pivot.dao.finanza.AlumnoPagoVeranoDAO;
import pe.edu.lamolina.pivot.dao.finanza.CargaAbonosDAO;
import pe.edu.lamolina.pivot.dao.finanza.CuentaBancariaDAO;
import pe.edu.lamolina.pivot.dao.finanza.DeudaInteresadoDAO;
import pe.edu.lamolina.pivot.dao.finanza.ItemCargaAbonoDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.EventoCicloDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.EventoDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.ModalidadIngresoCicloDAO;
import pe.edu.lamolina.pivot.dao.inscripcion.PostulanteDAO;

@Service
@Transactional(readOnly = true)
public class AbonoAlumnoServiceImp implements AbonoAlumnoService {

    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    CuentaBancariaDAO cuentaBancariaDAO;
    @Autowired
    CargaAbonosDAO cargaAbonosDAO;
    @Autowired
    ItemCargaAbonoDAO itemCargaAbonoDAO;
    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;
    @Autowired
    AbonoPostulanteDAO abonoPostulanteDAO;
    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;
    @Autowired
    EventoDAO eventoDAO;
    @Autowired
    EventoCicloDAO eventoCicloDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoPagoVeranoDAO alumnoPagoVeranoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<ItemCargaAbono> allAbonosByAlumno(CicloAcademico ciclo, DynatableFilter filter) {

        CicloPostula cicloPostula = findCicloActivo();
        logger.debug("CICLO POSTULA ID : {}", cicloPostula.getId());
        return itemCargaAbonoDAO.allByDynaTable2(filter, cicloPostula, TipoArchivoEnum.DI);
    }

    @Override
    @Transactional
    public List<Observado> loadArchivoHistorico(MultipartFile file, CicloAcademico ciclo, Usuario usuario) {

        CicloPostula cicloPostula = findCicloActivo();
        String rutaFile = saveFileAbonos(file);
        CargaAbonos carga = crearCargaAbonosHistorico(rutaFile);

        Date hoy = new LocalDate().toDate();
        carga.setCicloPostula(cicloPostula);
        Date fecha = buscarFechaArchivo(carga, hoy);
        verificarRedundanciaHistorica(carga, fecha);
        verificarAlumnos(carga);
        List<Observado> observados = validarItemsCarga(carga, fecha);

        carga.setFechaCarga(fecha);
        carga.setFechaRegistro(new Date());
        carga.setRutaArchivo(rutaFile);
        carga.setTipoArchivoEnum(TipoArchivoEnum.HI);
        carga.setUserRegistro(usuario);

        saveDatos(carga);
        verificarExtornosHistoricos(carga, usuario, ciclo);
        verificarAbonosAlumnos(carga, usuario, ciclo);

        return observados;
    }

    private void verificarExtornosHistoricos(CargaAbonos carga, Usuario usuario, CicloAcademico ciclo) {
        List<ItemCargaAbono> previos = carga.getVerificados();
        for (ItemCargaAbono previo : previos) {
            if (previo.getDescripcion().startsWith("EXTORNO")) {
                continue;
            }
            if (!previo.isValidado()) {
                Alumno alumno = previo.getAlumno();
                logger.debug("descripcion::::" + previo.getDescripcion());
                logger.debug("alumno::::" + (alumno == null ? null : alumno.getId() + ""));
                decrementarAbono(previo, ciclo);
                previo.setExtornado(1);
                previo.setFechaExtornado(new Date());
                previo.setUsuarioExtornado(usuario);
                itemCargaAbonoDAO.update(previo);
            }
        }
    }

    private Date buscarFechaArchivo(CargaAbonos carga, Date hoy) {
        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        Date fecha = null;
        for (ItemCargaAbono item : items) {
            fecha = (fecha == null) ? item.getFechaAbono() : fecha;
            if (fecha.compareTo(item.getFechaAbono()) != 0) {
                throw new PhobosException("Las fechas del archivo deben corresponder a una sola fecha");
            }
        }

        if (fecha.compareTo(hoy) == 0) {
            throw new PhobosException("No se permite cargar históricos del mismo día");
        }

        CargaAbonos previo = cargaAbonosDAO.findHistoricoByCtaBancoFecha(carga.getCuentaBancaria(), carga.getCicloPostula(), fecha);
        if (previo != null) {
            throw new PhobosException("La carga del archivos histórico del " + new DateTime(fecha).toString("dd/MM/yyyy") + " ya fue ejecutada.");
        }

        return fecha;
    }

    private void saveDatos(CargaAbonos carga) {

        cargaAbonosDAO.save(carga);
        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        for (ItemCargaAbono item : items) {
            itemCargaAbonoDAO.save(item);
        }
    }

    private void verificarAbonosAlumnos(CargaAbonos carga, Usuario usuario, CicloAcademico ciclo) {
        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        for (ItemCargaAbono item : items) {
            if (item.getDescripcion().startsWith("EXTORNO")) {
                continue;
            }
            if (item.getRedundante() == 1) {
                ItemCargaAbono previo = item.getNoRedundante();
                if (previo.getExtornado() == 1 && previo.getFechaExtornado() == null) {
                    decrementarAbono(previo, ciclo);
                    previo.setExtornado(1);
                    previo.setFechaExtornado(new Date());
                    previo.setUsuarioExtornado(usuario);
                    itemCargaAbonoDAO.update(previo);
                }
                continue;
            }
            if (item.getExtornado() == 1) {
                continue;
            }
            incrementarAbono(item, ciclo, usuario);
        }
    }

    @Override
    @Transactional
    public void revisarDeudasCompletas(List<DeudaInteresado> deudas, Postulante postulante, ModalidadIngreso modalidad, CicloPostula ciclo) {
        if (deudas.isEmpty()) {
            logger.debug("No tiene deudas");
            return;
        }

        Interesado interesado = postulante.getInteresado();
        boolean pagoProspecto = false;
        boolean pagoInscripcion = false;
        boolean pagoExtemporaneo = false;
        boolean debePagarProspecto = false;
        boolean debePagarInscripcion = false;
        boolean debePagarExtemporaneo = false;

        if (modalidad != null) {
            ModalidadIngresoCiclo mic = modalidadIngresoCicloDAO.findByModalidadCiclo(modalidad, ciclo);
            if (mic.getRindeExamenAdmision() == 1) {
                EventoCiclo examen = findEventoExamen(ciclo);
                Date fechaExamen = examen.getFechaInicio();
                if (new Date().after(fechaExamen)) {
                    logger.debug("No se revisan las deudas porque ya paso el examen");
                    return;
                }
            }
        }

        for (DeudaInteresado deuda : deudas) {
            if (deuda.getEstadoEnum() != DeudaEstadoEnum.ACT) {
                continue;
            }

            Postulante postul = deuda.getPostulante();
            if (postul != null) {
                if (postul.getId().longValue() != postulante.getId()) {
                    continue;
                }
            }

            ConceptoPago concepto = deuda.getConceptoPrecio().getConceptoPago();
            if (concepto.isSinDescripcion()) {
            } else if (concepto.isProspecto()) {
                debePagarProspecto = true;
                pagoProspecto = (deuda.getAbono().compareTo(deuda.getMonto()) >= 0);
            } else if (concepto.isExtemporaneo()) {
                debePagarExtemporaneo = true;
                pagoExtemporaneo = (deuda.getAbono().compareTo(deuda.getMonto()) >= 0);
            } else if (concepto.haveModalidadIngreso()) {
                debePagarInscripcion = true;
                pagoInscripcion = (deuda.getAbono().compareTo(deuda.getMonto()) >= 0);
            }
        }

        logger.debug("pagoExtemporaneo {}", pagoExtemporaneo);
        logger.debug("debePagarExtemporaneo {}", debePagarExtemporaneo);

        if (interesado.getEstadoEnum() == POST && !(pagoInscripcion || !debePagarInscripcion)) {
            Date hoy = new Date();
            EventoCiclo eventoCiclo = findEventoExtemporaneo(ciclo);
            if (eventoCiclo != null && hoy.after(eventoCiclo.getFechaInicio())) {
                debePagarExtemporaneo = true;
            }
        }

        logger.debug("pagoProspecto {}", pagoProspecto);
        logger.debug("pagoInscripcion {}", pagoInscripcion);
        logger.debug("pagoExtemporaneo {}", pagoExtemporaneo);
        logger.debug("debePagarProspecto {}", debePagarProspecto);
        logger.debug("debePagarInscripcion {}", debePagarInscripcion);
        logger.debug("debePagarExtemporaneo {}", debePagarExtemporaneo);

//        if (interesado.getEstadoEnum() == REG && pagoProspecto) {
//            interesado.setEstado(PROS);
//            interesadoDAO.update(interesado);
//            postulante.setEstado(PostulanteEstadoEnum.PROS);
//            postulanteDAO.update(postulante);
//            return;
//        }
//        if (interesado.getEstadoEnum() == PROS && !pagoProspecto && debePagarProspecto) {
//            interesado.setEstado(REG);
//            interesadoDAO.update(interesado);
//            postulante.setEstado(PostulanteEstadoEnum.CRE);
//            postulanteDAO.update(postulante);
//            return;
//        }
        if (interesado.getEstadoEnum() == POST
                && postulante.getEstadoEnum() == PostulanteEstadoEnum.PRE
                && (pagoProspecto || !debePagarProspecto)
                && (pagoInscripcion || !debePagarInscripcion)
                && (pagoExtemporaneo || !debePagarExtemporaneo)) {
            postulante.setEstado(PostulanteEstadoEnum.PAGO);
            if (postulante.getFechaEncuesta() != null && !modalidad.isPreLaMolina()) {
                postulante.setEstado(PostulanteEstadoEnum.INS);
            } else if (postulante.getFechaEncuesta() != null && modalidad.isPreLaMolina()) {
                postulante.setEstado(PostulanteEstadoEnum.ING);
            }
            postulanteDAO.update(postulante);
            return;
        }

        if (interesado.getEstadoEnum() == POST
                && Arrays.asList(PostulanteEstadoEnum.PAGO, PostulanteEstadoEnum.INS).contains(postulante.getEstadoEnum())
                && ((!pagoProspecto && debePagarProspecto)
                || (!pagoInscripcion && debePagarInscripcion)
                || (!pagoExtemporaneo && debePagarExtemporaneo))) {
            postulante.setEstado(PostulanteEstadoEnum.PRE);
            postulanteDAO.update(postulante);
            return;
        }

//        if (interesado.getEstadoEnum() == PROS
//                && postulante.getEstadoEnum() == PostulanteEstadoEnum.PROS
//                && (pagoProspecto || !debePagarProspecto)
//                && (pagoInscripcion || !debePagarInscripcion)
//                && (pagoExtemporaneo || !debePagarExtemporaneo)) {
//            if (modalidad == null) {
//                interesado.setEstado(PROS);
//            } else {
//                interesado.setEstado(POST);
//            }
//            interesadoDAO.update(interesado);
//            postulante.setEstado(PostulanteEstadoEnum.PAGO);
//            postulanteDAO.update(postulante);
//        }
    }

    private EventoCiclo findEventoExtemporaneo(CicloPostula ciclo) {
        Evento evento = eventoDAO.findByCode("EXTM");
        List<EventoCiclo> examenes = eventoCicloDAO.allByEventoCiclo(evento, ciclo);
        if (!examenes.isEmpty()) {
            return examenes.get(0);
        }
        return null;
    }

    private DeudaInteresado findDeudaByMonto(List<DeudaInteresado> deudas, BigDecimal monto, Postulante postulante) {
        for (DeudaInteresado deuda : deudas) {
            Postulante postul = deuda.getPostulante();
            if (deuda.getAbono().compareTo(BigDecimal.ZERO) == 0 && deuda.getMonto().compareTo(monto) == 0) {
                if (postul != null) {
                    if (postul.getId() == postulante.getId().longValue()) {
                        return deuda;
                    }
                } else {
                    return deuda;
                }
            }
        }
        return null;
    }

    private void verificarRedundanciaHistorica(CargaAbonos carga, Date fecha) {
        logger.debug("cta banco es {}", carga.getCuentaBancaria().getId());
        List<ItemCargaAbono> previos = itemCargaAbonoDAO.allActivosSinExtornosByFecha(carga.getCuentaBancaria(), TipoArchivoEnum.DI.name(), fecha);
        carga.setVerificados(previos);
        if (previos.isEmpty()) {
            return;
        }

        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        for (ItemCargaAbono item : items) {
            boolean existe = false;

            for (ItemCargaAbono previo : previos) {
                existe = ObjectUtil.equalsAttrs(item, previo,
                        Arrays.asList("fechaAbono", "importe", "descripcion", "sucursal", "usuarioBanco"));
                String nroOperacionCarga = item.getNumeroOperacion().replaceFirst("^0+(?!$)", "");
                String nroOperacionBD = previo.getNumeroOperacion().replaceFirst("^0+(?!$)", "");
                if (existe && nroOperacionBD.equals(nroOperacionCarga)) {
                    previo.setValidado(true);
                    item.setRedundante(1);
                    item.setNoRedundante(previo);
                    break;
                }
            }

            if (existe) {
                logger.debug("\tREDUNDANCIA");
            } else {
                logger.debug("\tNo encontró redundancia");
            }
        }
    }

    private List<Observado> validarItemsCarga(CargaAbonos carga, Date hoy) {
        List<Observado> observados = new ArrayList();
        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        logger.debug("van a leerse {} items de carga ", items.size());
        for (ItemCargaAbono item : items) {
            logger.debug("descrip {}", item.getDescripcion());
            if (!hoy.equals(item.getFechaAbono())) {
                throw new PhobosException("Solo se permite carga de archivos del " + new DateTime(hoy).toString("dd/MM/yyyy"));
            }

            if (item.getDescripcion().startsWith("EXTORNO")) {
                continue;
            }

            if (item.getNoRedundante() != null) {
                continue;
            }

            if (item.getExtornado() == 1) {
                continue;
            }

            String codigo = StringUtils.substring(item.getDescripcion(), -8).trim();
            Alumno alumno = item.getAlumno();

            if (alumno == null) {
                Observado observado = new Observado();
                observado.setOperacion(item.getNumeroOperacion());
                observado.setDescripcion("Alumno con código " + codigo + " no pudo ser hallado en la línea " + (item.getLinea().intValue() + 1));
                observados.add(observado);
            }
        }
        return observados;
    }

    private void verificarAlumnos(CargaAbonos carga) {
        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        for (ItemCargaAbono item : items) {

            if (item.getDescripcion().startsWith("EXTORNO")) {
                continue;
            }

            if (item.getNoRedundante() != null) {
                item.setPostulante(item.getNoRedundante().getPostulante());
                continue;
            }

            if (!item.getDescripcion().startsWith("EFECTIVO")) {
                continue;
            }

            String codigo = getCodigoPago(item.getDescripcion());
            Alumno alumno = alumnoDAO.findByCodigo(codigo);
            if (alumno == null) {
                logger.debug("Alumno con código - {} - no encontrado", codigo);
                logger.info("Alumno con código - {} - no encontrado", codigo);
            }

            item.setAlumno(alumno);
        }
    }

    private String getCodigoPago(String trama) {
        String cadena = trama.replaceAll("EFECTIVO", "").replaceAll("^0+", "");
        if (cadena.length() >= 8) {
            return cadena;
        }
        if (StringUtils.isNumeric(cadena)) {
            return StringUtils.substring(trama, -8);
        }
        return cadena;
    }

    private CargaAbonos crearCargaAbonosHistorico(String rutaFile) {
        CargaAbonos carga = new CargaAbonos();
        carga.setItemCargaAbono(new ArrayList());

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();
                if (loop == 0) {
                    String nroCtaBanco = getCellValue(1, row).split(" ")[0];
                    logger.debug("Cta banco es {}", nroCtaBanco);
                    CuentaBancaria cta = cuentaBancariaDAO.findByNumero(nroCtaBanco);
                    if (cta == null) {
                        throw new PhobosException("No se pudo ubicar la cuenta " + nroCtaBanco);
                    }
                    carga.setCuentaBancaria(cta);
                }
                if (loop == 4) {
                    String titulo = getCellValue(0, row);
                    if (!titulo.equals("Fecha")) {
                        throw new PhobosException("Este archivo no corresponde a movimientos históricos");
                    }
                }
                if (loop < 5) {
                    continue;
                }

                String columnaFecha = getCellValue(0, row);
                if (StringUtils.isEmpty(columnaFecha)) {
                    break;
                }

                Date fechaAbono = format.parse(columnaFecha);
                String columnaDescripcion = getCellValue(2, row);
                if (!(columnaDescripcion.startsWith("EFECTIVO"))) {
                    continue;
                }

                String columnaMonto = getCellValue(3, row);
                BigDecimal importe = new BigDecimal(columnaMonto.replaceAll(",", ""));
                String sucursal = getCellValue(5, row);
                String operacion = getCellValue(6, row);
                String userBanco = getCellValue(8, row);

                ItemCargaAbono item = new ItemCargaAbono();
                item.setFechaAbono(fechaAbono);
                item.setDescripcion(columnaDescripcion);
                item.setImporte(importe.abs());
                item.setNumeroOperacion(operacion);
                item.setSucursal(sucursal);
                item.setUsuarioBanco(userBanco);
                item.setLinea(loop);
                item.setCargaAbonos(carga);
                item.setRedundante(0);
                item.setExtornado(0);
                item.setUtilizado(0);

                carga.getItemCargaAbono().add(item);
            }
            logger.debug("Se han leido un total de {} filas", loop);
        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        } catch (ParseException e) {
            throw new PhobosException("Este archivo no corresponde a movimientos históricos");
        }
        return carga;
    }

    private String saveFileAbonos(MultipartFile file) {
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

//    @Override
//    @Transactional
//    public void asignarPostulante(ItemCargaAbono itemForm, CicloAcademico ciclo, DataSessionPivot ds) {
//        Postulante postulanteNew = postulanteDAO.findSinModalidad(itemForm.getPostulante().getId());
//        ItemCargaAbono itemBD = itemCargaAbonoDAO.find(itemForm.getId());
//        Postulante postulanteOld = itemBD.getPostulante();
//        itemBD.setPostulante(postulanteNew);
//        itemCargaAbonoDAO.update(itemBD);
//
//        postulanteOld.setImporteAbonado(postulanteOld.getImporteAbonado().subtract(itemBD.getImporte()));
//        postulanteOld.setImporteTotal(postulanteOld.getImporteAbonado().add(postulanteOld.getImporteDescuento()));
//        postulanteDAO.update(postulanteOld);
//
//        postulanteNew.setImporteAbonado(postulanteNew.getImporteAbonado().add(itemBD.getImporte()));
//        postulanteNew.setImporteTotal(postulanteNew.getImporteAbonado().add(postulanteNew.getImporteDescuento()));
//        postulanteDAO.update(postulanteNew);
//
//        AbonoPostulante abonoOld = abonoPostulanteDAO.findByItemCarga(itemBD);
//        abonoPostulanteDAO.delete(abonoOld);
//
//        AbonoPostulante abonoNew = new AbonoPostulante();
//        abonoNew.setAbono(itemBD);
//        abonoNew.setConcepto(itemBD.getConceptoPago());
//        abonoNew.setFechaDeposito(itemBD.getFechaAbono());
//        abonoNew.setImporte(itemBD.getImporte());
//        abonoNew.setImporteUtilizado(BigDecimal.ZERO);
//        abonoNew.setNumeroOperacion(itemBD.getNumeroOperacion());
//        abonoNew.setPostulante(postulanteNew);
//        abonoPostulanteDAO.save(abonoNew);
//
//        List<DeudaInteresado> deudas = deudaInteresadoDAO.allActivasByInteresado(postulanteNew.getInteresado(), ciclo);
//        DeudaInteresado deuda = findDeudaByMonto(deudas, itemBD.getImporte(), postulanteNew);
//        if (deuda != null) {
//            deuda.setFechaAbono(new Date());
//            deuda.setAbono(abonoNew.getImporte());
//            deudaInteresadoDAO.update(deuda);
//
//            itemBD.setConceptoPago(deuda.getConceptoPrecio().getConceptoPago());
//            itemCargaAbonoDAO.update(itemBD);
//        }
//        revisarDeudasCompletas(deudas, postulanteNew, postulanteNew.getModalidadIngreso(), ciclo);
//
//    }
    @Override
    @Transactional
    public void reasignarExtorno(ItemCargaAbono form, Usuario usuario) {
        ItemCargaAbono newExtonado = itemCargaAbonoDAO.find(form.getId());
        ItemCargaAbono extornador = itemCargaAbonoDAO.find(form.getExtornador().getId());
        ItemCargaAbono oldExtornado = itemCargaAbonoDAO.findByExtonador(extornador);
        if (oldExtornado == null) {
            throw new PhobosException("No se ha encontrado un extornador");
        }

        CicloPostula ciclo = findCicloActivo();

//        decrementarAbono(newExtonado.getPostulante(), newExtonado);
        oldExtornado.setExtornado(0);
        oldExtornado.setExtornador(null);
        oldExtornado.setFechaExtornado(null);
        oldExtornado.setUsuarioExtornado(null);
        itemCargaAbonoDAO.update(form);

        newExtonado.setExtornado(1);
        newExtonado.setExtornador(extornador);
        newExtonado.setFechaExtornado(new Date());
        newExtonado.setUsuarioExtornado(usuario);
        itemCargaAbonoDAO.update(newExtonado);
    }

    @Override
    public CicloPostula findCicloActivo() {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloPostulaDAO.findActivo(modalidad);
    }

    private EventoCiclo findEventoExamen(CicloPostula ciclo) {
        Evento evento = eventoDAO.findByCode("EXAM");
        List<EventoCiclo> examenes = eventoCicloDAO.allByEventoCiclo(evento, ciclo);
        if (!examenes.isEmpty()) {
            return examenes.get(0);
        }
        return null;
    }

    @Override
    public List<ItemCargaAbono> allExtornados(ItemCargaAbono form) {
        Date hoy = new DateTime().toDate();
        form.setFechaAbono(hoy);

        List<ItemCargaAbono> cargas = itemCargaAbonoDAO.allPosiblesExtornados(form);
        List<ItemCargaAbono> cargas2 = itemCargaAbonoDAO.allPosiblesExtornados2(form);
        cargas.addAll(cargas2);

        for (ItemCargaAbono item : cargas) {
            Postulante postul = item.getPostulante();
            List<AbonoPostulante> abonos = abonoPostulanteDAO.allByPostulante(postul);
            postul.setPagos(abonos.size());

            item.setAfectadoExtorno(0);
            if (item.getExtornador() != null) {
                if (item.getExtornador().getId() == form.getId().longValue()) {
                    item.setAfectadoExtorno(1);
                }
            }
        }

        return cargas;

    }

    @Override
    public List<Observado> loadArchivoDiario(MultipartFile file, CicloAcademico ciclo, Usuario usuario) {
        String rutaFile = saveFileAbonos(file);
        CargaAbonos carga = crearCargaAbonosDiario(rutaFile);
        CicloPostula cicloPostula = findCicloActivo();

        Date hoy = new LocalDate().toDate(); // new DateTime("2016-07-07").toDate(); // new LocalDate().toDate();
        verificarRedundanciaDiaria(carga, hoy);
        verificarAlumnos(carga);
        verificarExtornosDiario(carga, usuario);
        List<Observado> observados = validarItemsCarga(carga, hoy);

        int redundantes = 0;
        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        logger.debug("se van registrar {} nuevos abonos", items.size());
        for (ItemCargaAbono item : items) {
            redundantes += item.getRedundante();
        }
        if (redundantes == items.size()) {
            throw new PhobosException("Este archivo no contiene registros nuevos");
        }

        carga.setCicloPostula(cicloPostula);
        carga.setFechaCarga(hoy);
        carga.setFechaRegistro(new Date());
        carga.setRutaArchivo(rutaFile);
        carga.setTipoArchivoEnum(TipoArchivoEnum.DI);
        carga.setUserRegistro(usuario);

        saveDatos(carga);
        verificarAbonosAlumnos(carga, usuario, ciclo);

        return observados;
    }

    private CargaAbonos crearCargaAbonosDiario(String rutaFile) {
        CargaAbonos carga = new CargaAbonos();
        carga.setItemCargaAbono(new ArrayList());

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            FileInputStream fis = new FileInputStream(rutaFile);
            Workbook myWorkBook = new HSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();
                if (loop == 0) {
                    String nroCtaBanco = getCellValue(1, row).split(" ")[0];
                    logger.debug("Cta banco es {}", nroCtaBanco);
                    CuentaBancaria cta = cuentaBancariaDAO.findByNumero(nroCtaBanco);
                    if (cta == null) {
                        throw new PhobosException("No se pudo ubicar la cuenta " + nroCtaBanco);
                    }
                    carga.setCuentaBancaria(cta);
                }

                if (loop < 8) {
                    String titulo = getCellValue(0, row);
                    logger.debug("titulo es {} en la linea {}", titulo, loop);
                }

                if (loop == 4) {
                    String titulo = getCellValue(0, row);
                    if (!titulo.equals("Moneda")) {
                        logger.debug("titulo es {}", titulo);
                        throw new PhobosException("Este archivo no corresponde a movimientos del día");
                    }
                }
                if (loop == 5) {
                    String conte = getCellValue(0, row);
                    if (!conte.equals("Soles")) {
                        logger.debug("contenido es {}", conte);
                        throw new PhobosException("Este archivo no corresponde a movimientos del día");
                    }
                }
                if (loop == 7) {
                    String titulo = getCellValue(0, row);
                    logger.debug("Fecha es <<{}>>", titulo);
                    if (!titulo.equals("Fecha")) {
                        throw new PhobosException("Este archivo no corresponde a movimientos del día");
                    }
                }

                if (loop < 8) {
                    continue;
                }

                String columnaFecha = getCellValue(0, row);
                if (StringUtils.isEmpty(columnaFecha)) {
                    break;
                }

                Date fechaAbono = format.parse(columnaFecha);
                String columnaDescripcion = getCellValue(2, row);
                if (!(columnaDescripcion.startsWith("EFECTIVO") || columnaDescripcion.startsWith("EXTORNO"))) {
                    continue;
                }

                String columnaMonto = getCellValue(3, row);
                BigDecimal importe = new BigDecimal(columnaMonto.replaceAll(",", ""));
                String sucursal = getCellValue(4, row);
                String operacion = getCellValue(5, row);
                String userBanco = getCellValue(6, row);

                logger.debug("fecha {} ::: desc {}", columnaFecha, columnaDescripcion);

                ItemCargaAbono item = new ItemCargaAbono();
                item.setFechaAbono(fechaAbono);
                item.setDescripcion(columnaDescripcion);
                item.setImporte(importe.abs());
                item.setNumeroOperacion(operacion);
                item.setSucursal(sucursal);
                item.setUsuarioBanco(userBanco);
                item.setLinea(loop);
                item.setCargaAbonos(carga);
                item.setRedundante(0);
                item.setExtornado(0);
                item.setUtilizado(0);

                carga.getItemCargaAbono().add(item);
            }
            logger.debug("Se han leido un total de {} filas", loop);
            logger.debug("Se han cargado un total de {} registros ", carga.getItemCargaAbono().size());

        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new PhobosException("Este archivo no corresponde a movimientos del día");
        }
        return carga;
    }

    private void verificarRedundanciaDiaria(CargaAbonos carga, Date hoy) {
        List<ItemCargaAbono> previos = itemCargaAbonoDAO.allActivosByFecha(carga.getCuentaBancaria(), TipoArchivoEnum.DI.name(), hoy);
        if (previos.isEmpty()) {
            return;
        }
        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        for (ItemCargaAbono item : items) {
            boolean existe = false;
            for (ItemCargaAbono previo : previos) {
                existe = ObjectUtil.equalsAttrs(item, previo,
                        Arrays.asList("fechaAbono", "importe", "descripcion", "sucursal", "usuarioBanco"));
                String nroOperacionCarga = item.getNumeroOperacion().replaceFirst("^0+(?!$)", "");
                String nroOperacionBD = previo.getNumeroOperacion().replaceFirst("^0+(?!$)", "");
                if (existe && nroOperacionBD.equals(nroOperacionCarga)) {
                    item.setRedundante(1);
                    item.setNoRedundante(previo);
                    break;
                }
            }
        }
    }

    private void verificarExtornosDiario(CargaAbonos carga, Usuario usuario) {

        List<ItemCargaAbono> items = carga.getItemCargaAbono();
        for (ItemCargaAbono extornador : items) {
            if (!extornador.getDescripcion().startsWith("EXTORNO")) {
                continue;
            }
            if (extornador.getRedundante() == 1) {
                continue;
            }
            buscarExtornado(extornador, items, usuario);
        }
        Collections.reverse(items);

    }

    private void buscarExtornado(ItemCargaAbono extornador, List<ItemCargaAbono> items, Usuario usuario) {
        logger.debug("extorno de <<{}>> <<{}>> <<{}>> <<{}>>", extornador.getImporte(), extornador.getFechaAbono(), extornador.getSucursal(), extornador.getUsuarioBanco());
        List<ItemCargaAbono> copias = new ArrayList();
        for (ItemCargaAbono item : items) {
            copias.add(item);
        }
        Collections.reverse(copias);
        List<ItemCargaAbono> posibles = new ArrayList();

        for (ItemCargaAbono item : copias) {
            if (item.getExtornado() == 1) {
                continue;
            }
            if (!item.getDescripcion().startsWith("EFECTIVO")) {
                continue;
            }
            if (item.getRedundante() == 1) {
                ItemCargaAbono previo = item.getNoRedundante();
                if (previo.getExtornado() == 1) {
                    continue;
                }
            }
            logger.debug("   comparando con <<{}>> <<{}>> <<{}>> <<{}>>", item.getImporte(), item.getFechaAbono(), item.getSucursal(), item.getUsuarioBanco());
            boolean extornable = ObjectUtil.equalsAttrs(extornador, item,
                    Arrays.asList("fechaAbono", "importe", "sucursal", "usuarioBanco"));
            logger.debug("   comparacion es {}", extornable);
            if (extornable) {
                posibles.add(item);
                break;
            }
        }
        if (posibles.isEmpty()) {
            for (ItemCargaAbono item : copias) {
                if (item.getExtornado() == 1) {
                    continue;
                }
                if (!item.getDescripcion().startsWith("EFECTIVO")) {
                    continue;
                }
                if (item.getRedundante() == 1) {
                    ItemCargaAbono previo = item.getNoRedundante();
                    if (previo.getExtornado() == 1) {
                        continue;
                    }
                }
                logger.debug("   comparando con <<{}>> <<{}>> <<{}>> <<{}>>", item.getImporte(), item.getFechaAbono(), item.getSucursal(), item.getUsuarioBanco());
                boolean extornable = ObjectUtil.equalsAttrs(extornador, item,
                        Arrays.asList("fechaAbono", "importe", "sucursal"));
                logger.debug("   comparacion es {}", extornable);
                if (extornable) {
                    posibles.add(item);
                    break;
                }
            }
        }
        if (posibles.isEmpty()) {
            throw new PhobosException("No se pudo hallar la operación a extornar que corresponde a la línea " + extornador.getLinea());
        }

        for (ItemCargaAbono posible : posibles) {
            posible.setExtornado(1);
            posible.setFechaExtornado(new Date());
            posible.setUsuarioExtornado(usuario);
            posible.setExtornador(extornador);
            return;
        }
        throw new PhobosException("No se pudo hallar la operación a extornar que corresponde a la línea " + extornador.getLinea());
    }

    private void incrementarAbono(ItemCargaAbono item, CicloAcademico ciclo, Usuario usuario) {

        AlumnoPagoVerano alumnoPagoVeranoDB = alumnoPagoVeranoDAO.findAlumnoByCiclo(item.getAlumno(), ciclo);
        if (alumnoPagoVeranoDB == null) {
            AlumnoPagoVerano alPagVer = new AlumnoPagoVerano();
            alPagVer.setAbono(item.getImporte());
            alPagVer.setSaldo(item.getImporte());
            alPagVer.setConsumo(BigDecimal.ZERO);
            alPagVer.setAlumno(item.getAlumno());
            alPagVer.setCicloAcademico(ciclo);
            alPagVer.setFechaRegistro(new Date());
            alPagVer.setDeuda(BigDecimal.ZERO);
            alPagVer.setUserRegistro(usuario);
            alumnoPagoVeranoDAO.save(alPagVer);

        } else {
            AlumnoPagoVerano alumnoPagoVeranoUpd = new AlumnoPagoVerano(alumnoPagoVeranoDB.getId());
            alumnoPagoVeranoUpd.setAbono(alumnoPagoVeranoDB.getAbono().add(item.getImporte()));
            if (alumnoPagoVeranoDB.getDeuda().compareTo(BigDecimal.ZERO) == 0) {
                alumnoPagoVeranoUpd.setSaldo(alumnoPagoVeranoDB.getSaldo().add(item.getImporte()));
                alumnoPagoVeranoDAO.updateColumns(alumnoPagoVeranoUpd, "saldo", "abono");

            } else {
                BigDecimal diferencia = alumnoPagoVeranoDB.getDeuda().subtract(item.getImporte());
                if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
                    alumnoPagoVeranoUpd.setFechaSaldoNegativo(null);
                    alumnoPagoVeranoUpd.setSaldo(alumnoPagoVeranoDB.getSaldo().add(diferencia));
                    alumnoPagoVeranoDAO.updateColumns(alumnoPagoVeranoUpd, "saldo", "abono", "fechaSaldoNegativo");

                } else {
                    alumnoPagoVeranoUpd.setDeuda(diferencia.abs());
                    alumnoPagoVeranoUpd.setFechaSaldoNegativo(new Date());
                    alumnoPagoVeranoDAO.updateColumns(alumnoPagoVeranoUpd, "deuda", "abono", "fechaSaldoNegativo");
                }
            }

        }

    }

    private void decrementarAbono(ItemCargaAbono item, CicloAcademico ciclo) {
        logger.debug("DECREMENTAR ABONO {}", item.getId());
        logger.debug("DECREMENTAR ABONO {}", item.getAlumno().getId());
        AlumnoPagoVerano alumnoPagoVeranoBD = alumnoPagoVeranoDAO.findAlumnoByCiclo(item.getAlumno(), ciclo);
        alumnoPagoVeranoBD.setAbono(alumnoPagoVeranoBD.getAbono().subtract(item.getImporte()));
        BigDecimal diferencia = alumnoPagoVeranoBD.getSaldo().subtract(item.getImporte());
        if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
            alumnoPagoVeranoBD.setSaldo(diferencia);
        } else {
            alumnoPagoVeranoBD.setSaldo(BigDecimal.ZERO);
            alumnoPagoVeranoBD.setDeuda(diferencia.abs());
            alumnoPagoVeranoBD.setFechaSaldoNegativo(new Date());
        }
        alumnoPagoVeranoDAO.update(alumnoPagoVeranoBD);
    }
}
