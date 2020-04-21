Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#responsableAulaVUE',
    data: {
        responsablesURL: APP.url(rutaModulo + '/list'),
        aula: {},
        modalResponsableAula: {
            id: 'modalResponsableAula',
            header: true,
            okbtn: 'Guardar',
            showaccept: true,
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            form: "formRespAula"
        },
        personas: [],
        aulas: [],
        personaSeleccionada: {},
        turnosAtencionAula: [],
        turnoAtencionSeleccionado: {},
        tiposResposablesAulas: JSON.parse(jTiposResponsables),
        responsableAula: {persona: {}, turnosAtencionAulas: [], jTipo: {}}
    },
    computed: {
    },
    mounted: function () {

    },
    methods: {
        nuevoResponsable() {
            this.$refs.modalResponsableAula.title = `Nuevo Responsable`;
            this.$refs.modalResponsableAula.open();
            this.personas = [];
            this.aulas = [];
            this.responsableAula = {persona: {}, turnosAtencionAulas: [], jTipo: {}}
            this.turnosAtencionAula = [];
            this.turnoAtencionSeleccionado = {};
            this.searchAula('%');

        }, editar(item) {
            this.$refs.modalResponsableAula.title = `Editar Responsable`;
            this.personas = [];
            this.aulas = [];
            this.searchAula('%');
            this.turnoAtencionSeleccionado = {};


            let vue = this;
            let responsable = Object.assign({}, item);

            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url(rutaModulo + '/editarResponsableAula'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                data: JSON.stringify(responsable),
                success: function (response) {
                    if (response.success) {
                        vue.responsableAula = response.data;
                        vue.turnosAtencionAula = response.data.turnosAtencionAulas;
                        vue.$refs.modalResponsableAula.open();
                        MODAL.hideWait();
                    } else {
                        MODAL.hideWait();
                        notify(response.message, "error");
                    }
                },
                error: function (response) {
                    MODAL.hideWait();
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });

        }, searchPersona(search) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/allPersonas'),
                type: 'POST',
                data: {nombre: search},
                success(response) {
                    if (response.success) {
                        $vue.personas = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }, searchAula(search) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/allAulas'),
                type: 'POST',
                data: {nombre: search},
                success(response) {
                    if (response.success) {
                        $vue.aulas = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }, changePersonaResponsable() {
            let $vue = this;
            //   this.responsableAula.tipo = this.responsableAula.tipo.code;

            $.ajax({
                url: APP.url(rutaModulo + '/changePersonaResponsable'),
                type: 'POST',
                data: JSON.stringify($vue.responsableAula),
                dataType: "json",
                contentType: "application/json",
                success(response) {
                    if (response.success) {
                        if (response.data.id != null && response.data.id != "") {
                            console.log("tiene id" + response.data.id);
                            // $vue.responsableAula.id = response.data.id;
                            $vue.responsableAula = response.data;
                            console.log("resp");
                            console.dir($vue.responsableAula.persona);
                        }
                        $vue.turnosAtencionAula = response.data.turnosAtencionAulas;
                        //  console.dir($vue.turnosAtencionAula);
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }, changeTurnoAtencion() {

        }, labelAula(item) {
            if (item.id == undefined) {
                return "";
            }
            let label = item.codigo;
            if (item.nombre != null && item.nombre != '') {
                label += ' ' + item.nombre;
            }
            return label;
        }, saveResponsableAula() {
            let vue = this;
//            let responsable = Object.assign({}, this.personaSeleccionada);
//            responsable.turnosAtencionAulas = this.turnosAtencionAula;
            this.responsableAula.turnosAtencionAulas = this.turnosAtencionAula;

            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url(rutaModulo + '/saveResponsableAula'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                data: JSON.stringify(vue.responsableAula),
                success: function (response) {
                    if (response.success) {
                        vue.$refs.raptorResponsables.loadRemoteData();
                        vue.$refs.modalResponsableAula.close();
                        MODAL.hideWait();
                        notify(response.message, "info");
                    } else {
                        MODAL.hideWait();
                        notify(response.message, "error");
                    }
                },
                error: function (response) {
                    console.dir(response);
                    MODAL.hideWait();
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }
    }
});
 