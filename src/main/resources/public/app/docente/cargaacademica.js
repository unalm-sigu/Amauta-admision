new Vue({
    el: '#main',
    data: {
        docente: {
            id: iddocente,
            modalidadEstudio: {id: null},
            departamentoAcademico: {id: null},
            persona: {
                id: null,
                tipoDocumento: {id: null},
                paisNacer: {id: null},
                nacionalidad: {id: null},
                paisDomicilio: {id: null},
                ubicacionDomicilio: {id: null},
                ubicacionNacer: {id: null}
            },
        }
    },
    mounted: function () {

    },
    methods: {
        buildResponsable: function (record) {
            if (record.responsable != null) {
                return '<div class="block"><strong>Responsable:</strong> ' + record.responsable + '</div>';
            } else {
                return '<div class="text-danger block">Sin responsable</div>';
            }
        },
        buildSeccionesHtml: function (record) {
            var seccionesHtml = "";
            var secciones = record.secciones.split(",");
            for (var i = 0; i < secciones.length; i++) {
                seccionesHtml += '<div class="m-l-md inline"><a href="#" ';
                if (record.estado == 'ACEP' && secciones[i].split("|")[4] == "VER") {
                    seccionesHtml += 'class="notas-academicas"';
                } else if (secciones[i].split("|")[4] == "VER") {
                    seccionesHtml += 'class="ver-alumnos"';
                } else {
                    seccionesHtml += 'class="text-danger no-ver-alumnos"';
                }
                seccionesHtml += ' rel="' + secciones[i].split("|")[0] + '">' + secciones[i].split("|")[1];
                if (secciones[i].split("|")[3] != " ") {
                    seccionesHtml += " - " + secciones[i].split("|")[3];
                }
                seccionesHtml += '</a></div>';
            }
            return seccionesHtml;
        }
    }

});
