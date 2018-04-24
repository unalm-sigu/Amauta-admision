new Vue({
    el: '#main',
    data: {
        alumno: JSON.parse(alumnoJson),
        ident: true,
        flag: true,
        typeSearch: false,
        ciclos: [{"ciclo": "1997 Primer Ciclo", "descripción": "1997-I", "promedio": 13.23, "promedioPonderadoAcum": 13.18, "CreditoCursadosCiclo": 13, "CreditoAprobadosAcu": 204, "CreditoAprobaCiclo": 13, "creditoAcumulado": 206, "situacionAcademica": "Egresado matriculable", "cursos": [{"curso": "Manejo de Cuencas", "codigo": "IA6011", "creditos": 3, "nota": "14"}, {"curso": "Materiales y Procedimientos de Construccion", "codigo": "IA4015", "creditos": 4, "nota": "14"}, {"curso": "Planeamiento Rural I", "codigo": "IA4020", "creditos": 4, "nota": "12"}, {"curso": "Redaccion Tecnica", "codigo": "EP1014", "creditos": 2, "nota": "13"}]}, {"ciclo": "1996 Segundo Ciclo", "descripción": "1996-II", "promedio": 12.82, "promedioPonderadoAcum": 13.18, "CreditoCursadosCiclo": 17, "CreditoAprobadosAcu": 191, "CreditoAprobaCiclo": 15, "creditoAcumulado": 193, "situacionAcademica": "Egresado matriculable", "cursos": [{"curso": "Analisis Macro Economico I", "codigo": "EP2005", "creditos": 4, "nota": "15"}, {"curso": "Concreto Reforzado", "codigo": "IA4004", "creditos": 4, "nota": "13"}, {"curso": "Ingenieria Economica", "codigo": "IA5008", "creditos": 3, "nota": "13"}, {"curso": "Proyectos de Irrigacion", "codigo": "IA6017", "creditos": 2, "nota": "08"}, {"curso": "Riegos y Recuperacion de Tierras II", "codigo": "IA5015", "creditos": 3, "nota": "12"}, {"curso": "Seminario No Graduado en Ingenieria Agricola", "codigo": "IA5016", "creditos": 1, "nota": "15"}]}]
    },
    computed: {
    },
    created() {
        let vue = this;
    },
    mounted: function() {
        let vue = this;
    },
    methods: {
        styleNota(nota) {
            if (nota < 11 || nota == 'DE') {
                return "text-danger";
            } else {
                return "text-primary";
            }
        },
        validarNota(curso, tipo) {
            if (!tipo) {
                return true;
            } else {
                if (curso.nota >= 11)
                    return true;
            }
        },
        agregarCiclo: function() {
            let vue = this;
            vue.ciclos.push({"ciclo": "1997 Primer Ciclo", "descripción": "1997-I", "promedio": 13.23, "promedioPonderadoAcum": 13.18, "CreditoCursadosCiclo": 13, "CreditoAprobadosAcu": 204, "CreditoAprobaCiclo": 13, "creditoAcumulado": 206, "situacionAcademica": "Egresado matriculable", "cursos": [{"curso": "Manejo de Cuencas", "codigo": "IA6011", "creditos": 3, "nota": "14"}, {"curso": "Materiales y Procedimientos de Construccion", "codigo": "IA4015", "creditos": 4, "nota": "14"}, {"curso": "Planeamiento Rural I", "codigo": "IA4020", "creditos": 4, "nota": "12"}, {"curso": "Redaccion Tecnica", "codigo": "EP1014", "creditos": 2, "nota": "13"}]});
        }
    }
});
