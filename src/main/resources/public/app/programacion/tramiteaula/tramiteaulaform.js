Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker);

new Vue({
    el: '#main',
    data: {
        reservaaula: {tipoSolicitante: null,tipoReservaAmbiente:null, tramite: {alumno: {}, empresa: {}, docente: {}, oficina: {}}},
        reservaaulaedit: JSON.parse(reservaAulaJson),
        tiposSolicitante: JSON.parse(tiposSolicitanteJson),
        tipoReservaAmbiente: JSON.parse(tipoReservaAmbienteJson),
        urlfilter: APP.url("tramite/aula/filteraula"),
        institucion: {pais: {}},
        dataInstitucionModal: {
            id: 'idInstitucionModal',
            header: true,
            title: 'Agregar Institución',
            showaccept: true,
            okbtn: 'Agregar Institución'
        },
        rangofecha: true,
        solofecha: false,
        variosambiente: true,
        soloambiente: false,
        isactiveguardar: false,
        todos: true,
        solodisponible: false,
        reservados: [],
        moduloselecto: {id: null},
        dias: [],
        horas: [],
        jsonaulahorario: [],
        alumnos: [],
        docentes: [],
        empresas: [],
        oficinas: [],
        modulos: [],
        paises: [],
        isSearchingTipos: false,
        isSearchingTipoReservas: false,
        isSearchingAlumnos: false,
        isSearchingDocentes: false,
        isSearchingEmpresas: false,
        isSearchingModulos: false,
        isSearchingPais: false,
        isSearchingOficina: false,
        tiposolicitante: [],
        capacidadseleccinado: 0,
        capacidadfaltante: 0,
        totalaulas: 0,
    },
    mounted: function () {

        let $vue = this;

        if ($vue.reservaaulaedit != null) {
            if ($vue.reservaaulaedit.id != null) {
                $vue.reservaaula = $vue.reservaaulaedit;
                $vue.reservados = $vue.reservaaulaedit.reservados;
                let tipoEnum = $vue.reservaaulaedit.tramite.tipoSolicitanteEnum;
                $vue.reservaaula.tipoSolicitante = {id: tipoEnum.id, nombre: tipoEnum.value}
            }
        }
        $vue.loadModulosByOficina();
        $global.$on("changehorario", function () {
            $vue.changehorario();
        });

        $($vue.$refs.capacidadMaximaAmbiente).numeric({negative: false});
        $($vue.$refs.capacidadMinimaAmbiente).numeric({negative: false});

    },
    updated: function () {
        let $vue = this;

        $($vue.$refs.capacidadMaximaAmbiente).numeric({negative: false});
        $($vue.$refs.capacidadMinimaAmbiente).numeric({negative: false});
    },
    methods: {
        changeSoloFecha() {
            let $vue = this;
            $vue.reservados = [];
            $vue.clearHorario();
            $vue.rangofecha = !$vue.rangofecha;
        },
        changeRangoFecha() {
            let $vue = this;
            $vue.reservados = [];
            $vue.solofecha = !$vue.solofecha;
            $vue.clearHorario();
            $vue.changefilteraula();
        },
        addInstitucion() {
            let $vue = this;
            $vue.institucion = {paisUbicacion: {}};
            $vue.$refs.nuevaInstitucionModal.open();
        },
        saveInstitucionModal() {
            let $vue = this;
            let miform = $($vue.$refs.formInstitucionModal);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }

            $.ajax({
                url: APP.url('tramite/aula/saveInstitucion'),
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.institucion),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.reservaaula.tramite.empresa = response.data;
                        $vue.$refs.nuevaInstitucionModal.close();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        changeSoloAmbiente() {
            let $vue = this;
            $vue.variosambiente = !$vue.variosambiente;
        },
        changeVariosAmbientes() {
            let $vue = this;
            $vue.soloambiente = !$vue.soloambiente;
        },
        changeFechaInicio() {
            let $vue = this;
            if ($vue.reservaaula.fechaFin === undefined) {
                $vue.reservaaula.fechaFin = $vue.reservaaula.fechaInicio;
            }
            $vue.reservados = [];
            $vue.clearHorario();
            $vue.changefilteraula();
        },
        changeFechaFin() {
            let $vue = this;
            $vue.reservados = [];
            $vue.clearHorario();
            $vue.changefilteraula();
        },
        validarDiaSeleccionado(reservaaula) {
            let fechaString = reservaaula.fechaInicio;
            let partes = fechaString.split("/");
            let fecha = new Date(partes[2], partes[1] - 1, partes[0]);
            let diaSemana = fecha.getDay();
            return this.getDiaByIdDateJS(diaSemana);

        },
        getDiaByIdDateJS(dia) {
            let diasSemana = [{id: 1, dia: 'Domingo'}, {id: 2, dia: 'Lunes'}, {id: 3, dia: 'Martes'},
                {id: 4, dia: 'Miércoles'}, {id: 5, dia: 'Jueves'}, {id: 6, dia: 'Viernes'}, {id: 7, dia: 'Sábado'}];
            return diasSemana[dia];
        },
        guardarTramite() {

            let $vue = this;
            let miform = $($vue.$refs.formtramite);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }

            if ($vue.reservaaula.fechaInicio === $vue.reservaaula.fechaFin) {

                let diaDateJs = $vue.jsonaulahorario[0].dia.id + 1;// se sumas +1 por el date del JS
                let diaSeleccionado = this.validarDiaSeleccionado($vue.reservaaula);

                let diaTmp = $vue.jsonaulahorario[0].dia.id;

                for (var i = 0; i < $vue.jsonaulahorario.length; i++) {

                    if (diaTmp !== $vue.jsonaulahorario[i].dia.id) {
                        notify("Solo debe seleccionar el día de la fecha " + $vue.reservaaula.fechaInicio + " día " + diaSeleccionado.dia, "error");
                        return;
                    }
                }

                if (diaDateJs !== diaSeleccionado.id) {
                    notify("El dia seleccionado debe ser igual a la fecha " + $vue.reservaaula.fechaInicio + " día " + diaSeleccionado.dia, "error");
                    return;
                }
            }

            $vue.reservaaula.reservados = $vue.reservados;
            $vue.reservaaula.diahora = $vue.jsonaulahorario;
            $vue.isactiveguardar = true;
            console.log($vue.reservaaula);
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('tramite/aula/save'),
                data: JSON.stringify($vue.reservaaula),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        var urll = APP.url('tramite/aula');
                        $(location).attr('href', urll);
                    } else {
                        notify(response.message, "error");
                        $vue.isactiveguardar = false;
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                    $vue.isactiveguardar = false;
                }
            });
        },
        changeTodos() {

            let $vue = this;
            $vue.solodisponible = !$vue.solodisponible;
        },
        changeSoloDisponible() {

            let $vue = this;
            $vue.todos = !$vue.todos;
            $vue.changefilteraula();
        },
        deleteReservado(reserva) {

            let $vue = this;
            let indx = $vue.reservados.indexOf(reserva);
            $vue.reservados.splice(indx, 1);
            $vue.changefilteraula();
            $vue.changeCapacidadSeleccionado();
        },
        addAula(aula) {
            let $vue = this;
            if ($vue.reservaaula.aforo) {
                if ($vue.capacidadseleccinado) {
                    if (parseInt($vue.capacidadseleccinado) >= parseInt($vue.reservaaula.aforo)) {
                        notify("a sobrepasado su requerimiento", "error");
                        return;
                    }
                }
            }
            $vue.reservados.push(aula);
            $vue.changefilteraula();
            $vue.changeCapacidadSeleccionado();
        },
        changefilteraula() {

            let $vue = this;


            $vue.$refs.raptor.querie.push({name: 'capacidadmaximaambiente', value: $vue.reservaaula.capacidadMaximaAmbiente});
            $vue.$refs.raptor.querie.push({name: 'capacidadminimaambiente', value: $vue.reservaaula.capacidadMinimaAmbiente});

            $vue.$refs.raptor.querie.push({name: 'solodisponible', value: $vue.solodisponible});
            $vue.$refs.raptor.querie.push({name: 'fechainicio', value: $vue.reservaaula.fechaInicio});
            $vue.$refs.raptor.querie.push({name: 'horainicio', value: $vue.reservaaula.horaInicio});
            $vue.$refs.raptor.querie.push({name: 'horafin', value: $vue.reservaaula.horaFin});
            $vue.$refs.raptor.querie.push({name: 'rangofecha', value: $vue.rangofecha});

            $vue.$refs.raptor.querie.push({name: 'fechafin', value: $vue.rangofecha ? $vue.reservaaula.fechaFin : ''});
            $vue.$refs.raptor.querie.push({name: 'modulo', value: ($vue.moduloselecto != null && $vue.moduloselecto.id != null) ? $vue.moduloselecto.id : ''});

            var diahora = $vue.jsonaulahorario.map(function (v, i) {
                return v.id;
            });

            $vue.$refs.raptor.querie.push({name: 'diahora', value: diahora.toString()});

            var aulass = $vue.reservados.map(function (v, i) {
                return v.id;
            });

            $vue.$refs.raptor.querie.push({name: 'aulas', value: $vue.reservados.length > 0 ? aulass.toString() : ''});

            $vue.$refs.raptor.loadRemoteData();
        },
        changehorario() {
            let $vue = this;
            $vue.reservados = [];
            $vue.changefilteraula();
            $vue.changeCapacidadSeleccionado();
        },
        reloadaulalist() {
            let $vue = this;
            var aulass = $vue.reservados.map(function (v, i) {
                return v.id;
            });
            if ($vue.reservados.length > 0) {
                $vue.$refs.raptor.querie.push({name: 'aulas', value: aulass.toString()});
            } else {
                $vue.$refs.raptor.querie.push({name: 'aulas', value: ''});
            }
            $vue.$refs.raptor.loadRemoteData();
        },
        changemodulo() {
            let $vue = this;
            $vue.changefilteraula();
        },
        searchAlumnos(search) {
            let $vue = this;
            $vue.isSearchingAlumnos = true;
            axios_.get("/tramite/aula/allAlumno", {params: {nombre: search}})
                    .then(({data}) => {
                        $vue.alumnos = data;
                        $vue.isSearchingAlumnos = false;
                    }, () => {
                        $vue.isSearchingAlumnos = false;
                    });
        },
        getNombreCompleto(item) {
            if ($.isEmptyObject(item)) {
                return;
            }
            return item.persona.nombreCompleto;
        },
        searchDocentes(search) {
            let $vue = this;
            $vue.isSearchingDocentes = true;
            axios_.get("/tramite/aula/allDocente", {params: {nombre: search}})
                    .then(({data}) => {
                        $vue.docentes = data;
                        $vue.isSearchingDocentes = false;
                    }, () => {
                        $vue.isSearchingDocentes = false;
                    });
        },
        searchEmpresas(search) {
            let $vue = this;
            $vue.isSearchingEmpresas = true;
            axios_.get("/tramite/aula/allEmpresa", {params: {nombre: search}})
                    .then(({data}) => {
                        $vue.empresas = data;
                        $vue.isSearchingEmpresas = false;
                    }, () => {
                        $vue.isSearchingEmpresas = false;
                    });
        },
        searchOficina(search) {
            let $vue = this;
            $vue.isSearchingOficina = true;
            axios_.get("/tramite/aula/allOficina", {params: {nombre: search}})
                    .then(({data}) => {
                        $vue.oficinas = data;
                        $vue.isSearchingOficina = false;
                    }, () => {
                        $vue.isSearchingOficina = false;
                    });
        },
        searchModulos(search) {
            let $vue = this;
            $vue.isSearchingModulos = true;
            $.ajax({
                url: APP.url('tramite/aula/allAulaModulo'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: search},
                success(response) {
                    $vue.isSearchingModulos = false;
                    if (response.success) {
                        $vue.modulos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }, loadModulosByOficina() {
            let $vue = this;
            $vue.isSearchingModulos = true;
            $.ajax({
                url: APP.url('tramite/aula/allAulaModulos'),
                dataType: 'json',
                type: 'POST',
                async: true,
                success(response) {
                    $vue.isSearchingModulos = false;
                    if (response.success) {
                        $vue.modulos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        searchPais(search) {
            let $vue = this;
            $vue.isSearchingPais = true;
            $.ajax({
                url: APP.url('comun/buscar/allPaises'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: search},
                success(response) {
                    $vue.isSearchingPais = false;
                    if (response.success) {
                        $vue.paises = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        changeSolicitante(value) {
            let $vue = this;
            $vue.reservaaula.tramite.tipoSolicitante = value.id;
        },
        changeTipoReserva(value){
            let $vue = this;
            $vue.reservaaula.tramite.tipoReservaAmbiente = value.id;
        },
        changeCapacidadMinima() {
            let $vue = this;
            $vue.changefilteraula();
        },
        changeCapacidadMaxima() {
            let $vue = this;
            $vue.changefilteraula();
        },
        changeCapacidadSeleccionado() {
            let $vue = this;
            let total = 0;
            $vue.reservados.map(function (obj) {
                total += obj.capacidadAula;
            });
            $vue.capacidadseleccinado = total;
            $vue.capacidadfaltante = $vue.reservaaula.aforo - $vue.capacidadseleccinado;
            $vue.totalaulas = $vue.reservados.length;
        },
        validarrangofecha(dia, hora) {
            let $vue = this;
            if ($vue.reservaaula.fechaInicio == undefined) {
                notify("Seleccione la fecha de inicio", 'error')
                return true;
            }
            if ($vue.reservaaula.fechaFin == undefined) {
                if ($vue.rangofecha == true) {
                    notify("Seleccione la fecha de fin", 'error')
                    return true;
                }
            }
            if ($vue.rangofecha) {
                return false;
            }
            var date = moment($vue.reservaaula.fechaInicio, "DD/MM/YYYY");
            var day = date.day();
            if (day == 0) {
                day = 7;
            }

            if (dia.numeroDia != day) {
                return true;
            }
        },
        clearHorario() {
            let $vue = this;
            $vue.changeCapacidadSeleccionado();
            $global.$emit('clearhorario');
        },
        changeTotalAforo() {
            let $vue = this;
            $vue.changeCapacidadSeleccionado();
        }
    }
});
