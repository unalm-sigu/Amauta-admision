Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#main',
    data: {
        resolucion: resolucionJson != null ? JSON.parse(resolucionJson) : {reincorporaciones: [], retiroCiclo: [], cambioNota: [], cursoDirigido: [], tramiteTraslado: [], cambioNotaMasBajas: [], tramiteBachiller: [], tramiteTitulos: [], tramitePracticasPreProfesionales: []},
        oficinas: JSON.parse(oficinasJson),
        ciclos: JSON.parse(ciclosJson),
        tiposResolucion: JSON.parse(tiposResolucionJson),
        carreras: JSON.parse(carrerasJson),
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
        alumnos: [],
        cursos: [],
        docentes: [],
        alumnoCicloCursoBeans: [],
        isReincorporacion: false,
        isRetiroCiclo: false,
        isCambioNota: false,
        isCursoDirigido: false,
        isTraslado: false,
        isTrasladoInt: false,
        isNotaBaja: false,
        isBachiller: false,
        isTitulo: false,
        isPracticas: false,
        filterFacultad: null,
        modalError: {
            id: 'modalError',
            header: true,
            title: 'Detalle Error',
            okbtn: "Guardar",
            showaccept: false,
            confirm: false
        },
        errores: [],
        isEdicion: false,
        visualizarSelect: false
    }, created: function () {

    }, mounted: function () {
        let $vue = this;
        $(".numerico").numeric({negative: false});

        if ($vue.resolucion.id != null) {
            $vue.isEdicion = true;
            if ($vue.resolucion.isTipoRetiroCiclo) {
                $vue.isRetiroCiclo = true;
            } else if ($vue.resolucion.isTipoReincorporacion) {
                $vue.isReincorporacion = true;
            } else if ($vue.resolucion.isTipoCambioNota) {
                $vue.isCambioNota = true;
            } else if ($vue.resolucion.isTipoCursoDirigido) {
                $vue.isCursoDirigido = true;
            } else if ($vue.resolucion.isTipoTrasladoExterno || $vue.resolucion.isTipoIntercambioEstudiantil || $vue.resolucion.isTipoIngresoFisicoHistorial) {
                $vue.isTraslado = true;
            } else if ($vue.resolucion.isTipoTrasladoInterno) {
                $vue.isTrasladoInt = true;
            } else if ($vue.resolucion.isTipoTramiteBachiller) {
                $vue.isBachiller = true;
            } else if ($vue.resolucion.isTipoTramiteTitulo) {
                $vue.isTitulo = true;
            } else if ($vue.resolucion.isTipoTramitePracticas) {
                $vue.isPracticas = true;
            }
        }
        console.log($vue.resolucion);

    }, methods: {
        tipoResolucionSelect(item) {
            let $vue = this;
            $vue.isRetiroCiclo = false;
            $vue.isReincorporacion = false;
            $vue.isCambioNota = false;
            $vue.isCursoDirigido = false;
            $vue.isTraslado = false;
            $vue.isTrasladoInt = false;
            $vue.isNotaBaja = false;
            $vue.isBachiller = false;
            $vue.isTitulo = false;
            $vue.isPracticas = false;
            if (item.codigo == "RCI") {
                $vue.allRetiroCiclo();
                $vue.isRetiroCiclo = true;
            } else if (item.codigo == "ANCI") {
                $vue.isRetiroCiclo = true;
            } else if (item.codigo == "REIC") {
                $vue.isReincorporacion = true;
                $vue.allReincorporacion();
            } else if (item.codigo == "CAM_NOTA") {
                $vue.isCambioNota = true;
            } else if (item.codigo == "TRAS" || item.codigo == "INTES" || item.codigo == "ING_HIS") {
                $vue.isTraslado = true;
            } else if (item.codigo == "TRAS_INT") {
                $vue.isTrasladoInt = true;
            } else if (item.codigo == "NOTA_BAJA") {
                $vue.isNotaBaja = true;
            } else if (item.codigo == "BACHI") {
                $vue.isBachiller = true;
                $vue.allBachiller();
            } else if (item.codigo == "TITUL") {
                $vue.isTitulo = true;
                $vue.allTitulos();
            } else if (item.codigo == "PRACTICAS") {
                $vue.isPracticas = true;
            } else {
                $vue.isCursoDirigido = true;
            }
        },
        allPracticas() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");

            $.ajax({
                url: APP.url("academico/resolucion/allPracticas"),
                dataType: "json",
                contentType: "application/json"
            }).then(response => {
                if (response.success) {
                    $vue.resolucion.tramitePracticasPreProfesionales = response.data;
                    MODAL.hideWait();
                }
            });
        },
        allTitulos() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url("academico/resolucion/allTitulo"),
                dataType: "json",
                contentType: "application/json"
            }).then(response => {
                if (response.success) {
                    $vue.resolucion.tramiteTitulos = response.data;
                    MODAL.hideWait();
                }
            });

        },
        allRetiroCiclo() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url("academico/resolucion/allRetiroCiclo"),
                dataType: "json",
                contentType: "application/json"
            }).then(response => {
                if (response.success) {
                    $vue.resolucion.retiroCiclo = response.data;
                    MODAL.hideWait();
                }
            });

        },
        allReincorporacion() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url("academico/resolucion/allReincorporacion"),
                dataType: "json",
                contentType: "application/json"
            }).then(response => {
                if (response.success) {
                    $vue.resolucion.reincorporaciones = response.data;
                    MODAL.hideWait();
                }
            });

        },
        allBachiller() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url("academico/resolucion/allBachiller"),
                dataType: "json",
                contentType: "application/json"
            }).then(response => {
                if (response.success) {
                    $vue.resolucion.tramiteBachiller = response.data;
                    MODAL.hideWait();
                }
            });
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.apellidosNombres;
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

                if ($vue.isTrasladoInt || $vue.isTraslado) {
                    $.ajax({
                        url: APP.url("academico/resolucion/findAlumno"),
                        dataType: 'json',
                        type: 'post',
                        data: {nombre: nombre}
                    }).then(response => {
                        if (response.success) {
                            $vue.alumnos = response.data;
                        }

                        this.isLoading = false;
                    })
                } else {

                    $.ajax({
                        url: APP.url("academico/resolucion/findAlumno"),
                        dataType: 'json',
                        type: 'post',
                        data: {nombre: nombre, instanciaOficina: $vue.resolucion.oficina.id}
                    }).then(response => {
                        if (response.success) {
                            $vue.alumnos = response.data;
                        }

                        this.isLoading = false;
                    })
                }

            }
        },
        cicloCambioNota(alumno, resolucion) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/tramitecondicional/allCursosAlumnoByName"),
                dataType: 'json',
                type: 'post',
                data: {idAlumno: alumno.id, idCiclo: resolucion.cicloAplica.id}
            }).then(response => {
                if (response.success) {
                    $vue.cursos = response.data;
                }

                this.isLoading = false;
            })

        },
        allAlumnoCiclo(item, tramiteNotabaja) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/resolucion/allCiclosRepetido/" + item.id),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                if (response.success) {
                    tramiteNotabaja.alumnoCicloCursoBeans = response.data;
                }

                this.isLoading = false;
            })

        },
        addResolucion() {
            let $vue = this;
            if ($vue.isReincorporacion) {
                var reincorporacion = {seleccionado: true};
                $vue.resolucion.reincorporaciones.push(reincorporacion);
            } else if ($vue.isRetiroCiclo) {
                var retiroCiclo = {seleccionado: true};
                $vue.resolucion.retiroCiclo.push(retiroCiclo);
            } else if ($vue.isCambioNota) {
                var cambioNota = {};
                $vue.resolucion.cambioNota.push(cambioNota);
            } else if ($vue.isCursoDirigido) {
                var cursoDirigido = {seleccionado: true};
                $vue.resolucion.cursoDirigido.push(cursoDirigido);
            } else if ($vue.isTraslado || $vue.isTrasladoInt) {
                var traslado = {seleccionado: true};
                $vue.resolucion.tramiteTraslado.push(traslado);
            } else if ($vue.isNotaBaja) {
                var notaBaja = {alumnoCicloCursoBeans: []};
                $vue.resolucion.cambioNotaMasBajas.push(notaBaja);
            } else if ($vue.isBachiller) {
                var tramiteBachiller = {seleccionado: true};
                $vue.resolucion.tramiteBachiller.push(tramiteBachiller);
            } else if ($vue.isTitulo) {
                var tramiteTitulo = {seleccionado: true};
                $vue.resolucion.tramiteTitulos.push(tramiteTitulo);
            } else if ($vue.isPracticas) {
                var tramitePracticas = {};
                $vue.resolucion.tramitePracticasPreProfesionales.push(tramitePracticas);
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
            } else if ($vue.isCursoDirigido) {
                $vue.resolucion.cursoDirigido.splice(index, 1);
            } else if ($vue.isTraslado || $vue.isTrasladoInt) {
                $vue.resolucion.tramiteTraslado.splice(index, 1);
            } else if ($vue.isNotaBaja) {
                $vue.resolucion.tramiteTraslado.splice(index, 1);
            } else if ($vue.isBachiller) {
                $vue.resolucion.tramiteBachiller.splice(index, 1);
            } else if ($vue.isTitulo) {
                $vue.resolucion.tramiteTitulos.splice(index, 1);
            } else if ($vue.isPracticas) {
                $vue.resolucion.tramitePracticasPreProfesionales.splice(index, 1);
            }
        },
        validFilter(ofi, item) {
            let $vue = this;
            if (!$vue.visualizarSelect && (ofi != null && ofi.instanciaOficina != item.alumno.carrera.facultad.id)) {
                return false;
            } else if ($vue.visualizarSelect && !item.seleccionado) {
                return false;
            }
            return true;
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
            $vue.errores = [];

            $.ajax({
                url: APP.url('academico/resolucion/save'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success && response.data.length == 0) {
                        notify(response.message, 'info');
                        $vue.resolucion = {reincorporaciones: [], retiroCiclo: [], cambioNota: [], cursoDirigido: [], tramiteTraslado: [], cambioNotaMasBajas: [], tramiteBachiller: [], tramiteTitulos: [], tramitePracticasPreProfesionales: []};
                        $vue.alumnos = [];
                    } else {
                        if (response.data != null && response.data.length > 0) {
                            $vue.errores = response.data;
                            $vue.$refs.modalError.open();
//                            $vue.resolucion = {reincorporaciones: [], retiroCiclo: [], cambioNota: [], cursoDirigido: [], tramiteTraslado: []};
//                            $vue.alumnos = [];
                            notify("Algunos alumnos no pudieron ser matriculados.", 'error');
                        } else {
                            notify(response.message, 'error');
                        }
                    }
                    MODAL.hideWait();
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        updateResolucion() {
            let $vue = this;
            var valid = $('#form').parsley().validate();

            if (!valid) {
                return;
            }

            MODAL.showWait("Espere un momento por favor");
            $vue.errores = [];


            bootbox.confirm({
                message: '¿Seguro que desea actualizar la resolución? ',
                buttons: {
                    confirm: {label: 'Sí, aceptar', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/resolucion/resolucionExistente/update'),
                            dataType: "json",
                            contentType: "application/json",
                            type: 'POST',
                            data: JSON.stringify($vue.resolucion),
                            success: function (response) {
                                if (response.success && response.data.length == 0) {
                                    notify(response.message, 'info');
                                    location.href = APP.url('academico/resolucion');

                                } else {
                                    if (response.data != null && response.data.length > 0) {
                                        $vue.errores = response.data;
                                        $vue.$refs.modalError.open();
//                            $vue.resolucion = {reincorporaciones: [], retiroCiclo: [], cambioNota: [], cursoDirigido: [], tramiteTraslado: []};
//                            $vue.alumnos = [];
                                        notify("Algunos alumnos no pudieron ser matriculados.", 'error');
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }
                                MODAL.hideWait();
                            },
                            error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });

                    }
                }
            });


        },
        findDocente(nombre) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/findDocente'),
                data: {nombre: nombre},
                success: function (response) {
                    if (response.success) {
                        $vue.docentes = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function (response) {
                    notify(response.message, "error");
                }
            });
        },
        customLabelDocente( { persona }){
            return `${persona.nombreCompleto} `;
        },
        validColumCreditos(item) {
            if (item.oficina != null && item.oficina.codigo == "F040") {
                return true;
            }
            return false;
        }
    }
})