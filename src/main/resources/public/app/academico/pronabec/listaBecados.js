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
            okbtn: "Guardar",
            showaccept: true
        },
        filtroExcel:{
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
            let urll = '';
            $vue.processreporte = true;

            console.log(this.filtroExcel);

            const objetoComoString = JSON.stringify(this.filtroExcel);
            urll = APP.url('academico/becaspronabec/filtroBecadosExcel');

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
            }).catch(error => {
                $vue.processreporte = false;
                notify(Messages.errorComunicacion, "error");
            });

        },

        // searchPersona(nombre) {
        //     let $vue = this;
        //     if (nombre == null || nombre.trim().length == 0) {
        //         return;
        //     }
        //     $vue.listPersona = [];
        //     axios.get("/comun/buscar/allPersona", {params: {nombre: nombre}})
        //         .then(response => {
        //             $vue.listPersona = response.data.data;
        //         });
        // },
        // save() {
        //     let $vue = this;
        //     if (!$("#form-validar-escalafon").parsley().validate()) {
        //         notify.warning("Debe completar todos los campos requeridos.");
        //         return;
        //     }
        //     $vue.$refs.escalofonModal.beginProcessing();
        //     axios.post("/escalafon/save", $vue.escalafon)
        //         .then(function (response) {
        //             if (response.data.success) {
        //                 notify(response.data.message, 'success');
        //                 $vue.$refs.escalofonModal.confirmReaction(true);
        //                 if (response.data.data != null) {
        //                     location.href = $vue.editar(response.data.data);
        //                 }
        //                 $vue.$refs.raptorEscalafon.loadRemoteData();
        //             } else {
        //                 $vue.$refs.escalofonModal.confirmReaction(false);
        //                 notify(response.data.message, 'warning');
        //             }
        //         })
        //         .catch(function (error) {
        //             notify(error.errorComunicacion, "error");
        //             $vue.$refs.escalofonModal.confirmReaction(false);
        //         });
        // },
        // editar(item) {
        //     return APP.url('escalafon/update/' + item.id) + this.getOrigenURL();
        // },
        // ver(item) {
        //     return location.href = '/escalafon/info/' + item.id;
        // },
        // getOrigenURL() {
        //     var url = window.location.href;
        //     return "?origen=" + Base64.encode(url);
        // },
        // eliminar(item) {
        //     let $vue = this;
        //     $vue.configConfirmAction.message = Messages.confirmDelete;
        //     $vue.configConfirmAction.okbtn = "Si, eliminar";
        //     $vue.configConfirmAction.okclass = "btn-danger";
        //     $vue.configConfirmAction.okaction = function () {
        //         axios.post("/escalafon/eliminar", item).then(response => {
        //             $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
        //             if (response.data.success) {
        //                 notify(response.data.message, "success");
        //                 $vue.$refs.raptorEscalafon.loadRemoteData();
        //             } else {
        //                 notify(response.data.message, "warning");
        //             }
        //         }).catch(e => {
        //             $vue.$refs.modalConfirmAction.confirmReaction(false);
        //             notify(Messages.errorComunicacion, "error");
        //         });
        //     };
        //     $vue.$refs.modalConfirmAction.open();
        // }
    }
});