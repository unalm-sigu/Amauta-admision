Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#pageGpoResolucionForm',
    data: {
        URL_TRAMITES: APP.url('academico/resolucion/listTramites'),
        resolucion: null,
        tiposResoluciones: null,
        files: []
    }, created: function () {
        this.loadResolucionForm();
    }, mounted: function () {
        let $vue = this;

    }, methods: {
        loadResolucionForm: function () {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/resolucion/loadModalResolucion'),
                type: 'post',
                success: function (response) {
                    if (response.success) {
                        $vue.resolucion = response.data.resolucionJson;
                        $vue.tiposResoluciones = response.data.tiposResolucionesJson;
                        console.dir(response.data);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})