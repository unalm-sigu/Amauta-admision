package pe.edu.lamolina.amauta.controller.docente.bloqueoIngresante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.IngresanteDAO;
import pe.edu.lamolina.amauta.dao.academico.PrelamolinaDAO;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaBloqueoIngresanteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;
import pe.edu.lamolina.model.enums.ModalidadIngresoEnum;
import pe.edu.lamolina.model.inscripcion.Ingresante;

@Service
@Transactional(readOnly = true)
public class MatriculaBloqueoIngresanteServiceImp implements MatriculaBloqueoIngresanteService {

    private final BigDecimal notaMinima = new BigDecimal(10.5);

    @Autowired
    MatriculaBloqueoIngresanteDAO matriculaBloqueoIngresanteDAO;
    @Autowired
    PrelamolinaDAO prelamolinaDAO;
    @Autowired
    IngresanteDAO ingresanteDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Override
    public List<MatriculaBloqueoIngresante> allByDynatable(DynatableFilter filter, DataSessionPivot ds) {
        return matriculaBloqueoIngresanteDAO.allByDynatable(filter, ds.getCicloAcademico());
    }

    @Override
    @Transactional
    public void copiaIngresantesAdmision(DataSessionPivot ds) {
        List<MatriculaBloqueoIngresante> listaBD = matriculaBloqueoIngresanteDAO.allByCicloAcademico(ds.getCicloAcademico());

        Assert.isTrue(listaBD.isEmpty(), "Ya se genero la copia de los ingresantes del ciclo " + ds.getCicloAcademico().getDescripcion());

        List<Ingresante> ingresantes = ingresanteDAO.allByCicloAcademico(ds.getCicloAcademico());

        List<Ingresante> ingresantesQuinto = new ArrayList();

        String numeroCiclo = ds.getCicloAcademico().getNumeroCiclo();//validar

        List<CicloAcademico> ciclosQuintosAnteriores = cicloAcademicoDAO.allMenorRegularPreByCantidad(2, ds.getCicloAcademico());
        if (numeroCiclo.equalsIgnoreCase("1")) {
            ingresantesQuinto = ingresanteDAO.allByCicloAcademicoModalidadIngreso(ciclosQuintosAnteriores, ModalidadIngresoEnum.QUINTO_SECUNDARIA.getCode());
        }

        ingresantes.addAll(ingresantesQuinto);
        
        for (Ingresante ingresante : ingresantes) {
            MatriculaBloqueoIngresante bloqueoIngresante = new MatriculaBloqueoIngresante();
            bloqueoIngresante.setIngresante(ingresante);
            bloqueoIngresante.setCicloAcademico(ds.getCicloAcademico());
            bloqueoIngresante.setInscrito(Boolean.FALSE);

            if (ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("04")
                    || ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("05")
                    || ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("08")) {//'04','05','08' solo rv y rm

                bloqueoIngresante.setRm(ingresante.getEvaluado().getPuntajeRm());
                bloqueoIngresante.setRv(ingresante.getEvaluado().getPuntajeRv());
                bloqueoIngresante.setMatricula(Boolean.FALSE);
                this.validarRvRM(ingresante, bloqueoIngresante, notaMinima);
            } else {

                if (ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("03")) {
                    bloqueoIngresante.setRm(ingresante.getPrelamolina().getPuntajeRm());
                    bloqueoIngresante.setRv(ingresante.getPrelamolina().getPuntajeRv());
                    bloqueoIngresante.setMatematica(ingresante.getPrelamolina().getPuntajeMatematicas());
                    bloqueoIngresante.setFisica(ingresante.getPrelamolina().getPuntajeFisica());
                    bloqueoIngresante.setQuimica(ingresante.getPrelamolina().getPuntajeQuimica());
                    bloqueoIngresante.setBiologia(ingresante.getPrelamolina().getPuntajeBiologia());
                } else {
                    bloqueoIngresante.setRm(ingresante.getEvaluado().getPuntajeRm());
                    bloqueoIngresante.setRv(ingresante.getEvaluado().getPuntajeRv());
                    bloqueoIngresante.setMatematica(ingresante.getEvaluado().getPuntajeMatematicas());
                    bloqueoIngresante.setFisica(ingresante.getEvaluado().getPuntajeFisica());
                    bloqueoIngresante.setQuimica(ingresante.getEvaluado().getPuntajeQuimica());
                    bloqueoIngresante.setBiologia(ingresante.getEvaluado().getPuntajeBiologia());
                }
                bloqueoIngresante.setMatricula(Boolean.FALSE);
                this.validarDemasMaterias(ingresante, bloqueoIngresante, notaMinima);

            }
            
            bloqueoIngresante.setFechaRegistro(new Date());
            bloqueoIngresante.setUsuario(ds.getUsuario());
            matriculaBloqueoIngresanteDAO.save(bloqueoIngresante);
        }

    }

    private void validarRvRM(Ingresante ingresante, MatriculaBloqueoIngresante bloqueoIngresante, BigDecimal notaMinima) {

        if (ingresante.getEvaluado().getPuntajeRm().compareTo(notaMinima) >= 0
                && ingresante.getEvaluado().getPuntajeRv().compareTo(notaMinima) >= 0) {
            bloqueoIngresante.setMatricula(Boolean.TRUE);
        }
    }

    private void validarDemasMaterias(Ingresante ingresante, MatriculaBloqueoIngresante bloqueoIngresante, BigDecimal notaMinima) {

        if (ingresante.getPrelamolina() != null) {
            if (ingresante.getPrelamolina().getPuntajeRm().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeRv().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeMatematicas().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeFisica().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeQuimica().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeBiologia().compareTo(notaMinima) >= 0) {

                bloqueoIngresante.setMatricula(Boolean.TRUE);

            }
        }
        if (ingresante.getEvaluado() != null) {
            if (ingresante.getEvaluado().getPuntajeRm().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeRv().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeMatematicas().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeFisica().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeQuimica().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeBiologia().compareTo(notaMinima) >= 0) {

                bloqueoIngresante.setMatricula(Boolean.TRUE);

            }

        }

    }

    @Override
    @Transactional
    public void actualizarMatricula(Long id, DataSessionPivot ds) {
        MatriculaBloqueoIngresante mbi = matriculaBloqueoIngresanteDAO.find(id);

        if (mbi.getMatricula()) {
            mbi.setMatricula(Boolean.FALSE);
        } else {
            mbi.setMatricula(Boolean.TRUE);
        }
        mbi.setFechaRegistro(new Date());
        mbi.setUsuarioActualiza(ds.getUsuario());
        matriculaBloqueoIngresanteDAO.update(mbi);
    }

    @Override
    public List<MatriculaBloqueoIngresante> allByCicloAcademico(Long ciclo) {
        return matriculaBloqueoIngresanteDAO.allByCicloAcademico(new CicloAcademico(ciclo));
    }

}
