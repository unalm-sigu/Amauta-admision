Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#main',
    data: {
        cursoDirigidoURL: APP.url("academico/cursodirigido/list"),
        listFacultad: JSON.parse(facultadesJson),
        facultad: {},
        isDisabled: false
    },
    computed: {

    },
    created() {
        let $vue = this;

    },
    mounted: function () {
        let $vue = this;

    },
    methods: {
        json(item) {
            if (item.situacionActual.cruceHorario != null) {
                return;
            }
            item.situacionActual = JSON.parse(item.situacionActual);
        },
        actualizar(item, accion) {
            let $vue = this;
            item.situacionActual = JSON.stringify(item.situacionActual);
            item.accionTramiteAcademicos = [];
            item.accionTramiteAcademicos.push(accion);
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/cursodirigido/update'),
                data: JSON.stringify(item),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        anular(item) {
            let $vue = this;
            var data = {};
            data.id = item.id;
            bootbox.confirm({
                message: "¿Está seguro que desea anular el curso dirigido?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result && !$vue.processing) {
                        MODAL.showWait("Espere un momento por favor");

                        $.ajax({
                            method: 'POST',
                            async: false,
                            url: APP.url('academico/cursodirigido/anular'),
                            data: JSON.stringify(data),
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.load.loadRemoteData();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error() {
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                        MODAL.hideWait();
                    }

                }
            });

        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        urlReporteDirigido(item) {
            let $vue = this;
            return APP.url('academico/tramiteacademico/cursodirigido/' + item.tramite.id + '/reporte');
        },
        classColor(estado) {
            switch (estado) {
                case 'SOL_ANU':
                case 'RHZ_SOL':
                    return  "label label-danger"
                    break;
                case 'SOL_CUR_DIR':
                    return  "label label-primary"
                    break;
                case 'RES_FAC':
                    return  "label label-success"
                    break;

                default:
                    return  "label label-primary"
                    break;
            }
        },
        changeFacultadSelected() {
            let $vue = this;
            $vue.$refs.load.querie.push({name: 'facultad-dirigido', value: $vue.facultad.id});
            $vue.$refs.load.repreload();
        },
        dowloadListFac() {
            let $vue = this;
            if ($vue.facultad.id == null) {
                notify("Debe seleccionar una facultad.", "error");
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            location.href = APP.url('academico/cursodirigido/listFacDirigido/' + $vue.facultad.id + '/reporte');
            MODAL.hideWait();
        },
        dowloadRepFac() {
            let $vue = this;
            if ($vue.facultad.id == null) {
                notify("Debe seleccionar una facultad.", "error");
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            location.href = APP.url('academico/cursodirigido/repFacDirigido/' + $vue.facultad.id + '/reporte');
            MODAL.hideWait();
        }
    }
});
