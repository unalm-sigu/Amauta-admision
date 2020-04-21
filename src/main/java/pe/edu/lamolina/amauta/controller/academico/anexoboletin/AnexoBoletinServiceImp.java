package pe.edu.lamolina.amauta.controller.academico.anexoboletin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoEnum.CRE;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;

@Service
@Transactional(readOnly = true)
public class AnexoBoletinServiceImp implements AnexoBoletinService {

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Override
    public List<AnexoBoletin> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<AnexoBoletin> anexos = anexoBoletinDAO.allByDynatable(filter);
        List<AnexoBoletin> anexosCount = anexoBoletinDAO.countGpoSeccByCiclo(ciclo);
        Map<Long, AnexoBoletin> mapAnexoCount = TypesUtil.convertListToMap("id", anexosCount);

        for (AnexoBoletin anexo : anexos) {
            AnexoBoletin anx = mapAnexoCount.get(anexo.getId());
            if (anx == null) {
                anexo.setCantidadGpoSecc(0L);
            } else {
                anexo.setCantidadGpoSecc(anx.getCantidadGpoSecc());
            }
        }
        Collections.sort(anexos, new AnexoBoletin.CompareOrden());
        return anexos;
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        List<AnexoBoletin> anexos = anexoBoletinDAO.allAnexosSuperiores();
        List<AnexoBoletin> anexosBoletin = new ArrayList();
        for (AnexoBoletin anx : anexos) {
            if (anx.getId() < 5) {
                anexosBoletin.add(anx);
            }
        }
        return anexosBoletin;
    }

    @Override
    @Transactional
    public void save(AnexoBoletin anexo, Usuario usuario) {
        anexo.setCodigo(anexo.getCodigo().toUpperCase());
        ObjectUtil.eliminarAttrSinId(anexo);
        AnexoBoletin superior = anexo.getAnexoSuperior();

        if (superior.getId() == 1L) {
            anexo.setDepartamentoAcademico(null);
            anexo.setCarrera(null);
        } else if (superior.getId() == 2L) {
            anexo.setCarrera(null);
        } else if (superior.getId() == 3L) {
            anexo.setCarrera(null);
        } else if (superior.getId() == 4L) {
            anexo.setDepartamentoAcademico(null);
        }

        if (anexo.getId() == null) {
            AnexoBoletin anexoCodeBD = anexoBoletinDAO.findByCode(anexo.getCodigo());
            Assert.isNull(anexoCodeBD, "Ya existe otro anexo con este código");
            anexo.setEstado(EstadoEnum.CRE.name());
            anexoBoletinDAO.save(anexo);

        } else {
            AnexoBoletin anexoBD = anexoBoletinDAO.find(anexo.getId());
            Assert.isTrue(Arrays.asList(ACT, CRE).contains(anexoBD.getEstadoEnum()), "Solo se puede modificar un anexo activo o creado recientemente");
            Assert.isTrue(anexoBD.getAnexoSuperior().getId() == superior.getId().longValue(), "No puede modificarse el anexo superior que lo contiene");
            AnexoBoletin anexoCodeBD = anexoBoletinDAO.findByCode(anexo.getCodigo());
            if (anexoCodeBD != null) {
                Assert.isTrue(anexoCodeBD.getId() == anexoBD.getId().longValue(), "Ya existe otro anexo con este código");
            }

            boolean sonIguales = ObjectUtil.verificarIgualdad(anexo, anexoBD, Arrays.asList("nombre", "codigo", "orden", "departamentoAcademico.id", "carrera.id"));
            Assert.isFalse(sonIguales, "No ha realizado ningún cambio");

            anexoBD.setNombre(anexo.getNombre());
            anexoBD.setCodigo(anexo.getCodigo());
            anexoBD.setDepartamentoAcademico(anexo.getDepartamentoAcademico());
            anexoBD.setCarrera(anexo.getCarrera());
            anexoBoletinDAO.update(anexoBD);
        }
    }

    @Override
    public AnexoBoletin find(Long id) {
        return anexoBoletinDAO.find(id);
    }

    @Override
    @Transactional
    public void cambiarEstado(AnexoBoletin anexo, String accion) {
        AnexoBoletin anexoBD = anexoBoletinDAO.find(anexo.getId());
        Assert.isNotNull(anexoBD, "El anexo-boletín que desea modificar no existe en el sistema");

        if (accion.equals("desactivar")) {
            Assert.isTrue(anexoBD.getEstadoEnum() == EstadoEnum.ACT, "El anexo-boletín que desea modificar ya no se encuentra activo");
            Assert.isNotBlank(anexo.getMotivoAnulacion(), "Debe indicar el motivo de la anulación");
            anexoBD.setEstado(EstadoEnum.INA.name());
            anexoBD.setMotivoAnulacion(anexo.getMotivoAnulacion());
            anexoBD.setFechaAnulacion(new Date());

            List<AnexoBoletin> anexos = anexoBoletinDAO.allBySuperior(anexoBD.getAnexoSuperior());
            for (AnexoBoletin anxBD : anexos) {
                if (anxBD.getOrden() > anexoBD.getOrden()) {
                    anxBD.setOrden(anxBD.getOrden() - 1);
                    anexoBoletinDAO.update(anxBD);
                }
            }
            anexoBoletinDAO.update(anexoBD);

        } else if (accion.equals("activar")) {
            Assert.isTrue(anexoBD.getEstadoEnum() != EstadoEnum.ACT, "El anexo-boletín que desea modificar ya se encuentra activo");
            anexoBD.setEstado(EstadoEnum.ACT.name());
            anexoBD.setMotivoAnulacion(null);
            anexoBD.setFechaAnulacion(null);

            int maximo = 1;
            boolean noExisteMayores = true;
            List<AnexoBoletin> anexos = anexoBoletinDAO.allBySuperior(anexoBD.getAnexoSuperior());
            for (AnexoBoletin anxBD : anexos) {
                if (anxBD.getId() != anexoBD.getId().longValue()) {
                    maximo = (anxBD.getOrden() > maximo) ? anxBD.getOrden() : maximo;
                }
                if (anxBD.getOrden() >= anexoBD.getOrden() && anxBD.getId() != anexoBD.getId().longValue()) {
                    anxBD.setOrden(anxBD.getOrden() + 1);
                    anexoBoletinDAO.update(anxBD);
                    noExisteMayores = false;
                }
            }
            if (noExisteMayores) {
                anexoBD.setOrden(maximo + 1);
            }
            anexoBoletinDAO.update(anexoBD);

        } else if (accion.equals("eliminar")) {
            anexoBoletinDAO.delete(anexoBD);
        }
    }

    @Override
    public AnexoResumen resumen() {
        return anexoBoletinDAO.resumen();
    }

    @Override
    @Transactional
    public void cambiarOrden(AnexoBoletin anexoBoletin, String direccion) {
        AnexoBoletin anexoBD = anexoBoletinDAO.find(anexoBoletin.getId());
        Assert.isTrue(anexoBD != null, "El anexo-boletín que desea modificar no existe en el sistema");
        Assert.isTrue(anexoBD.getEstadoEnum() == EstadoEnum.ACT, "El anexo-boletín que desea modificar no se encuentra activo");
        Integer salto = direccion.equals("subir") ? 1 : (direccion.equals("bajar") ? -1 : 0);
        Assert.isTrue(salto != 0, "No ha indicado correctamente en que sentido va cambiar el orden del anexo");

        Integer inicio = anexoBD.getOrden();
        Integer sgte = anexoBD.getOrden() + salto;
        AnexoBoletin anexoSgteBD = anexoBoletinDAO.findActivoByOrdenAnexoSuperior(sgte, anexoBD.getAnexoSuperior());
        Assert.isTrue(anexoSgteBD != null, "Ya superó el límite del orden de salto");

        anexoSgteBD.setOrden(inicio);
        anexoBD.setOrden(sgte);
        anexoBoletinDAO.update(anexoBD);
        anexoBoletinDAO.update(anexoSgteBD);

    }

    @Override
    public List<CicloAcademico> allCiclosByNombre(String nombre) {
        ModalidadEstudio pregrado = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.allByModalidadEstudioName(pregrado, nombre);
    }

    @Override
    public CicloAcademico findCiclo(CicloAcademico ciclo) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo.getId());
        Assert.isNotNull(cicloBD, "Este ciclo no existe en el sistema");
        return cicloBD;
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentosAcademicos() {
        return departamentoAcademicoDAO.allActivos();
    }

    @Override
    public List<Carrera> allCarrerasPosgrado() {
        return carreraDAO.allByModalidadEnum(ModalidadEstudioEnum.EPG);
    }

}
