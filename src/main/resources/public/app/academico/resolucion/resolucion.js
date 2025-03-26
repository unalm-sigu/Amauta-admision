Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#resoluciones',
    data: {
        URL_RESOLUCIONES: APP.url('academico/resolucion/listResoluciones'),
        URL_TRAMITES: APP.url('academico/resolucion/listTramitesToConfirm'),
        colorEstado: {CRE: "default", ACT: "success", ANU: "danger", BLO: "warning", FUS: "warning", DOC_CONF: "success", VB_RES: "primary"},
        resolucionModal: {
            id: 'modalResolucion',
            header: true,
            title: 'Resoluciones',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg',
            showaccept: true
        },
        modalResolucionRetiroCiclo: {
            id: 'modalResolucionRetiroCiclo',
            header: true,
            title: 'Resolucion Retiro Ciclo',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Confirmar Tramite',
            okbtn: 'Aceptar',
            modalsize: 'modal-md',
            showaccept: true
        },
        modalAlumnos: {
            id: 'modalAlumnos',
            header: true,
            title: 'Alumnos',
            okbtn: 'Aceptar',
            showaccept: false
        },
        resolucion: null,
        tiposResoluciones: null,
        files: [],
        ciclosToReincorporacion: null,
        alumnosReincorporacion: [],
        alumnosRetiroCiclo: [],
        alumnosCambioNota: [],
        alumnosCursoDirigido: [],
        alumnoTramiteBachiller: [],
        alumnoTramitePracticas: [],
        alumnoTramiteTraslado: {},
        alumnoTramiteReadmision: [],
        alumnoTramiteCambioPlanCurricular: [],
        alumnoTramiteRenuncia: [],
        tipo: "",
        tipoMap: {
            BACHI: 'Bachiller',
            BACHIFAC: 'Bachiller Facultad',
            TITUL: 'Titulo',
            TITULBAC: 'Titulo Facultad',
            ALUMRENUNCIA: 'Renuncia de Alumno',
            RENUNCIA_CAR: 'Renuncia de Carrera',
            PRACTICAS: 'Prácticas Profesionales',
            TRAS: 'Traslado',
            CAM_NOTA: 'Cambio de Nota',
            CURDIR: 'Curso Dirigido',
            TRAS_INT: 'Traslado Interno',
            RCI: 'Reincorporación',
            REIC: 'Reincorporación',
            INTES: 'Intercambio Estudiantil'
        },
    },
    mounted: function () {
        let $vue = this;

    },
    methods: {
        cambiarEstadoReincorporacion(tramite, estadoDestino, event) {
            event.preventDefault();
            let $vue = this;
            console.log("cambiarEstadoReincorporacion");
            console.dir(tramite);
            $.ajax({
                url: APP.url('academico/tramiteacademico/cambiarEstadoReincorporacion'),
                type: 'POST',
                async: false,
                data: {
                    tramite: tramite.id,
                    estado: "SOL_ACEP"
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(response.message, "error");
                }
            });
        },
        nuevaResolucion(event) {
            let $vue = this;
            event.preventDefault();
            $.ajax({
                url: APP.url('academico/resolucion/loadModalResolucion'),
                type: 'post',
                success: function (response) {
                    if (response.success) {
                        $vue.resolucion = response.data.resolucionJson;
                        $vue.tiposResoluciones = response.data.tiposResolucionesJson;
                        $vue.$refs.modalResolucion.open();
                        console.dir(response.data);
                    } else {
                        notify(Messages.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        editarResolucion: function (resolucion, e) {
            e.preventDefault();
            location.href = APP.url("academico/resolucion/" + resolucion.id + "/editar");
        },
        editarResolucionOther: function (resolucion, e) {
            e.preventDefault();
            location.href = APP.url("academico/resolucion/existentes/" + resolucion.id);
        },
        anularAlumnoEnResolucion: function (resolucion, e) {
            e.preventDefault();
            if (resolucion.isTipoIntercambioEstudiantil) {
                this.anularResolucionIntercambioEstudiantil(resolucion);
            } else {
                location.href = APP.url("academico/resolucion/existentes/" + resolucion.id + "/anularTramite");
            }
        },
        loadModalSubirDoc: function (resolucion, e) {
            e.preventDefault();
            this.resolucion = resolucion;
            this.$refs.modalResolucion.open();
        },
        saveResolucion(event) {
            if (event) {
                event.preventDefault();
            }
            let $vue = this;
            $.ajax({
                url: APP.url('academico/resolucion/saveResolucion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.modalResolucion.close();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            if (newFile && oldFile) {
                // update
                if (newFile.active && !oldFile.active) {
                    // beforeSend
                    // min size
                    if (newFile.size >= 0 && this.minSize > 0 && newFile.size < this.minSize) {
                        this.$refs.upload.update(newFile, {error: 'size'})
                    }
                }
                if (newFile.progress !== oldFile.progress) {

                    // progress
                }
                if (newFile.error && !oldFile.error) {
                }
                if (newFile.success && !oldFile.success) {
                    //  $vue.producto.productoImagen.splice(0, 0, newFile.response.data)
                }
            }
            if (!newFile && oldFile) {
                if (oldFile.success && oldFile.response.id) {
                }
            }
            // Automatically activate upload
            if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                if (!this.$refs.upload.active) {
                    //console.log('subiendo')
                    this.$refs.upload.active = true
                } else {
                    //console.log("FIN?")
                }
            }

            if ($vue.$refs.upload.uploaded) {
                if ($vue.files.length > 0) {
                    //  $vue.reloadProducto();x
                    $vue.resolucion.rutaUrl = $vue.files[0].response.data;
                }
                if ($vue.$refs.upload.clear()) {
                    //   console.log("reiniciar img 2")
                }
            }

            if (newFile && oldFile && !newFile.active && oldFile.active) {
                // Get response data
                if (newFile.xhr) {
                    //  Get the response status code
                    if (newFile.xhr.status == 200) {
                        notify(newFile.response.message, "info");
                        $vue.$refs.tblResoluciones.loadRemoteData();
                    } else {
                        notify(newFile.response.message, "error");
                    }
                    $vue.$refs.modalResolucion.close();
                    MODAL.hideWait();
                } else {
                    notify(response.message, "error");
                }
            }
        },
        inputFilter(newFile, oldFile, prevent) {
            if (newFile && !oldFile) {
                if (!/\.(gif|jpg|jpeg|png|pdf)$/i.test(newFile.name)) {
                    swal(
                            'Oops...',
                            'Este archivo no esta permitido!',
                            'error'
                            )
                    return prevent();
                }
            }
            if (newFile && (!oldFile || newFile.file !== oldFile.file)) {
                newFile.url = ''
                let URL = window.URL || window.webkitURL
                if (URL && URL.createObjectURL) {
                    newFile.url = URL.createObjectURL(newFile.file)
                }
            }
        },
        changeFile(value) {
            console.log("changeFile");
            console.dir(this.files);
        },
        getEstadoClass: function (estadoCode) {
            return "label-" + this.colorEstado[estadoCode];
        },
        loadModalConfirmar(resolucion, event) {
            event.preventDefault();
            let $vue = this;
            $.ajax({
                url: APP.url('academico/resolucion/loadModalResolucion'),
                type: 'post',
                data: {resolucion: resolucion.id},
                success: function (response) {
                    if (response.success) {
                        $vue.resolucion = response.data.resolucion;
                        $vue.ciclosToReincorporacion = response.data.ciclosToReincorporacion;
                        $vue.$refs.tblTramites.ajaxdata = {resolucion: resolucion.id};
                        $vue.$refs.tblTramites.loadRemoteData();
                        $vue.$refs.modalConfirmar.open();
                    } else {
                        notify(Messages.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        saveConfirmarSubirDocumento(event) {
            let $vue = this;
            if (event) {
                event.preventDefault();
            }
            var form = $("[id='frmConfirmar']");
            APP.validateMultiSelect(form);

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            //  $vue.resolucion.estadoEnum = null;
            delete $vue.resolucion.estadoEnum;
            $.ajax({
                url: APP.url('academico/resolucion/saveConfirmar'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.tblResoluciones.loadRemoteData();
                        $vue.$refs.modalConfirmar.close();
                    } else {
                        notify(response.message, "error");
                        $vue.$refs.modalConfirmar.close();
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        customLabelCiclosRei( { descripcion, tipo}) {
            if (descripcion != '' && tipo != '') {
                return `${descripcion} - ${tipo}`;
        }
        },
        allAlumnos(item) {
            let $vue = this;
            $vue.tipo = item.tipoResolucion.codigo;
            $vue.tipoNombre = item.tipoResolucion.nombre;
            $vue.codigoOficina = item.oficina.codigo;
            console.log(item.tipoResolucion);
            axios_.post(APP.url('academico/resolucion/existentes/alumnos/'), item)
                    .then(({data}) => {

                        if (!data.length) {
                            notify("No contiene información de alumnos", "error");
                            return;
                        }
                        console.log(data)
                        if ($vue.tipo == "REIC") {
                            $vue.alumnosReincorporacion = data;
                        } else if ($vue.tipo == "RCI") {
                            $vue.alumnosRetiroCiclo = data;
                        } else if ($vue.tipo == "CAM_NOTA") {
                            $vue.alumnosCambioNota = data;
                        } else if ($vue.tipo == "CURDIR") {
                            $vue.alumnosCursoDirigido = data;
                        } else if ($vue.tipo == "TRAS" || $vue.tipo == "ING_HIS" || $vue.tipo == "INTES") {
                            $vue.alumnoTramiteTraslado = data[0]; ///retorn solo 1 registro
                        } else if ($vue.tipo == "BACHI") {
                            $vue.alumnoTramiteBachiller = data;
                        } else if ($vue.tipo == "BACHIFAC") {
                            $vue.alumnoTramiteBachiller = data;
                        } else if ($vue.tipo == "TITUL") {
                            $vue.alumnoTramiteBachiller = data;
                        } else if ($vue.tipo == "TITULBAC") {
                            $vue.alumnoTramiteBachiller = data;
                        } else if ($vue.tipo == "PRACTICAS") {
                            $vue.alumnoTramitePracticas = data;
                        } else if ($vue.tipo == "TRAS_INT") {
                            $vue.alumnoTramiteTraslado = data;
                        } else if ($vue.tipo == "ALUMRENUNCIA") {
                            $vue.alumnoTramiteRenuncia = data;
                        } else if ($vue.tipo == "RENUNCIA_CAR") {
                            $vue.alumnoTramiteRenuncia = data;
                        } else if ($vue.tipo == "READMISION") {
                            $vue.alumnoTramiteReadmision = data;
                        } else if ($vue.tipo == "CAMBIO_PLAN_CURRICULAR") {
                            $vue.alumnoTramiteCambioPlanCurricular = data;
                        }

                        $vue.$refs.modalAlumnos.open();

                    }, () => {
                    });

        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        anularResolucionIntercambioEstudiantil(resolucion) {

            let $vue = this;

            bootbox.confirm({
                message: "¿Está seguro que desea anular la resolución?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        axios_.post(APP.url('academico/resolucion/anularResolucionIntercambioEstudiantil/'), resolucion)
                                .then(response => {
                                    if (response.data.success) {
                                        notify(response.data.message, "info");
                                        $vue.$refs.tblResoluciones.loadRemoteData();
                                        MODAL.hideWait();
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                });
                    }
                }
            });
        }
    }
})