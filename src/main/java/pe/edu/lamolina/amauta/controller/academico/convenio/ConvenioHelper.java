package pe.edu.lamolina.amauta.controller.academico.convenio;

import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.general.Pais;

public class ConvenioHelper {

    public String showCodigoPais(Pais pais) {
        if (pais == null) {
            return "";
        }
        return (String) ObjectUtil.getParentTree(pais, "codigo");
    }

    public Object getParentTree(Object obj, String attr) {
        return ObjectUtil.getParentTree(obj, attr);
    }
}
