package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ClonGpoSeccionServiceImp implements ClonGpoSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Override
    @Transactional
    public void clonarCiclo(CicloAcademico cicloOrigenForm, CicloAcademico cicloDestinoForm, DataSessionPivot ds) {
        logger.debug("clonarCiclo");
        CicloAcademico cicloOrigen = cicloAcademicoDAO.find(cicloOrigenForm.getId());

        CicloAcademico cicloDestino = cicloAcademicoDAO.find(cicloDestinoForm.getId());

        JsonNodeFactory jFactory = JsonNodeFactory.instance;
        ObjectMapper mapper = new ObjectMapper();

        List<GrupoSeccion> grupoSecciones = grupoSeccionDAO.allByCicloClone(cicloOrigen);
        logger.debug("grupoSecciones size  {}", grupoSecciones.size());

        List<AnexoBoletin> anexoBoletines = grupoSecciones.stream().map(x -> x.getAnexoBoletin()).collect(Collectors.toList());
        logger.debug("anexoBoletines size  {}", anexoBoletines.size());

        for (AnexoBoletin anexoBoletine : anexoBoletines) {

            ObjectNode anexoBoletineNode = JsonHelper.createJson(anexoBoletine, jFactory, new String[]{"*",
                "departamentoAcademico.id",
                "carrera.id",
                "anexoSuperior.id",});

            try {

                AnexoBoletin anexoBoletineNew = mapper.readValue(anexoBoletineNode.toString(), AnexoBoletin.class);
                anexoBoletineNew.setId(null);
                anexoBoletinDAO.save(anexoBoletineNew);
                ObjectUtil.printAttr(anexoBoletineNew);

            } catch (IOException ex) {
                logger.debug("error after copy anexoBoletin  {}", anexoBoletine.getId());
            }
        }

        for (GrupoSeccion grupoSeccione : grupoSecciones) {

            ObjectNode grupoSeccioneNode = JsonHelper.createJson(grupoSeccione, jFactory, new String[]{"*",
                "departamentoAcademico.id",
                "carrera.id",
                "anexoSuperior.id",});

            try {

                GrupoSeccion grupoSeccionNew = mapper.readValue(grupoSeccioneNode.toString(), GrupoSeccion.class);
                grupoSeccionNew.setId(null);
                grupoSeccionDAO.save(grupoSeccionNew);
                ObjectUtil.printAttr(grupoSeccionNew);

            } catch (IOException ex) {
                logger.debug("error after copy anexoBoletin");
            }

        }

        List<Seccion> secciones = seccionDAO.allByCicloClone(cicloOrigen);

        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allByCicloClone(cicloOrigen);

    }
}
