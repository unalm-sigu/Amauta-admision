Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#resolucionReinForm',
    data: {
        resolucion: {retiroCiclo: {}},
        oficinas: JSON.parse(oficinasJson),
        ciclos: JSON.parse(ciclosJson),
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
        alumnos: [],
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true
            if ($vue.resolucion.oficina == null) {
                notify("Seleccione una oficina.");
                return;
            }
            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/resolucion/allAlumno"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre, instanciaOficina: $vue.resolucion.oficina.instanciaOficina}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        oficinaSelect(ofi) {
            let $vue = this;
            if ($vue.resolucion.oficina != null) {
                if (ofi.id != $vue.resolucion.oficina.id) {
                    $vue.alumnos = [];
                }
            }
        },
        save() {
            let $vue = this;
            var valid = $('#form').parsley().validate();

            if (!valid) {
                return;
            }
            $.ajax({
                url: APP.url('academico/resolucion/saveRetiroCiclo'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        $vue.resolucion = {retiroCiclo: {}};
                        $vue.alumnos = [];
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})