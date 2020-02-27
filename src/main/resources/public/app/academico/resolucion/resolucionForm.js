Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#pageGpoResolucionForm',
    data: {
        URL_TRAMITES_REUNION: APP.url('academico/resolucion/listTramiteReunionConsejo'),
        colorEstado: {CRE: "default", ACT: "success", ANU: "danger", BLO: "warning", FUS: "warning"},
        resolucion: null,
        tiposResoluciones: null,
        oficinas: null,
        reunionesConsejo: [],
        files: []
    }, 
    created: function () {
        this.resolucion = JSON.parse(resolucionJson);
        this.loadResolucionForm();
    }, 
    mounted: function () {
        let $vue = this;

    }, 
    methods: {
        loadResolucionForm: function () {
            let $vue = this;
            let dataVar = {};
            if ($vue.resolucion != null) {
                dataVar = {resolucion: $vue.resolucion.id};
            }

            $.ajax({
                url: APP.url('academico/resolucion/loadFormResolucion'),
                data: dataVar,
                type: 'post',
                success: function (response) {
                    if (response.success) {
                        $vue.resolucion = response.data.resolucionJson;
                        $vue.tiposResoluciones = response.data.tiposResolucionesJson;
                        $vue.oficinas = response.data.oficinasJson;
                        if ($vue.resolucion.id != "") {
                            $vue.loadTramitesConsejo();
                        }
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, 
        activacionTramite(tramite) {
            if (!tramite.activo) {
                tramite.seleccionado = false;
            }
        },
        saveResolucion(event) {
            if (event) {
                event.preventDefault();
            }
            let $vue = this;
            var form = $("[id='frmResolucion']");

            form.find(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });
            form.find('.multiselect__input').each(function () {
                var input = $(this);
                let element = input.closest('.multiselect').find('.multiselect__single');

                if (element.css('display') != 'none' && element.html() != "") {
                    $(this).removeAttr("required");
                }
            });


            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            this.resolucion.tramitesReunionConsejo = this.$refs.tblTramitesReunion.data;

            $.ajax({
                url: APP.url('academico/resolucion/saveResolucion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        if (response.data.operation == "s") {
                            location.href = APP.url('academico/resolucion/succesSave');
                        } else {
                            notify(response.message, "info");
                        }
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        saveConfirmarResVB(event) {
            let $vue = this;
            if (event) {
                event.preventDefault();
            }
            $.ajax({
                url: APP.url('academico/resolucion/saveConfirmarResVB'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        if (response.data.operation == "s") {
                            location.href = APP.url('academico/resolucion/succesSaveResVB');
                        } else {
                            notify(response.message, "info");
                        }
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, 
        cambiarOficina() {
            let $vue = this;
            $vue.reunionesConsejo = [];
            $.ajax({
                method: 'POST',
                url: APP.url('academico/resolucion/cambiarOficina'),
                data: {
                    oficina: $vue.resolucion.oficina.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.reunionesConsejo = response.data.reunionesConsejo;

                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, 
        loadTramitesConsejo() {
            let $vue = this;
            if ((this.resolucion.reunionConsejo != null && this.resolucion.reunionConsejo.id != "")
                    && (this.resolucion.tipoResolucion != null && this.resolucion.tipoResolucion.id != "")) {
                let params = {
                    reunionConsejo: this.resolucion.reunionConsejo.id,
                    tipoResolucion: this.resolucion.tipoResolucion.id
                };
                if ($vue.resolucion != null && $vue.resolucion.id != "") {
                    params = Object.assign(params, {resolucion: $vue.resolucion.id});
                }
                this.$refs.tblTramitesReunion.ajaxdata = params;
            }
            this.$refs.tblTramitesReunion.loadRemoteData();
        }, 
        customLabel( { fecha }) {
            return `${fecha}`;
        }, 
        getEstadoClass: function (estadoCode) {
            return "label-" + this.colorEstado[estadoCode];
        }
    }
})