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
        filtroExcel:{},
        filtroExcelDto:{
            nota: false,
            curso_matriculado: false,
            ciclo_academico: null,
            tercera_vez: false,
            retiro_ciclo: false,
            electivo_matriculado: false,
            se_matriculo:'no',
        },
        cicloAcademicoError: false,
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
            $vue.filtroExcel={... $vue.filtroExcelDto};
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

            this.cicloAcademicoError = false;

            if (this.filtroExcel.cicloActual === 'no' && !this.filtroExcel.ciclo_academico) {
                this.cicloAcademicoError = true;
                return;
            }

            let urll = '';
            $vue.processreporte = true;

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
            // this.filtroExcel = {
            //     tipo_beca: null,
            //     ciclo_academico: null,
            //     veces_desaprobado: null,
            //     retiroCiclo: null,
            //     curso_matriculado: false,
            //     nota: false,
            //     cambioCarrera: false
            // };
        },
        eliminarTodos() {
            const $vue = this;

            // Modal de confirmación con diseño más llamativo
            const modal = bootbox.confirm({
                title: '<i class="fas fa-exclamation-triangle text-danger"></i> <strong>ACCIÓN CRÍTICA</strong>',
                message: `
            <div class="alert alert-danger border-0 mb-3">
                <div class="d-flex align-items-center">
                    <i class="fas fa-skull-crossbones fa-2x text-danger me-3"></i>
                    <div>
                        <h5 class="mb-1 text-danger fw-bold">¡ATENCIÓN! Esta acción es IRREVERSIBLE</h5>
                        <p class="mb-0">Se eliminarán <strong>TODOS</strong> los registros de becas PRONABEC permanentemente.</p>
                    </div>
                </div>
            </div>
            <div class="bg-light p-3 rounded">
                <p class="mb-2"><i class="fa fa-info-circle text-primary"></i> <strong>Antes de continuar, confirme que:</strong></p>
                <ul class="mb-0 small">
                    <li>Ha realizado una copia de seguridad</li>
                    <li>Tiene autorización para esta operación</li>
                    <li>Comprende que esta acción NO se puede deshacer</li>
                </ul>
            </div>
            <div class="mt-3 text-center">
                <strong class="text-danger">¿Está absolutamente seguro que desea continuar?</strong>
            </div>
        `,
                size: 'large',
                backdrop: true,
                closeButton: false,
                buttons: {
                    cancel: {
                        label: '<i class="fa fa-shield"></i> Cancelar y Mantener Datos',
                        className: "btn-secondary btn-modal me-2"
                    },
                    confirm: {
                        label: '<i class="fa fa-trash"></i> SÍ, ELIMINAR TODO',
                        className: "btn-danger btn-modal btn-procesar fw-bold text-uppercase"
                    }
                },
                callback: function(result) {
                    if (result) {
                        $vue.ejecutarEliminacionTotal();
                        return false;
                    } else {
                        notify("Operación cancelada. Los datos están seguros.", "info");
                        return true;
                    }
                },
                className: 'modal-eliminar-todos'
            });

            // Agregar estilos personalizados al modal
            modal.on('shown.bs.modal', function() {

                // Focus en el botón cancelar por seguridad
                $('.btn-secondary').focus();
            });
        },

        ejecutarEliminacionTotal() {
            const startTime = Date.now();

            // Actualizar UI del botón
            $(".btn-procesar").html('<i class="fas fa-skull fa-pulse"></i> ELIMINANDO TODO...');
            $(".btn-modal").prop('disabled', true);

            // Mostrar indicador de progreso
            const progressHtml = `
        <div class="mt-3 elimination-progress">
            <div class="d-flex align-items-center justify-content-center">
                <div class="spinner-border text-danger me-2" role="status"></div>
                <span class="text-danger fw-bold">PROCESANDO ELIMINACIÓN MASIVA...</span>
            </div>
            <div class="progress mt-2" style="height: 8px;">
                <div class="progress-bar bg-danger progress-bar-striped progress-bar-animated" 
                     style="width: 100%"></div>
            </div>
        </div>
    `;
            $('.bootbox-body').append(progressHtml);

            AXIOS.post(APP.url(`academico/becaspronabec/eliminarTodos`))
                .then(response => {
                    const processingTime = ((Date.now() - startTime) / 1000).toFixed(1);

                    // Restaurar estado de botones
                    $(".btn-modal").prop('disabled', false);
                    $(".btn-procesar").html('<i class="fas fa-trash-alt"></i> SÍ, ELIMINAR TODO');
                    $('.elimination-progress').remove();

                    if (response.data.success) {
                        // Notificación de éxito más dramática
                        notify(`
                    <div class="text-center">
                        <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                        <h6 class="mb-1">ELIMINACIÓN COMPLETADA</h6>
                        <small>Todos los registros han sido eliminados en ${processingTime}s</small>
                    </div>
                `, "success", 4000);

                        // Cerrar modal con efecto
                        $('.modal').fadeOut(300, function() {
                            $(this).modal('hide');
                        });

                        // Recarga con countdown
                        this.mostrarCountdownRecarga();

                    } else {
                        notify(`
                    <div class="text-center">
                        <i class="fas fa-exclamation-triangle fa-2x text-warning mb-2"></i>
                        <h6 class="mb-1">ERROR EN LA ELIMINACIÓN</h6>
                        <small>${response.data.message || "No se pudieron eliminar todos los registros"}</small>
                    </div>
                `, "error", 5000);
                    }
                })
                .catch(error => {
                    // Restaurar estado de botones
                    $(".btn-modal").prop('disabled', false);
                    $(".btn-procesar").html('<i class="fas fa-trash-alt"></i> SÍ, ELIMINAR TODO');
                    $('.elimination-progress').remove();

                    console.error('Error en eliminación masiva:', error);

                    notify(`
                <div class="text-center">
                    <i class="fas fa-times-circle fa-2x text-danger mb-2"></i>
                    <h6 class="mb-1">FALLO CRÍTICO</h6>
                    <small>Error de comunicación durante la eliminación</small>
                </div>
            `, "error", 6000);
                });
        },

        mostrarCountdownRecarga() {
            let countdown = 5;
            const countdownInterval = setInterval(() => {
                notify(`
            <div class="text-center">
                <i class="fas fa-sync-alt fa-spin text-primary fa-2x mb-2"></i>
                <h6 class="mb-1">RECARGANDO SISTEMA</h6>
                <div class="fs-4 fw-bold text-primary">${countdown}</div>
                <small>La página se recargará automáticamente</small>
            </div>
        `, "info", 1000);

                countdown--;

                if (countdown < 0) {
                    clearInterval(countdownInterval);
                    window.location.reload();
                }
            }, 1000);
        }





    }
});