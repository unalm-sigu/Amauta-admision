package pe.edu.lamolina.amauta.controller.docente.notasacademicas.tipoevaluacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.dao.academico.TipoEvaluacionDAO;
import pe.edu.lamolina.model.academico.TipoEvaluacion;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TipoEvaluacionServiceImp implements TipoEvalucionService{

    @Autowired
    TipoEvaluacionDAO tipoEvaluacionDAO;

    @Override
    public List<TipoEvaluacion> allByDynatable(DynatableFilter filter) {
        return tipoEvaluacionDAO.allByDynaTable(filter);
    }

    @Override
    @Transactional
    public void save(TipoEvaluacion tipo) {

        // Generación del código a partir del nombre
        String nuevoCodigo = generarCodigo(tipo.getNombre());

        // Verifica si el código ya existe y ajusta si es necesario
        int contador = 1;
        String codigoOriginal = nuevoCodigo;
        while (tipoEvaluacionDAO.existsByCodigo(nuevoCodigo)) {
            nuevoCodigo = codigoOriginal + contador;
            contador++;
        }
        tipo.setCodigo(nuevoCodigo);

        List<TipoEvaluacion> evaluaciones = tipoEvaluacionDAO.findByOrdenGreater(220);

        for (TipoEvaluacion evaluacion : evaluaciones) {
            evaluacion.setOrden(evaluacion.getOrden() + 1);
            tipoEvaluacionDAO.update(evaluacion);

        }
        tipo.setOrden(220);
        tipoEvaluacionDAO.save(tipo);
    }

    @Override
    @Transactional
    public void actualizarTipoEvaluacion(TipoEvaluacion tipo) {
        TipoEvaluacion tipoDB = tipoEvaluacionDAO.find(tipo.getId());
        boolean nombre = tipoEvaluacionDAO.existsByNombre(tipo.getNombre());
        boolean codigo = tipoEvaluacionDAO.existsByCodigo(tipo.getCodigo());
        if (tipoDB == null) {
            throw new PhobosException("No tiene un informacion");
        }
        if(nombre){
            throw new PhobosException("Ya existe un nombre igual");
        }
        if(codigo){
            throw new PhobosException("Ya existe un codigo igual");
        }
        tipoDB.setNombre(tipo.getNombre());
        tipoDB.setCodigo(tipo.getCodigo());
        tipoEvaluacionDAO.update(tipoDB);
    }

    private String generarCodigo(String nombre) {
        StringBuilder codigo = new StringBuilder();
        String[] palabras = nombre.split(" ");

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                codigo.append(Character.toUpperCase(palabra.charAt(0)));
            }
        }

        return codigo.toString();
    }
}
