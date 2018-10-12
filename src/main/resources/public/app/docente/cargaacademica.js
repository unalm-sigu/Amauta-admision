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
        tipoSeccion(seccion) {
            if (seccion.tipoSeccionEnum.value.indexOf(" ") < 0) {
                return seccion.tipoSeccionEnum.value;
            }
            return seccion.tipoSeccionEnum.value.split(" ")[0];
        },
        buildResponsable: function (record) {
            if (record.responsable != null) {
                return '<div class="block"><strong>Responsable:</strong> ' + record.responsable + '</div>';
            } else {
                return '<div class="text-danger block">Sin responsable</div>';
            }
        },
        buildSeccionesHtml: function (record) {
            console.log(record.secciones);
            var seccionesHtml = "";
//            var secciones = record.secciones.split(",");
            var secciones = record.secciones;
            for (var i = 0; i < secciones.length; i++) {
                console.log(secciones[i]);
                seccionesHtml += '<div class="m-l-md inline"><a href="#" ';
                if (record.estado == 'ACEP' && secciones[i].verInformacion) {
                    seccionesHtml += 'class="notas-academicas"';
                } else if (secciones[i].verInformacion) {
                    seccionesHtml += 'class="ver-alumnos"';
                } else {
                    seccionesHtml += 'class="text-danger no-ver-alumnos"';
                }
                seccionesHtml += ' rel="' + secciones[i].id + '">' + secciones[i].codigo2;
//                if (secciones[i].split("|")[3] != " ") {
                if (secciones[i].grupoHoras != '') {
                    seccionesHtml += " - " + secciones[i].grupoHoras.codigo;
                }
                seccionesHtml += '</a></div>';
            }
            return seccionesHtml;
        }
    }

});
