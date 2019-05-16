Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#resolucionReinForm',
    data: {
        resolucion: {reincorporaciones: [], retiroCiclo: []},
        oficinas: JSON.parse(oficinasJson),
        ciclos: JSON.parse(ciclosJson),
        tiposResolucion: JSON.parse(tiposResolucionJson),
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
        alumnos: [],
        isReincorporacion: true
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        tipoResolucionSelect(item) {
            let $vue = this;
            if (item.codigo == "RCI") {
                $vue.isReincorporacion = false;
            } else {
                $vue.isReincorporacion = true;
            }
        },
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
                    url: APP.url("academico/resolucion/findAlumno"),
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
        addResolucion() {
            let $vue = this;
            if ($vue.isReincorporacion) {
                var reincorporacion = {};
                $vue.resolucion.reincorporaciones.push(reincorporacion);
            } else {
                var retiroCiclo = {};
                $vue.resolucion.retiroCiclo.push(retiroCiclo);
            }
        },
        deleteItem(index) {
            let $vue = this;
            if ($vue.isReincorporacion) {
                $vue.resolucion.reincorporaciones.splice(index, 1);
            } else {
                $vue.resolucion.retiroCiclo.splice(index, 1);
            }
        },
        oficinaSelect(ofi) {
            let $vue = this;
            if ($vue.resolucion.oficina != null) {
                if (ofi.id != $vue.resolucion.oficina.id) {
                    $vue.resolucion.reincorporaciones = [];
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
                url: APP.url('academico/resolucion/save'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        $vue.resolucion = {reincorporaciones: [], retiroCiclo: []};
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