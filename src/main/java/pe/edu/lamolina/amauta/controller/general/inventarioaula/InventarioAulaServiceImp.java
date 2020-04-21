package pe.edu.lamolina.amauta.controller.general.inventarioaula;

import com.google.common.base.Strings;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.almacen.Almacen;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.almacen.Producto;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.almacen.TipoProducto;
import pe.edu.lamolina.model.enums.CodigoTipoProductoEnum;
import pe.edu.lamolina.model.enums.EstadoInventarioEnum;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.UnidadMedida;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.almacen.AlmacenDAO;
import pe.edu.lamolina.amauta.dao.almacen.InventarioDAO;
import pe.edu.lamolina.amauta.dao.almacen.ProductoDAO;
import pe.edu.lamolina.amauta.dao.almacen.ResumenInventarioDAO;
import pe.edu.lamolina.amauta.dao.almacen.TipoProductoDAO;
import pe.edu.lamolina.amauta.dao.general.ArchivoDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Service
@Transactional(readOnly = true)
public class InventarioAulaServiceImp implements InventarioAulaService {

    @Autowired
    InventarioDAO inventarioDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    ProductoDAO productoDAO;

    @Autowired
    TipoProductoDAO tipoProductoDAO;

    @Autowired
    ResumenInventarioDAO resumenInventarioDAO;

    @Autowired
    AlmacenDAO almacenDAO;

    @Autowired
    ArchivoDAO archivoDAO;

    @Autowired
    StorageService swiftService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Aula findAula(Long idaula) {
        return aulaDAO.find(idaula);
    }

    @Override
    public List<Inventario> allByDynatable(DynatableFilter filter, Aula aula) {
        List<Inventario> inventarios = inventarioDAO.allByDynatable(filter, aula);
        List<Long> idInventarios = inventarios.stream().map(x -> x.getId()).collect(Collectors.toList());
        List<Archivo> archivos = archivoDAO.allByInstanciasTipoInstancia(idInventarios, InstanciaEnum.INVENTARIO);
        Map<Long, Archivo> archivosMap = TypesUtil.convertListToMap("idInstancia", archivos);
        for (Inventario inventarioo : inventarios) {
            Archivo archivo = archivosMap.get(inventarioo.getId());
            if (archivo != null) {
                inventarioo.setImagen(archivo.getRuta());
            }
        }
        return inventarios;
    }

    @Override
    @Transactional
    public void update(Inventario inventario, Usuario user) {
        Inventario inventarioDb = inventarioDAO.find(inventario.getId());
        inventario.setAlmacen(inventarioDb.getAlmacen());
        inventario.setCodigo(inventario.getCodigo().trim());
        inventario.setFechaRegistro(inventarioDb.getFechaRegistro());
        inventario.setUserRegistro(inventarioDb.getUserRegistro());
        inventario.setEstado(inventarioDb.getEstado());
        inventarioDAO.update(inventario);
        String imagen = inventario.getImagentemporal().trim();
        if (!Strings.isNullOrEmpty(imagen)) {
            Archivo archivo = archivoDAO.findFirstByInstanciasTipoInstancia(inventario.getId(), InstanciaEnum.INVENTARIO);
            this.sendArchivoS3(imagen);
            if (archivo != null) {
                archivo.setRuta(AcademicoConstantine.S3_URL_ACADEMICO + AcademicoConstantine.S3_DIR_INVENTARIO + imagen);
                archivo.setNombre(imagen);
                archivoDAO.update(archivo);
            } else {
                archivo = new Archivo();
                archivo.setFechaRegistro(new Date());
                archivo.setUsuarioRegistro(user);
                archivo.setIdInstancia(inventario.getId());
                archivo.setInstancia(InstanciaEnum.INVENTARIO.name());
                archivo.setRuta(AcademicoConstantine.S3_URL_ACADEMICO + AcademicoConstantine.S3_DIR_INVENTARIO + imagen);
                archivo.setNombre(imagen);
                archivoDAO.save(archivo);
            }
        }
    }

    @Override
    @Transactional
    public void save(Inventario inventario, Usuario user) {

        Integer times = inventario.getTimes();

        if (times != null) {
            if (times > 0) {
                this.saveMultipleInventariado(inventario, user);
                return;
            }
        }

        Almacen almacen = almacenDAO.findByAula(inventario.getAlmacen().getAula());
        if (almacen == null) {
            almacen = new Almacen();
            almacen.setAula(inventario.getAlmacen().getAula());
            almacen.setUserRegistro(user);
            almacen.setFechaRegistro(new Date());
            almacenDAO.save(almacen);
        }
        inventario.setAlmacen(almacen);
        inventario.setCodigo(inventario.getCodigo().trim());
        inventario.setFechaRegistro(new Date());
        inventario.setUserRegistro(user);
        inventario.setEstadoEnum(EstadoInventarioEnum.DISP);
        inventarioDAO.save(inventario);
        String imagen = inventario.getImagentemporal().trim();

        if (!Strings.isNullOrEmpty(imagen)) {
            this.sendArchivoS3(imagen);
            Archivo archivo = new Archivo();
            archivo.setFechaRegistro(new Date());
            archivo.setUsuarioRegistro(user);
            archivo.setIdInstancia(inventario.getId());
            archivo.setInstancia(InstanciaEnum.INVENTARIO.name());
            archivo.setRuta(AcademicoConstantine.S3_URL_ACADEMICO + AcademicoConstantine.S3_DIR_INVENTARIO + imagen);
            archivo.setNombre(imagen);
            archivoDAO.save(archivo);
        }

        ResumenInventario resumen = resumenInventarioDAO.findByAlmacenProducto(almacen, inventario.getProducto());
        if (resumen == null) {
            resumen = new ResumenInventario();
            resumen.setVisibleReporteParcial(0);
            resumen.setAlmacen(almacen);
            resumen.setProducto(inventario.getProducto());
            resumen.setCantidad(1);
            resumenInventarioDAO.save(resumen);
            return;
        }
        resumen.setCantidad((resumen.getCantidad() + 1));
        resumenInventarioDAO.update(resumen);
    }

    @Transactional
    private void saveMultipleInventariado(Inventario inventario, Usuario user) {

        inventario.setTimes(Math.abs(inventario.getTimes()));

        Almacen almacen = almacenDAO.findByAula(inventario.getAlmacen().getAula());

        if (almacen == null) {
            almacen = new Almacen();
            almacen.setAula(inventario.getAlmacen().getAula());
            almacen.setUserRegistro(user);
            almacen.setFechaRegistro(new Date());
            almacenDAO.save(almacen);
        }

        ResumenInventario resumen = resumenInventarioDAO.findByAlmacenProducto(almacen, inventario.getProducto());

        if (resumen == null) {
            resumen = new ResumenInventario();
            resumen.setVisibleReporteParcial(0);
            resumen.setAlmacen(almacen);
            resumen.setProducto(inventario.getProducto());
            resumen.setCantidad(0);
            resumenInventarioDAO.save(resumen);
        }

        for (int i = 0; i < inventario.getTimes(); i++) {

            Inventario inventarioNew = new Inventario();
            inventarioNew.setAlmacen(almacen);
            inventarioNew.setProducto(inventario.getProducto());
            inventarioNew.setFechaRegistro(new Date());
            inventarioNew.setUserRegistro(user);
            inventarioNew.setEstadoEnum(EstadoInventarioEnum.DISP);

            inventarioNew.setMarca(inventario.getMarca());

            inventarioNew.setModelo(inventario.getModelo());
            inventarioNew.setSerie(inventario.getSerie());
            inventarioNew.setAnoFabricacion(inventario.getAnoFabricacion());

            inventarioNew.setMaterial(inventario.getMaterial());
            inventarioNew.setLargo(inventario.getLargo());
            inventarioNew.setAncho(inventario.getAncho());
            inventarioNew.setAlto(inventario.getAlto());

            inventarioNew.setColor(inventario.getColor());
            inventarioNew.setCondicion(inventario.getCondicion());
            inventarioNew.setFechaIngreso(inventario.getFechaIngreso());
            inventarioNew.setFechaBaja(inventario.getFechaBaja());

            inventarioNew.setProveedor(inventario.getProveedor());
            inventarioNew.setFechaVencimientoGarantia(inventario.getFechaVencimientoGarantia());
            inventarioNew.setVidaUtil(inventario.getVidaUtil());
            inventarioNew.setComentario(inventario.getComentario());

            inventarioDAO.save(inventarioNew);
            resumen.setCantidad((resumen.getCantidad() + 1));
        }

        resumenInventarioDAO.update(resumen);

    }

    @Override
    @Transactional
    public void delete(Inventario inventario) {

        Archivo archivo = archivoDAO.findFirstByInstanciasTipoInstancia(inventario.getId(), InstanciaEnum.INVENTARIO);
        if (archivo != null) {
            this.deleteArchivoS3(archivo.getNombre());
            archivoDAO.delete(archivo);
        }

        Inventario inventarioDb = inventarioDAO.find(inventario.getId());
        ResumenInventario resumen = resumenInventarioDAO.findByAlmacenProducto(inventarioDb.getAlmacen(), inventarioDb.getProducto());
        if (resumen != null) {
            if (resumen.getCantidad() > 0) {
                resumen.setCantidad((resumen.getCantidad() - 1));
                resumenInventarioDAO.update(resumen);
            }
        }
        inventarioDAO.delete(inventarioDb);
    }

    @Override
    public Inventario find(Inventario inventario) {
        Inventario inventarioDb = inventarioDAO.find(inventario.getId());
        Archivo archivo = archivoDAO.findFirstByInstanciasTipoInstancia(inventario.getId(), InstanciaEnum.INVENTARIO);
        if (archivo != null) {
            inventarioDb.setImagen(archivo.getRuta());
        }
        return inventarioDb;
    }

    @Override
    public List<Producto> allProducto() {
        List<Producto> productos = productoDAO.allTipoBienes();
        List<Producto> masters = productos.stream().filter(producto -> producto.getProductoSuperior() == null).collect(Collectors.toList());
        Map<Long, List<Producto>> productosMasterMap = TypesUtil.convertListToMapList("productoSuperior.id", productos);
        masters.stream().forEach(producto -> {
            List<Producto> childproduct = productosMasterMap.get(producto.getId());
            producto.setProductos(childproduct);
        });
        return masters;
    }

    @Override
    @Transactional
    public void saveProducto(Producto producto, Usuario user) {
        Producto last = productoDAO.findLastByCodeInventario(CodigoTipoProductoEnum.BIENES);
        if (last == null) {
            producto.setCodigo("INV000001");
        } else {
            int i = Integer.parseInt(last.getCodigo().substring(3, 9));
            i++;
            String full = String.format("%06d", i);
            producto.setCodigo("INV" + full);
        }
        Producto productoRegistrado = productoDAO.findByCodigo(producto.getCodigo());
        if (productoRegistrado != null) {
            throw new PhobosException("Código de producto ya registrado");
        }
        TipoProducto tipoProducto = tipoProductoDAO.findByCode(CodigoTipoProductoEnum.BIENES);
        producto.setTipoProducto(tipoProducto);
        producto.setUnidadPrincipal(new UnidadMedida(1));
        productoDAO.save(producto);
    }

    @Override
    public List<ResumenInventario> allResumenByDynatable(DynatableFilter filter, Aula aula) {
        return resumenInventarioDAO.allByDynatable(filter, aula);
    }

    @Override
    @Transactional
    public void updateResumen(ResumenInventario resumen) {
        ResumenInventario resumenDb = resumenInventarioDAO.find(resumen.getId());
        resumenDb.setVisibleReporteParcial(resumen.getVisibleReporteParcial());
        resumenInventarioDAO.update(resumenDb);
    }

    private void sendArchivoS3(String nombreArchivo) {
        File file = new File(GlobalConstantine.TMP_DIR + nombreArchivo);
        logger.debug("el archivo {} existe {} ", (GlobalConstantine.TMP_DIR + nombreArchivo), (file.exists()));
        if (!file.exists()) {
            throw new PhobosException("No existe el archivo en el servidor");
        }
        swiftService.uploadFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, AcademicoConstantine.S3_DIR_INVENTARIO, GlobalConstantine.TMP_DIR, nombreArchivo, true);
    }

    private void deleteArchivoS3(String nombreArchivo) {
        swiftService.deleteFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, AcademicoConstantine.S3_DIR_INVENTARIO, nombreArchivo);
    }

    @Override
    @Transactional
    public void updateInventarioCode(List<Inventario> inventarios, Usuario user) {
        if(inventarios.isEmpty()){
            return;
        }
        List<Inventario> inventariosFilter=inventarios.stream()
                .filter(x->x.getCodeEdit()==true)
                .collect(Collectors.toList());
        List<Inventario> inventariosDb = inventarioDAO.allById(inventariosFilter);
        Map<Long,Inventario> inventariosDbMap = TypesUtil.convertListToMap("id", inventariosDb);
        for (Inventario inventario : inventariosFilter) {
            Inventario inventarioDb= inventariosDbMap.get(inventario.getId());
            inventarioDb.setCodigo(inventario.getCodigo());
            inventarioDAO.update(inventarioDb);
        }
    }

}
