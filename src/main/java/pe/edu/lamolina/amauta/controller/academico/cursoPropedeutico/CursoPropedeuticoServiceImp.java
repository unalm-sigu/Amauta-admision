package pe.edu.lamolina.amauta.controller.academico.cursoPropedeutico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoPropedeuticoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteCicloDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteDAO;
import pe.edu.lamolina.amauta.dao.aporte.ResumenAporteAlumnoDAO;
import pe.edu.lamolina.amauta.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.amauta.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCursoPropedeutico;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.AporteCiclo;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.enums.AportesEnum;
import pe.edu.lamolina.model.enums.CuentaBancariaEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoDeudaEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Transactional(readOnly = true)
public class CursoPropedeuticoServiceImp implements CursoPropedeuticoService {

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoCursoPropedeuticoDAO alumnoCursoPropedeuticoDAO;

    @Autowired
    ResumenAporteAlumnoDAO resumenAporteAlumnoDAO;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    AporteCicloDAO aporteCicloDAO;

    @Autowired
    AporteDAO aporteDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Override
    public List<MatriculaResumen> findMatriculaResumen(String nombre, CicloAcademico cicloAcademico) {

        return matriculaResumenDAO.allByNombreAndCiclo(nombre, cicloAcademico);
    }

    @Override
    public List<Seccion> findSeccion(String nombre, CicloAcademico cicloAcademico) {
        return seccionDAO.allByNombreAndCiclo(nombre, cicloAcademico);
    }

    @Override
    @Transactional
    public void save(AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, CicloAcademico cicloAcademico, Usuario usuario) {

        Seccion seccion = alumnoCursoPropedeuticoBean.getSeccion();
        BigDecimal precio = alumnoCursoPropedeuticoBean.getPrecio();
        for (MatriculaResumen matriculaResumen : alumnoCursoPropedeuticoBean.getMatriculaResumens()) {

            Alumno alumno = matriculaResumen.getAlumno();

            AlumnoCursoPropedeutico alumnoCursoPropedeutico = new AlumnoCursoPropedeutico();
            alumnoCursoPropedeutico.setEstado(EstadoEnum.ACT.name());
            alumnoCursoPropedeutico.setMatriculaResumen(matriculaResumen);
            alumnoCursoPropedeutico.setSeccion(seccion);
            alumnoCursoPropedeutico.setFechaRegistro(new Date());
            alumnoCursoPropedeutico.setPrecio(precio);
            alumnoCursoPropedeutico.setUserRegistro(usuario);
            alumnoCursoPropedeuticoDAO.save(alumnoCursoPropedeutico);

            ResumenAporteAlumno resumenAporteAlumno = resumenAporteAlumnoDAO.findByMatriculaResumen(matriculaResumen);
            if (resumenAporteAlumno == null) {
                resumenAporteAlumno = new ResumenAporteAlumno(matriculaResumen, usuario);
                resumenAporteAlumno.setMontoInicial(precio);
                resumenAporteAlumno.setMontoPendiente(precio);
                resumenAporteAlumno.setMontoTotal(precio);
                resumenAporteAlumnoDAO.save(resumenAporteAlumno);
            } else {
                resumenAporteAlumno.setMontoInicial(resumenAporteAlumno.getMontoInicial().add(precio));
                resumenAporteAlumno.setMontoPendiente(resumenAporteAlumno.getMontoPendiente().add(precio));
                resumenAporteAlumno.setMontoTotal(resumenAporteAlumno.getMontoTotal().add(precio));
                resumenAporteAlumnoDAO.update(resumenAporteAlumno);
            }
            Aporte aporte = aporteDAO.findByCode(AportesEnum.A53);
            AporteCiclo aporteCiclo = aporteCicloDAO.findByCicloAcademicoAporte(cicloAcademico, aporte);

            Date fechaVencimiento = new DateTime().plusDays(5).toDate();

            DeudaAlumno deudaAlumno = new DeudaAlumno();

            AporteAlumnoCiclo aporteAlumnoCiclo = new AporteAlumnoCiclo(aporteCiclo, resumenAporteAlumno, deudaAlumno);

            ObjectNode detalleJson = createDetalleJson(aporteAlumnoCiclo);
            deudaAlumno.setConcepto("Deuda Académica");
            deudaAlumno.setNumeroCuota(1);
            deudaAlumno.setAlumno(alumno);
            deudaAlumno.setCuentaBancaria(aporteCiclo.getCuentaBancaria());
            deudaAlumno.setEstadoEnum(DeudaEstadoEnum.DEU);
            deudaAlumno.setMonto(precio);
            deudaAlumno.setTipoDeudaEnum(TipoDeudaEnum.APO);
            deudaAlumno.setDetalleJson(detalleJson.toString());
            deudaAlumno.setUserRegistro(usuario);
            deudaAlumno.setFechaRegistro(new Date());
            deudaAlumno.setAbono(BigDecimal.ZERO);
            deudaAlumno.setFechaVencimiento(fechaVencimiento);
            deudaAlumno.setFechaEmision(new Date());
            deudaAlumnoDAO.save(deudaAlumno);
            //logger.info("\tSe genero deuda-alumno con un monto de {}", monto);

            Acreencia acreencia = new Acreencia();

            acreencia.setDescripcion("Deuda Académica");
            acreencia.setOficina(new Oficina(OficinaEnum.OBUAE.getId()));
            acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_ALUMNO);
            acreencia.setInstanciaTabla(deudaAlumno.getId());
            acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
            acreencia.setMonto(precio);
            acreencia.setAbono(BigDecimal.ZERO);
            acreencia.setPersona(alumno.getPersona());
            acreencia.setCuentaBancaria(aporteCiclo.getCuentaBancaria());
            acreencia.setFechaDocumento(new Date());
            acreencia.setUsuarioRegistro(usuario);
            acreencia.setFechaVencimiento(fechaVencimiento);
            acreencia.setFechaRegistro(new Date());
            acreenciaDAO.save(acreencia);

        }

    }

    @Override
    @Transactional
    public void update(AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, CicloAcademico cicloAcademico, Usuario usuario) {

        List<AlumnoCursoPropedeutico> alumnoCursoPropedeuticosDB = alumnoCursoPropedeuticoDAO.allBySeccion(alumnoCursoPropedeuticoBean.getSeccion());

        ListsInspector inspector = TypesUtil.analizeLists(alumnoCursoPropedeuticosDB, alumnoCursoPropedeuticosDB, "id");
        List<AlumnoCursoPropedeutico> acpDead = inspector.getDeadList();
        List<AlumnoCursoPropedeutico> acpNew = inspector.getNewList();

        for (AlumnoCursoPropedeutico alumnoCursoPropedeutico : acpDead) {
            alumnoCursoPropedeutico.setEstado(EstadoEnum.INA.name());
            alumnoCursoPropedeuticoDAO.update(alumnoCursoPropedeutico);
        }
        for (AlumnoCursoPropedeutico alumnoCursoPropedeuticoForm : acpNew) {
            AlumnoCursoPropedeutico alumnoCursoPropedeutico = new AlumnoCursoPropedeutico();
            alumnoCursoPropedeutico.setEstado(EstadoEnum.ACT.name());
            alumnoCursoPropedeutico.setMatriculaResumen(alumnoCursoPropedeuticoForm.getMatriculaResumen());
            alumnoCursoPropedeutico.setSeccion(alumnoCursoPropedeuticoBean.getSeccion());
            alumnoCursoPropedeutico.setFechaRegistro(new Date());
            alumnoCursoPropedeutico.setUserRegistro(usuario);
            alumnoCursoPropedeuticoDAO.save(alumnoCursoPropedeutico);
        }

    }

    @Override
    public List<AlumnoCursoPropedeutico> list(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return alumnoCursoPropedeuticoDAO.allBySeccionDynatable(filter, cicloAcademico);
    }

    private ObjectNode createDetalleJson(AporteAlumnoCiclo aporteAlumnoCiclo) {
        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);

        ObjectNode node = JsonHelper.createJson(aporteAlumnoCiclo, JsonNodeFactory.instance, new String[]{
            "*",
            "aporteCiclo.*",
            "aporteCiclo.aporte.*",
            "resumenAporteAlumno.*",
            "resumenAporteAlumno.matriculaResumen.*"
        });

        json.set("data", node);
        return json;
    }

}
