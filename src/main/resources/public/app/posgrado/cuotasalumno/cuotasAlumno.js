Vue.component("multiselect", window.VueMultiselect.default);

var app = new Vue({
    el: '#cuotasAlumnoMain',
    data: {
        URL: 'posgrado/cuotasalumno',
        alumnoResumenCuotas: JSON.parse(alumnoResumenCuotasJson),
        tarifaCarrera: null,
        tarifasCarreras: JSON.parse(tarifasCarrerasJson),
        totalTarifaConceptoMonto: 0
    },
    created: function () {
        /*
         this.alumnoResumenCuotas.alumnoConceptosMatricula = null;
         this.alumnoResumenCuotas.alumnoCuotasMatricula = null;
         */
    },
    mounted: function () {
    },
    methods: {
        loadGeneracionCuotas() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${$vue.URL}/loadCuotasAlumnosPage`),
                data: {
                    alumno: $vue.alumno.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tarifasCarreras = response.data.tarifasCarreras;
                    }
                }
            });
        },
        labelTarifa(item) {
            if (item.cicloInicio == undefined) {
                return "";
            }
            return item.cicloInicio.descripcion;
        },
        changeTarifa() {
            let $vue = this;
            if ($vue.alumnoResumenCuotas.tarifaCarrera == null || $vue.alumnoResumenCuotas.tarifaCarrera.tarifasConcepto == null) {
                return;
            }
            let tarifasConceptos = $vue.alumnoResumenCuotas.tarifaCarrera.tarifasConcepto;
            this.alumnoResumenCuotas.porcentajeMontoInicial = tarifasConceptos[1].porcentajeInicial;
            this.pagoAlCash();
            this.calcularCreditosExceso();
        }, pagoAlCash() {
            if (this.alumnoResumenCuotas.pagoCash) {
                this.alumnoResumenCuotas.porcentajeMontoInicial = 100;
                this.alumnoResumenCuotas.cuotas = 0;
            } else {
                let tarifasConceptos = this.alumnoResumenCuotas.tarifaCarrera.tarifasConcepto;
                this.alumnoResumenCuotas.porcentajeMontoInicial = tarifasConceptos[1].porcentajeInicial;
            }
        }, calcularCreditosExceso() {
            if ((this.alumnoResumenCuotas == null || this.alumnoResumenCuotas.tarifaCarrera == null)
                    || this.alumnoResumenCuotas.tarifaCarrera.creditosMaximo == "") {
                this.alumnoResumenCuotas.creditosExceso = 0;
            }


            if (this.alumnoResumenCuotas.creditosMaximo <= this.alumnoResumenCuotas.tarifaCarrera.creditosMinimo) {
                this.alumnoResumenCuotas.creditosExceso = 0;
            } else if (this.alumnoResumenCuotas.creditosMaximo <= this.alumnoResumenCuotas.tarifaCarrera.creditosMaximo) {
                this.alumnoResumenCuotas.creditosExceso = 0;
            } else {
                this.alumnoResumenCuotas.creditosExceso
                        = parseInt(this.alumnoResumenCuotas.creditosMaximo) - parseInt(this.alumnoResumenCuotas.tarifaCarrera.creditosMaximo);
            }
        }, generarCuotas() {
            let $vue = this;
            delete $vue.alumnoResumenCuotas.alumnoConceptosMatricula;
            delete $vue.alumnoResumenCuotas.alumnoCuotasMatricula;
            $.ajax({
                method: 'POST',
                url: APP.url(`${$vue.URL}/generarCuotasAlumno`),
                data: JSON.stringify($vue.alumnoResumenCuotas),
                async: false,
                dataType: "json",
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$set($vue.alumnoResumenCuotas, 'alumnoConceptosMatricula', response.data.alumnoConceptosMatricula);
                        $vue.$set($vue.alumnoResumenCuotas, 'alumnoCuotasMatricula', response.data.alumnoCuotasMatricula);

                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }, grabarCuotasAlumno() {
            let $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url(`${$vue.URL}/grabarCuotasAlumno`),
                data: JSON.stringify($vue.alumnoResumenCuotas),
                async: false,
                dataType: "json",
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }
    }, watch: {

    }
});
