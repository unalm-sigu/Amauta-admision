Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker);
const BecasList = httpVueLoader('/app/academico/pronabec/BecasPronabecList.vue');
new Vue({
    el: '#main',
    data: {
        tipoBeca: JSON.parse(tipoBecaJson),
        ciclos: JSON.parse(ciclos),
        raptorBecados: null,
        modalNuevoBecc: {
            id: 'modalNuevoBecc',
            header: true,
            title: 'Nuevo Becado ',
            okbtn: "Guardar",
            showaccept: true
        },
        isLoading: false,
        configConfirmAction: VUE_MODAL.structConfirm({}),
        alumnos: [],
        alumnoBecado: {},
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
        modalReporteBec: {
            id: 'modalReporteBec',
            header: true,
            title: 'Reportes',
            okbtn: "Descargar",
            showaccept: true
        },
        filtroExcel:{
            nota: false,
            curso_matriculado: false,
            ciclo_academico: null,
            tercera_vez: false,
            retiro_ciclo: false,
            electivo_matriculado: false,
        },
    },
    components: {
        becasList: BecasList,
    },
    mounted: function () {
        const vm = this; // Para acceder al componente Vue

        // Inicializar el datepicker para fecha de inicio
        $('#datepickerInicio').datepicker({
            format: 'yyyy-mm-dd',
            autoclose: true
        }).on('changeDate', function (e) {
            vm.alumnoBecado.fechaInicio = e.format(); // Actualiza el modelo
        });

        // Inicializar el datepicker para fecha de fin
        $('#datepickerFin').datepicker({
            format: 'yyyy-mm-dd',
            autoclose: true
        }).on('changeDate', function (e) {
            vm.alumnoBecado.fechaFin = e.format();// Actualiza el modelo
        });
    },
    methods: {
        customLabel(item){
            if (item.id == undefined) {
                return "";
            }
            return item.numeroDocIdentidad + " - " + item.nombreCompleto;
        },
        openModal() {
            let $vue = this;
            $vue.alumnoBecado = {};
            $vue.$refs.modalNuevoBec.open();
            //console.log($vue);

        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url(`academico/becaspronabec/allAlumnoByNombre`),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;

                    }
                    this.isLoading = false;
                });

            }
        },
        save() {
            let $vue = this;
            var form = $("#formNuevo");
            if (!form.parsley().validate()) {
                return;
            }
            // console.dir($vue.$parent);
            // return;

            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`academico/becaspronabec/saveBecado`),
                data: JSON.stringify($vue.alumnoBecado),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {

                        //this.parents().becaLoad.loadRemoteData();
                        // swal({
                        //     title: 'Guardado satisfactoriamente',
                        //     icon: 'success',
                        //     buttons: {
                        //         ok: 'Aceptar'
                        //     }
                        // }).then((value) => {
                        //     if (value) {
                        //         location.reload();
                        //     }
                        // });
                        location.reload();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalNuevoBec.close();
                    MODAL.hideWait();

                },
                error: function () {
                    $vue.$refs.modalNuevoBec.close();
                    notify(Messages.errorComunicacion, "error");
                }

            });
        },
        openReporteModal(){
            let $vue = this;
            $vue.$refs.modalReporte.open();
        },
        generarReporte() {
            let $vue = this;
            let urll = '';
            $vue.processreporte = true;
            urll = APP.url(`academico/becaspronabec/exportExcel`);

            axios({
                url: urll,
                method: 'POST',
                responseType: 'blob',
            }).then((response) => {
                var namee = response
                    .headers["content-disposition"]
                    .replace("attachment; filename=", "")
                    .replace(/"/g, '');
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', namee);
                document.body.appendChild(link);
                link.click();
                $vue.processreporte = false;
            }).catch(error => {
                $vue.processreporte = false;
                notify(Messages.errorComunicacion, "error");
            });
        },
        reporteFiltro(){
            let $vue = this;
            var form = $("#formReporte");
            if (!form.parsley().validate()) {
                return;
            }
            let urll = '';
            $vue.processreporte = true;

            console.log(this.filtroExcel);
            // if(this.filtroExcel.tipoReporte == null){
            //     return;
            // }
            // if(this.filtroExcel.cicloActual == null){
            //     return;
            // }


            const objetoComoString = JSON.stringify(this.filtroExcel);
            if (this.filtroExcel.tipoReporte === 'general') {
                urll = this.filtroExcel.cicloActual === 'si'
                    ? APP.url('academico/becaspronabec/cicloActual/descargar')
                    : APP.url('academico/becaspronabec/cicloAnterior/descargar');
            } else {
                urll = this.filtroExcel.cicloActual === 'si'
                    ? APP.url('academico/becaspronabec/cicloActual/descargar')
                    : APP.url('academico/becaspronabec/cicloAnterior/descargar');
                    // : APP.url('academico/becaspronabec/filtroBecadosExcel');
            }


            $vue.$refs.modalReporte.beginProcessing();

            axios({
                url: urll,
                method: 'POST',
                data: objetoComoString,
                headers: {
                    'Content-Type': 'application/json'
                },
                responseType: 'blob',
            }).then((response) => {
                var namee = response
                    .headers["content-disposition"]
                    .replace("attachment; filename=", "")
                    .replace(/"/g, '');
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', namee);
                document.body.appendChild(link);
                link.click();

                $vue.processreporte = false;
                $vue.$refs.modalReporte.confirmReaction(true);
            }).catch(error => {
                $vue.processreporte = false;
                $vue.$refs.modalReporte.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });

        },
        clearFields() {
            this.filtroExcel = {
                tipo_beca: null,
                ciclo_academico: null,
                veces_desaprobado: null,
                retiroCiclo: null,
                curso_matriculado: false,
                nota: false,
                cambioCarrera: false
            };
        },


    }
});