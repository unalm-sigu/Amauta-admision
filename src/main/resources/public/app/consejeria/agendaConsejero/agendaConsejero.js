Vue.component("multiselect", window.VueMultiselect.default)
Vue.component('date-picker', VueBootstrapDatetimePicker);
new Vue({
    el: '#agendaConsejeroVUE',
    data: {
        ciclo: JSON.parse(cicloJson),
        horas: JSON.parse(jHora),
        consejeros: JSON.parse(jConsejeros),
        agendaConsejeroURL: APP.url(rutaModulo + '/list'),
        consejeroSelect: null,
        agendaModal: {
            id: 'agendaModal',
            header: true,
            title: "Agenda Consejero",
            okbtn: 'Guardar',
            modalsize: "modal-xl",
            showaccept: true
        },
        asistenciaModal: {
            id: 'asistenciaModal',
            header: true,
            title: "Asistencia",
            okbtn: 'Guardar',
            modalsize: "modal-md",
            showaccept: true
        },
        noAsistenciaModal: {
            id: 'noAsistenciaModal',
            header: true,
            title: "Inasistencia",
            okbtn: 'Guardar',
            modalsize: "modal-md",
            showaccept: true
        },
        agendaConsejero: {},
        alumnosConsejeros: [],
        alumnosConsejerosTemp: [],
        reunionAlumnoConsejerosTemp: [],
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        },
        reunionConsejero: {},
        horasInicio: [],
        horasFin: [],
        selectAll: false
    },
    mounted: function () {
        let $vue = this;
        $vue.consejeroSelect = $vue.consejeros[0];
        if ($vue.consejeroSelect.id != undefined) {
            $vue.loadAgendasURL();
            $vue.cargaAconsejados($vue.consejeroSelect);
        }
        $('[data-toggle="tooltip"]').tooltip();
    },
    watch: {
        selectAll: function (val) {
            let $vue = this;
            for (var i = 0; i < $vue.alumnosConsejerosTemp.length; i++) {
                $vue.alumnosConsejerosTemp[i].seleccionado = val;
            }
            $vue.horasInicio = [];
            $vue.horasFin = [];
            if(val == false) {
                $vue.agendaConsejero.hora = {};
            }
        }
    },
    methods: {
        customLabel(item) {
            if (item.carrera.id == null) {
                return;
            }
            return item.carrera.nombre;
        },
        loadAgendasURL() {
            let $vue = this;
            $vue.$refs.load.url = APP.url(rutaModulo + '/list/' + $vue.consejeroSelect.carrera.id);
            $vue.$refs.load.loadRemoteData();
        },
        asistio() {
            let $vue = this;
            var valid = $('#formAsistio').parsley().validate();
            //const {fechaAsistencia} = $vue.reunionConsejero;
            //const formatFechaAsistencia = moment(fechaAsistencia).format("yyyy-MM-ddTHH:mm");
            //const formatFechaAsistencia = moment(fechaAsistencia).format('MM/DD/YYYY hh:mm:ss a');
            //console.log(formatFechaAsistencia);
            //$vue.reunionConsejero[fechaAsistencia] = formatFechaAsistencia;
            if (valid != true) {
                notify("Ingrese los datos obligatorios.", "error");
                return;
            }
            $.ajax({
                url: APP.url(rutaModulo + "/asistenciaReunion"),
                contentType: "application/json",
                data: JSON.stringify($vue.reunionConsejero),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    $vue.$refs.asistenciaModal.close();
                    $vue.init();
                    notify(response.message, "success");
                }
            }, error => {
                console.log(error);
            });
        },
        noAsistio() {
            let $vue = this;
            var valid = $('#formNoAsistio').parsley().validate();
            if (valid != true) {
                notify("Ingrese los datos obligatorios.", "error");
                return;
            }
            $.ajax({
                url: APP.url(rutaModulo + "/inasistenciaReunion"),
                contentType: "application/json",
                data: JSON.stringify($vue.reunionConsejero),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.noAsistenciaModal.close();
                    $vue.$refs.load.loadRemoteData();
                    notify(response.message, "success");
                }
            });
        },
        anular(item) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea anular la reunión?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        $.ajax({
                            url: APP.url(rutaModulo + "/anularReunion"),
                            contentType: "application/json",
                            data: JSON.stringify(item),
                            type: 'post',
                        }).then(response => {
                            if (response.success) {
                                $vue.$refs.load.loadRemoteData();
                                notify(response.message, "success");
                            }
                        });
                    }
                }
            });

        },
        anularAgenda(item) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea anular toda la agenda?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        $.ajax({
                            url: APP.url(rutaModulo + "/anularAgenda"),
                            contentType: "application/json",
                            data: JSON.stringify(item),
                            type: 'post',
                        }).then(response => {
                            if (response.success) {
                                $vue.$refs.load.loadRemoteData();
                                notify(response.message, "success");
                            }
                        });
                    }
                }
            });

        },
        styleColor(item) {
            switch (item.name) {
                case "AGEN":
                    return "label label-primary";
                case "ANU" :
                    return "label label-danger";
                case "NASIS" :
                    return "label label-warning";
                case "ASIS" :
                case "VEN" :
                case "ATEN" :
                    return "label label-success";
            }
        },
        openModal() {
            let $vue = this;
            $vue.init();
            $vue.$refs.agendaModal.title = 'Agenda Consejero';
            $vue.$refs.agendaModal.okbtn = 'Guardar';
            $('#formSaveOrUpdate').parsley().reset();
            $vue.horasInicio = [];
            $vue.horasFin = [];
            $vue.$refs.agendaModal.open();
        },
        openUpdateModal(item) {
            let $vue = this;
            $vue.init();
            $vue.obtenerInfo(item);
            $vue.$refs.agendaModal.title = 'Actualizar Agenda';
            $vue.$refs.agendaModal.okbtn = 'Actualizar';
            $vue.$refs.agendaModal.open();
        },
        openAsistenciaModal(item) {
            let $vue = this;
            $vue.reunionConsejero = Object.assign({}, item);
            $('#formAsistio').parsley().reset();
            $vue.$refs.asistenciaModal.open();
        },
        openNoAsistenciaModal(item) {
            let $vue = this;
            $vue.reunionConsejero = Object.assign({}, item);
            $('#formNoAsistio').parsley().reset();
            $vue.$refs.noAsistenciaModal.open();
        },
        obtenerInfo(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/findAgenda/" + item.id),
                contentType: "application/json",
                type: 'get'
            }).then(response => {
                if (response.success) {
                    $vue.agendaConsejero = response.data;
                    $vue.alumnosConsejerosTemp = response.data.alumnoConsejeros;
                }
            });
        },
        seleccionaHora(item) {
            let $vue = this;
            let reunionAlumnoConsejeros = [];
            $vue.agendaConsejero.reunionAlumnoConsejeros = [];
            for (var i = 0; i < $vue.alumnosConsejerosTemp.length; i++) {
                $vue.horasInicio[i] = item.descripcion;
                $vue.horasFin[i] = item.descripcionFin;
                let data = {
                    alumnoConsejero: $vue.alumnosConsejerosTemp[i],
                    horaInicio: $vue.horasInicio[i],
                    horaFin: $vue.horasFin[i]
                };
                reunionAlumnoConsejeros.push(data);

            }
            $vue.agendaConsejero.reunionAlumnoConsejeros = reunionAlumnoConsejeros;
        },
        save() {
            let $vue = this;
            var valid = $('#formSaveOrUpdate').parsley().validate();
            if (valid != true) {
                notify("Ingrese los datos obligatorios.", "warning");
                return;
            }
// NO ELIMINAR FILTRO FECHA            
//            const formatFecha = /^(0?[1-9]|[12][\d]|3[0-1])[\/](0?[1-9]|1[0-2])[\/](\d{4})$/;
//            if(formatFecha.test($vue.agendaConsejero.fecha)){
//                const dia = $vue.agendaConsejero.fecha.split("/")[0];
//                const mes = $vue.agendaConsejero.fecha.split("/")[1];
//                const anio = $vue.agendaConsejero.fecha.split("/")[2];
//                const fechaActual = moment();
//                //const fechaReunion = moment("2023-01-31 23:59:59");
//                const fechaReunion = moment(`${anio}-${mes}-${dia} 00:00:00`);
//                console.log(fechaReunion.diff(fechaActual, "days"));
//                const diferenciaEnDias = fechaReunion.diff(fechaActual, "days");
//                if(diferenciaEnDias < 0 || isNaN(diferenciaEnDias)) {
//                    //console.log("Invalido", diferenciaEnDias);
//                    notify("Ingrese una fecha válida", "warning");
//                    return;
//                }
//            }

            if($vue.selectAll === false) {
                console.log("$vue.selectAll", $vue.selectAll);
                const reunionAlumnoConsejeros = [];
                $vue.agendaConsejero.reunionAlumnoConsejeros = [];
                for (var i = 0; i < $vue.alumnosConsejerosTemp.length; i++) {
                    if ($vue.alumnosConsejerosTemp[i].seleccionado) {
                        let data = {
                            alumnoConsejero: $vue.alumnosConsejerosTemp[i],
                            horaInicio: $vue.horasInicio[i],
                            horaFin: $vue.horasFin[i]
                        };
                        reunionAlumnoConsejeros.push(data);
                    }
                }
                $vue.agendaConsejero.reunionAlumnoConsejeros = reunionAlumnoConsejeros;
            }

            $vue.agendaConsejero.consejero = $vue.consejeroSelect;

            if($vue.agendaConsejero.reunionAlumnoConsejeros.length == 0) {
                notify("Seleccione alumno(s).", "warning");
                return;
            }
            $vue.agendaConsejero.reunionAlumnoConsejeros.forEach(item => {
                if(item.horaInicio == undefined) {
                    notify( `Alumno ${item.alumnoConsejero.alumno.codigo} no cuenta con hora de inicio.`, "warning");
                    return;
                }
                if(item.horaFin == undefined) {
                    notify( `Alumno ${item.alumnoConsejero.alumno.codigo} no cuenta con hora fin.`, "warning");
                    return;
                }
            });

            $.ajax({
                url: APP.url(rutaModulo + "/save"),
                contentType: "application/json",
                data: JSON.stringify($vue.agendaConsejero),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "success");
                } else {
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "error");
                }
            });
        },
        update() {
            let $vue = this;
            var reunionAlumnoConsejeros = [];
            for (var i = 0; i < $vue.alumnosConsejerosTemp.length; i++) {
                if ($vue.alumnosConsejerosTemp[i].seleccionado) {
                    var data = {
                        alumnoConsejero: $vue.alumnosConsejerosTemp[i]
                    };
                    reunionAlumnoConsejeros.push(data);
                }
            }
            $vue.agendaConsejero.reunionAlumnoConsejeros = reunionAlumnoConsejeros;

            $.ajax({
                url: APP.url(rutaModulo + "/update"),
                contentType: "application/json",
                data: JSON.stringify($vue.agendaConsejero),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "success");
                } else {
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "error");
                }
            });
        },
        cargaAconsejados(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/listAlumnos/" + item.carrera.id),
                contentType: "application/json",
                type: 'get'
            }).then(response => {
                if (response.success) {
                    $vue.alumnosConsejeros = JSON.parse(JSON.stringify(response.data));
                    $vue.alumnosConsejerosTemp = JSON.parse(JSON.stringify(response.data));
                }
            });
        },
        init() {
            let $vue = this;
            $vue.agendaConsejero = {};
            $vue.alumnosConsejerosTemp = JSON.parse(JSON.stringify($vue.alumnosConsejeros));
        },
        reporte() {
            let $vue = this;
            location.href = APP.url('consejeria/agendaconsejero/reporteReuniones/' + $vue.consejeroSelect.carrera.id);
        },
        mostrarMenu(item) {
            const selector = document.body.querySelector('.nav-link');
            let parentNode = selector.parentNode;
            let childNode = selector.childNodes;
            console.log(childNode);
            while (parentNode !== null && parentNode !== document.documentElement) {
                //console.log(selector.classList);
                if (parentNode.classList.contains('collapse')) {
                    parentNode.classList.add('show');
                    const parentNavLink = document.body.querySelector(
                        '[data-bs-target="#' + parentNode.id + '"]'
                    );
                    //console.log(parentNavLink);
                    parentNavLink.classList.remove('collapsed');
                    parentNavLink.classList.add('active');
                }
                parentNode = parentNode.parentNode;
            }
            selector.classList.add('active');
        }
    }
});







        