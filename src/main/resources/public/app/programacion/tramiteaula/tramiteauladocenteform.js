Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker);

new Vue({
    el: '#mainFormDocente',
    data: {
        reserva: {
            id: null,
            tramite: {
                tipoSolicitante: 'DOC',
                docente: {},
                serie: null,
                numero: null
            },
            fechaInicio: null,
            fechaFin: null,
            motivo: '',
            comentario: '',
            estado: 'PEND',
            visibleHorario: true
        },
        docente: {},
        docenteNombre: '',
        aulasSeleccionadas: [],
        rangofecha: false,
        soloDisponibles: false,
        capacidadMinima: '',
        guardando: false,
        esEdicion: false,
        jsonaulahorario: '{}',
        urlFiltroAulas: APP.url('tramite/aula/filtroAulas?soloDisponibles=false&soloOera=true'),
        cursosDocente: [],
        cursoSeleccionado: null,
        verificando: false,
        resultadoCruce: null
    },
    mounted: function() {
        let $vue = this;

        if (typeof docenteJson !== 'undefined' && docenteJson) {
            $vue.docente = docenteJson;
            $vue.reserva.tramite.docente = docenteJson;

            if (docenteJson.persona) {
                $vue.docenteNombre = docenteJson.persona.nombres + ' ' +
                                     docenteJson.persona.paterno + ' ' +
                                     docenteJson.persona.materno;
            }
        }

        if (typeof reservaJson !== 'undefined' && reservaJson) {
            $vue.esEdicion = true;
            $vue.reserva = reservaJson;

            if (reservaJson.fechaInicio) {
                $vue.reserva.fechaInicio = moment(reservaJson.fechaInicio, 'DD/MM/YYYY').format('DD/MM/YYYY');
            }

            if (reservaJson.fechaFin) {
                $vue.reserva.fechaFin = moment(reservaJson.fechaFin, 'DD/MM/YYYY').format('DD/MM/YYYY');
                $vue.rangofecha = true;
            }

            if (reservaJson.reservados && reservaJson.reservados.length > 0) {
                $vue.aulasSeleccionadas = reservaJson.reservados;
            }

            if (reservaJson.aulaReservada && reservaJson.aulaReservada.length > 0) {
                let horarios = reservaJson.aulaReservada.map(function(ar) {
                    return {
                        dia: ar.dia ? ar.dia.id : null,
                        hora: ar.hora ? ar.hora.id : null
                    };
                });

                $vue.jsonaulahorario = JSON.stringify(horarios);
            }
        }

        if (typeof $global !== 'undefined') {
            $global.$on('changehorario', function() {
                if ($vue.soloDisponibles) {
                    $vue.filtrarAulas();
                }
                $vue.resultadoCruce = null;
            });
        }

        // Cargar cursos del docente
        $.ajax({
            url: APP.url('tramite/aula/cursosByDocente'),
            type: 'GET',
            success: function(response) {
                if (response.success) {
                    $vue.cursosDocente = response.data || [];
                }
            }
        });
    },
    methods: {
        changeRangoFecha() {
            let $vue = this;
            if (!$vue.rangofecha) {
                $vue.reserva.fechaFin = null;
            }
            $vue.actualizarDiasActivos();
        },

        changeFechaInicio() {
            let $vue = this;
            if ($vue.rangofecha && $vue.reserva.fechaInicio && $vue.reserva.fechaFin) {
                let inicio = moment($vue.reserva.fechaInicio, 'DD/MM/YYYY');
                let fin = moment($vue.reserva.fechaFin, 'DD/MM/YYYY');

                if (inicio.isAfter(fin)) {
                    notify('La fecha de inicio no puede ser posterior a la fecha fin', 'warning');
                    $vue.reserva.fechaInicio = null;
                    $vue.actualizarDiasActivos();
                    return;
                }
            }

            $vue.actualizarDiasActivos();

            if ($vue.soloDisponibles) {
                $vue.$nextTick(function() {
                    $vue.filtrarAulas();
                });
            }
        },

        changeFechaFin() {
            let $vue = this;
            if ($vue.reserva.fechaInicio && $vue.reserva.fechaFin) {
                let inicio = moment($vue.reserva.fechaInicio, 'DD/MM/YYYY');
                let fin = moment($vue.reserva.fechaFin, 'DD/MM/YYYY');

                if (fin.isBefore(inicio)) {
                    notify('La fecha fin no puede ser anterior a la fecha de inicio', 'warning');
                    $vue.reserva.fechaFin = null;
                    $vue.actualizarDiasActivos();
                    return;
                }
            }

            $vue.actualizarDiasActivos();

            if ($vue.soloDisponibles) {
                $vue.$nextTick(function() {
                    $vue.filtrarAulas();
                });
            }
        },

        actualizarDiasActivos() {
            let $vue = this;

            if (!$vue.reserva.fechaInicio) {
                $global.$emit('set-dias-activos', []);
                return;
            }

            // Mapeo isoWeekday (1=Lun ... 7=Dom) a nombre español normalizado
            let isoANombre = {
                1: 'lunes', 2: 'martes', 3: 'miercoles',
                4: 'jueves', 5: 'viernes', 6: 'sabado', 7: 'domingo'
            };

            let inicio = moment($vue.reserva.fechaInicio, 'DD/MM/YYYY');
            let fin = ($vue.rangofecha && $vue.reserva.fechaFin)
                ? moment($vue.reserva.fechaFin, 'DD/MM/YYYY')
                : inicio.clone();

            // Si el rango cubre 7+ días, todos los días son válidos
            if (fin.diff(inicio, 'days') >= 6) {
                $global.$emit('set-dias-activos', []);
                return;
            }

            let diasSet = {};
            let current = inicio.clone();
            while (current.isSameOrBefore(fin, 'day')) {
                diasSet[isoANombre[current.isoWeekday()]] = true;
                current.add(1, 'days');
            }

            $global.$emit('set-dias-activos', Object.keys(diasSet));
        },

        agregarAula(aula) {
            let $vue = this;

            if ($vue.aulaYaAgregada(aula)) {
                notify('El aula ya fue agregada', 'warning');
                return;
            }

            $vue.aulasSeleccionadas.push({
                id: aula.id,
                codigo: aula.codigo,
                nombre: aula.nombre,
                capacidadAula: aula.capacidadAula
            });

            notify('Aula agregada correctamente', 'success');
        },

        eliminarAula(aula) {
            let $vue = this;

            let index = $vue.aulasSeleccionadas.findIndex(function(a) {
                return a.id === aula.id;
            });

            if (index !== -1) {
                $vue.aulasSeleccionadas.splice(index, 1);
                notify('Aula eliminada', 'info');
            }
        },

        aulaYaAgregada(aula) {
            let $vue = this;
            return $vue.aulasSeleccionadas.some(function(a) {
                return a.id === aula.id;
            });
        },

        filtrarAulas() {
            let $vue = this;


            if (!$vue.soloDisponibles) {

                let params = { soloDisponibles: false, soloOera: true };
                if ($vue.capacidadMinima) {
                    params['queries[capacidadminimaambiente]'] = $vue.capacidadMinima;
                }
                $vue.urlFiltroAulas = APP.url('tramite/aula/filtroAulas') + '?' + $.param(params);

                $vue.$nextTick(function() {
                    if ($vue.$refs.raptor && typeof $vue.$refs.raptor.loadRemoteData === 'function') {
                        $vue.$refs.raptor.loadRemoteData();
                    }
                });
                return;
            }

            if (!$vue.reserva.fechaInicio) {
                notify('Debe seleccionar una fecha para filtrar por disponibilidad', 'warning');
                $vue.soloDisponibles = false;
                return;
            }

            let horarios = [];
            if (typeof $global !== 'undefined') {
                $global.$emit('get-horarios-seleccionados', function(data) {
                    horarios = data;
                });
            }

            if (!horarios || horarios.length === 0) {
                notify('Debe seleccionar al menos un horario (día y hora) para filtrar por disponibilidad', 'warning');
                $vue.soloDisponibles = false;
                return;
            }

            let params = {
                fechaInicio: $vue.reserva.fechaInicio,
                horarios: JSON.stringify(horarios),
                soloDisponibles: true,
                soloOera: true
            };

            if ($vue.rangofecha && $vue.reserva.fechaFin) {
                params.fechaFin = $vue.reserva.fechaFin;
            }

            if ($vue.capacidadMinima) {
                params['queries[capacidadminimaambiente]'] = $vue.capacidadMinima;
            }

            $vue.urlFiltroAulas = APP.url('tramite/aula/filtroAulas') + '?' + $.param(params);

            $vue.$nextTick(function() {
                if ($vue.$refs.raptor) {
                    console.log('Refrescando raptor-table con loadRemoteData');
                    if (typeof $vue.$refs.raptor.loadRemoteData === 'function') {
                        $vue.$refs.raptor.loadRemoteData();
                    } else {
                        console.error('El método loadRemoteData no existe en raptor');
                    }
                } else {
                    console.error('No se encontró la referencia de raptor');
                }
            });
        },

        validarFormulario() {
            let $vue = this;

            if (!$vue.reserva.fechaInicio) {
                notify('Debe seleccionar una fecha de inicio', 'error');
                return false;
            }

            if ($vue.rangofecha && !$vue.reserva.fechaFin) {
                notify('Debe seleccionar una fecha fin', 'error');
                return false;
            }

            let horarios = [];
            if (typeof $global !== 'undefined') {
                $global.$emit('get-horarios-seleccionados', function(data) {
                    horarios = data;
                });
            }

            if (!horarios || horarios.length === 0) {
                notify('Debe seleccionar al menos un horario (día y hora)', 'error');
                return false;
            }

            if (!$vue.aulasSeleccionadas || $vue.aulasSeleccionadas.length === 0) {
                notify('Debe seleccionar al menos un aula', 'error');
                return false;
            }

            if (!$vue.reserva.motivo || $vue.reserva.motivo.trim().length < 10) {
                notify('El motivo debe tener al menos 10 caracteres', 'error');
                return false;
            }

            if ($vue.$refs.formreserva) {
                let parsleyForm = $($vue.$refs.formreserva).parsley();
                if (!parsleyForm.validate()) {
                    notify('Complete todos los campos requeridos correctamente', 'error');
                    return false;
                }
            }

            return true;
        },

        guardar() {
            let $vue = this;

            if (!$vue.validarFormulario()) {
                return;
            }

            let titulo = $vue.esEdicion ? 'Actualizar Reserva' : 'Crear Reserva';
            let mensaje = $vue.esEdicion ?
                '¿Está seguro de actualizar esta solicitud de reserva?' :
                '¿Está seguro de enviar esta solicitud de reserva?\n\nSu solicitud será revisada por el área de Programación de Horarios.';

            swal({
                title: titulo,
                text: mensaje,
                icon: 'info',
                buttons: {
                    cancel: {
                        text: 'Cancelar',
                        value: false,
                        visible: true
                    },
                    confirm: {
                        text: 'Sí, enviar solicitud',
                        value: true
                    }
                }
            }).then(function(confirmado) {
                if (!confirmado) {
                    return;
                }

                $vue.enviarReserva();
            });
        },

        verificarCruce() {
            let $vue = this;

            let horarios = [];
            if (typeof $global !== 'undefined') {
                $global.$emit('get-horarios-seleccionados', function(data) {
                    horarios = data;
                });
            }

            if (!horarios || horarios.length === 0) {
                notify('Debe seleccionar al menos un horario (día y hora) antes de verificar', 'warning');
                return;
            }

            $vue.verificando = true;
            $vue.resultadoCruce = null;

            let dataToSend = {
                curso: { id: $vue.cursoSeleccionado.id },
                diahora: horarios.map(function(h) {
                    return {
                        dia: { id: h.dia },
                        hora: { id: h.hora }
                    };
                })
            };

            $.ajax({
                url: APP.url('tramite/aula/verificarCruceAlumnos'),
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dataToSend),
                success: function(response) {
                    $vue.verificando = false;
                    if (response.success) {
                        $vue.resultadoCruce = response.data || [];
                    } else {
                        notify(response.message || 'Error al verificar cruces', 'error');
                    }
                },
                error: function(xhr) {
                    $vue.verificando = false;
                    let mensaje = 'Error al verificar cruces';
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        mensaje = xhr.responseJSON.message;
                    }
                    notify(mensaje, 'error');
                }
            });
        },

        enviarReserva() {
            let $vue = this;

            $vue.guardando = true;

            let horarios = [];
            if (typeof $global !== 'undefined') {
                $global.$emit('get-horarios-seleccionados', function(data) {
                    horarios = data;
                });
            }

            let dataToSend = {
                id: $vue.reserva.id,
                estado: 'PEND',
                tipoReservaAmbiente: 'Libre',
                tramite: {
                    id: $vue.reserva.tramite.id,
                    tipoSolicitante: 'DOC',
                    docente: {
                        id: $vue.docente.id
                    }
                },
                fechaInicio: $vue.reserva.fechaInicio,
                fechaFin: $vue.rangofecha ? $vue.reserva.fechaFin : $vue.reserva.fechaInicio,
                motivo: $vue.reserva.motivo,
                comentario: $vue.reserva.comentario,
                visibleHorario: $vue.reserva.visibleHorario,
                diahora: horarios.map(function(h) {
                    return {
                        dia: { id: h.dia },
                        hora: { id: h.hora }
                    };
                }),
                reservados: $vue.aulasSeleccionadas.map(function(a) {
                    return { id: a.id };
                })
            };

            $.ajax({
                url: APP.url('tramite/aula/save'),
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dataToSend),
                success: function(response) {
                    $vue.guardando = false;

                    if (response.success) {
                        if (response.data === 'PENDIENTE_REVISION') {
                            swal({
                                title: '¡Solicitud Registrada!',
                                // text: 'Su solicitud de reserva de aula ha sido registrada exitosamente.\n\nSerá enviada al área de Programación de Horarios para su revisión y aprobación.',
                                icon: 'success',
                                button: 'Entendido'
                            }).then(function() {
                                window.location.href = APP.url('tramite/aula/docente');
                            });
                        } else {
                            notify(response.message || 'Reserva guardada correctamente', 'success');
                            setTimeout(function() {
                                window.location.href = APP.url('tramite/aula/docente');
                            }, 1500);
                        }
                    } else {
                        notify(response.message || 'Error al guardar la reserva', 'error');
                    }
                },
                error: function(xhr) {
                    $vue.guardando = false;

                    let mensaje = 'Error al guardar la reserva';
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        mensaje = xhr.responseJSON.message;
                    }

                    notify(mensaje, 'error');
                }
            });
        }
    }
});
