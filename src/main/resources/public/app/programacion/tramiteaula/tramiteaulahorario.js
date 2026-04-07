Vue.component("aula-horario-component", {
    template: "#aulaHorarioComp",
    props: {
        dias: {type: Array, default: function() { return []; }},
        horas: {type: Array, default: function() { return []; }},
        jsonaulahorario: {default: function() { return []; }},
        validarrangofecha: {type: Boolean, default: false},
    },
    data: function() {
        return {
            horariosSeleccionados: [],
            horasLoaded: false,
            diasActivos: []   // nombres normalizados de los días válidos según la fecha; vacío = todos activos
        };
    },
    watch: {
        jsonaulahorario: {
            immediate: true,
            deep: true,
            handler: function(newVal) {
                let $vue = this;
                try {
                    if (typeof newVal === 'string' && newVal !== '{}' && newVal !== '' && newVal !== '[]') {
                        let parsed = JSON.parse(newVal);
                        $vue.horariosSeleccionados = Array.isArray(parsed) ? parsed : [];
                    } else if (Array.isArray(newVal)) {
                        $vue.horariosSeleccionados = newVal;
                    } else {
                        $vue.horariosSeleccionados = [];
                    }

                    if ($vue.horasLoaded) {
                        $vue.marcarHorariosSeleccionados();
                    }
                } catch (e) {
                    $vue.horariosSeleccionados = [];
                }
            }
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.loadComponent($vue);

        $global.$on("clearhorario", function () {
            $vue.clearhorario();
        });

        $global.$on("get-horarios-seleccionados", function (callback) {
            if (typeof callback === 'function') {
                callback($vue.horariosSeleccionados);
            }
        });

        $global.$on("set-dias-activos", function (dias) {
            $vue.diasActivos = dias || [];
            // Deseleccionar horas que ya no corresponden al día activo
            if ($vue.diasActivos.length > 0) {
                $vue.horas.forEach(function(hora) {
                    if (hora.dias) {
                        hora.dias.forEach(function(dia) {
                            if (!$vue.isDiaActivo(dia) && dia.selecionado) {
                                dia.selecionado = false;
                            }
                        });
                    }
                });
                $vue.horariosSeleccionados = $vue.horariosSeleccionados.filter(function(h) {
                    let dia = $vue.dias.find(function(d) { return d.id === h.dia; });
                    return dia ? $vue.isDiaActivo(dia) : false;
                });
            }
        });
    },
    methods: {
        loadComponent($vue) {
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/aula/loadHorario'),
                success: function (response) {
                    if (response.success) {
                        $vue.dias = response.data.dias;
                        $vue.horas = response.data.horas;
                        $vue.horasLoaded = true;

                        $vue.$nextTick(function() {
                            $vue.marcarHorariosSeleccionados();
                        });
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        marcarHorariosSeleccionados() {
            let $vue = this;

            if (!$vue.horas || !$vue.horariosSeleccionados || !Array.isArray($vue.horariosSeleccionados)) {
                return;
            }

            // Limpiar selecciones previas
            $vue.horas.forEach(function(hora) {
                if (hora.dias) {
                    hora.dias.forEach(function(dia) {
                        dia.selecionado = false;
                    });
                }
            });

            $vue.horariosSeleccionados.forEach(function(horario) {
                let hora = $vue.horas.find(h => h.id === horario.hora);
                if (hora && hora.dias) {
                    let dia = hora.dias.find(d => d.id === horario.dia);
                    if (dia) {
                        dia.selecionado = true;
                    }
                }
            });
        },
        normalizarNombre(str) {
            return str.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
        },
        isDiaActivo(dia) {
            if (!this.diasActivos || this.diasActivos.length === 0) return true;
            let nombre = this.normalizarNombre(dia.nombre);
            return this.diasActivos.indexOf(nombre) !== -1;
        },
        selectHora(dia, hora) {
            let $vue = this;

            if (!$vue.isDiaActivo(dia)) return;

            if (dia.selecionado) {
                dia.selecionado = false;
                $vue.horariosSeleccionados = $vue.horariosSeleccionados.filter(function(h) {
                    return !(h.dia === dia.id && h.hora === hora.id);
                });
            } else {
                dia.selecionado = true;
                $vue.horariosSeleccionados.push({
                    dia: dia.id,
                    hora: hora.id
                });
            }

            $global.$emit('changehorario', $vue.horariosSeleccionados);
        },
        clearhorario() {
            let $vue = this;
            $vue.horas.forEach(function(hora) {
                if (hora.dias) {
                    hora.dias.forEach(function(dia) {
                        dia.selecionado = false;
                    });
                }
            });
            $vue.horariosSeleccionados = [];
        }
    }
});

