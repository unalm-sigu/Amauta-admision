Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#resolucionReinForm',
    data: {
        resolucion: {reincorporaciones: [], retiroCiclo: [], cambioNota: []},
        oficinas: JSON.parse(oficinasJson),
        ciclos: JSON.parse(ciclosJson),
        tiposResolucion: JSON.parse(tiposResolucionJson),
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
        alumnos: [],
        cursos: [],
        isReincorporacion: false,
        isRetiroCiclo: false,
        isCambioNota: false
    }, created: function () {

    }, mounted: function () {
        let $vue = this;
        $(".numerico").numeric({negative: false});
    }, methods: {
        tipoResolucionSelect(item) {
            let $vue = this;
            $vue.isRetiroCiclo = false;
            $vue.isReincorporacion = false;
            $vue.isCambioNota = false;
            if (item.codigo == "RCI") {
                $vue.isRetiroCiclo = true;
            } else if (item.codigo == "REIC") {
                $vue.isReincorporacion = true;
            } else {
                $vue.isCambioNota = true;
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
        cicloCambioNota(ciclo, item) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/tramitecondicional/allCursosAlumnoByName"),
                dataType: 'json',
                type: 'post',
                data: {idAlumno: item.alumno.id, idCiclo: ciclo.id}
            }).then(response => {
                if (response.success) {
                    $vue.cursos = response.data;
                }

                this.isLoading = false;
            })

        },
        addResolucion() {
            let $vue = this;
            if ($vue.isReincorporacion) {
                var reincorporacion = {};
                $vue.resolucion.reincorporaciones.push(reincorporacion);
            } else if ($vue.isRetiroCiclo) {
                var retiroCiclo = {};
                $vue.resolucion.retiroCiclo.push(retiroCiclo);
            } else if ($vue.isCambioNota) {
                var cambioNota = {};
                $vue.resolucion.cambioNota.push(cambioNota);
            }
        },
        deleteItem(index) {
            let $vue = this;
            if ($vue.isReincorporacion) {
                $vue.resolucion.reincorporaciones.splice(index, 1);
            } else if ($vue.isRetiroCiclo) {
                $vue.resolucion.retiroCiclo.splice(index, 1);
            } else if ($vue.isCambioNota) {
                $vue.resolucion.cambioNota.splice(index, 1);

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
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url('academico/resolucion/save'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        $vue.resolucion = {reincorporaciones: [], retiroCiclo: [], cambioNota: []};
                        $vue.alumnos = [];
                    } else {
                        notify(response.message, 'error');
                    }
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})