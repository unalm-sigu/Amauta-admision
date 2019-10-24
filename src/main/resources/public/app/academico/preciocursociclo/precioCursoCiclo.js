Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#preciocursocicloVUE',
    data: {
        preciocursocicloURL: APP.url('academico/preciocursociclo/list'),
        verTabla: false,
        guardaPrecio: false,
        cfgCantidadAlumno: {
            id: 'idCfgCantidadAlumno',
            header: true,
            showaccept: true,
            title: 'Cantidad mínima de alumnos por Curso'
        },
        modalPreciocursociclo: {
            id: 'modalPreciocursociclo',
            header: true,
            title: '',
            okbtn: 'Crear',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true,
            modalsize: 'modal-lg',
        },
        cantidadalumno: {general: null, carrera: null},
        cursoCicloAcademico: '',
        tipoCarpetas: [],
        ciclo: JSON.parse(cicloJson)
    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
        $vue.tipoCarpetas = JSON.parse(tipoCarpetasJson);

    },
    updated() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
    },
    methods: {
        verGuardar() {
            let $vue = this;
            this.guardaPrecio = true;

            bootbox.confirm({
                message: '¿Está seguro que desea guarda este curso de nivelación?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        $vue.guardar();
                    } else {
                        $vue.guardaPrecio = false;
                    }
                }
            });
        },
        guardar() {
            this.guardaPrecio = true;
            let $vue = this;
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/preciocursociclo/save"),
                data: JSON.stringify($vue.$refs.raptorPrecioCursoCiclo.data)
            }).then(response => {
                if (response.success) {
                    $vue.verTabla = false;
                    $vue.guardaPrecio = false;
                    $vue.$refs.raptorPrecioCursoCiclo.loadRemoteData();
                    notify(response.message, "info");
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                $vue.verTabla = true;
                $vue.guardaPrecio = false;
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        configurarCantidadAlumno() {
            let $vue = this;
            $vue.$refs.cfgCantidadAlumnoModal.open();
            $vue.cantidadalumno = {general: null, carrera: null}
        },
        saveCantidadAlumno() {
            let $vue = this;
            if ($("#formcfgCantidadAlumno").parsley().validate() != true) {
                return;
            }
            swal('¿Seguro que desea registrar la cantidad mínima de alumnos requerido por curso?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Aceptar", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                $.ajax({
                    method: 'POST',
                    async: false,
                    url: APP.url('academico/preciocursociclo/configurarcantidad'),
                    contentType: "application/json",
                    data: JSON.stringify($vue.cantidadalumno),
                    success: function (response) {
                        if (response.success) {
                            $vue.$refs.cfgCantidadAlumnoModal.close();
                            $vue.$refs.raptorPrecioCursoCiclo.loadRemoteData();
                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                        } else {
                            return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    },
                    error: function () {
                        return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                });
            }).catch(err => {
                if (err) {
                    swal(APP.errorComunicacion, "error");
                } else {
                    swal.stopLoading();
                    swal.close();
                }
            });
        },
        editarmodal(item) {
            let $vue = this;
            $vue.cursoCicloAcademico = item;
            $vue.modalPreciocursociclo.title = "Configuración Tipo Carpeta : " + item.cicloAcademico.descripcion + " - " + item.curso.codigo;
            $vue.modalPreciocursociclo.okbtn = "Actualizar";
            $vue.$refs.modalPreciocursociclo.open();

            $vue.tipoCarpetasTeorias = [];
            $vue.isLoading = true;
        },
        changeTipoCarpeta(cursoCiclo) {
            this.cursoCicloAcademico = cursoCiclo;
            this.updateModal();
        },
        updateModal() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                async: false,
                url: APP.url('academico/preciocursociclo/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.cursoCicloAcademico),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalPreciocursociclo.close();
                        $vue.$refs.raptorPrecioCursoCiclo.loadRemoteData();
                        return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                    } else {
                        return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                },
                error: function () {
                    return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                }
            });
        }
    }
});
