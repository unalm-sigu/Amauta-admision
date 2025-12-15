Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#reporteAulasLibresVUE',
    data: {
        dias: JSON.parse(diasJson),
        horas: JSON.parse(horasJson),
        aulas: JSON.parse(aulasJson),
        tipoAulas: JSON.parse(tipoAulasJson),
        oficinas: JSON.parse(oficinasJson),
        modulos: JSON.parse(modulosJson),
        oficinaSeleccionada: null,
        tipoAulaSeleccionado: null,
        moduloSeleccionado: null,
        aulaSeleccionada: null,
        diaSeleccionado: null,
        horaSeleccionada: null,
        estadisticas: {
            totalAulas: 0,
            aulasLibres: 0,
            aulasOcupadas: 0,
            porcentajeOcupacion: 0
        },
        aulasFiltradas: [],
        matrizOcupacion: {},
        // Nuevas propiedades para búsqueda y ordenamiento
        busquedaAula: '',
        campoOrden: 'codigo',
        ordenAscendente: true
    },
    computed: {
        mostrarFiltrosOera() {
            return this.oficinaSeleccionada && this.oficinaSeleccionada.codigo === 'OERA' &&
                   this.tipoAulaSeleccionado && this.tipoAulaSeleccionado.codigo === 'AUL';
        },
        modulosFiltrados() {
            // Filtrar módulos que pertenezcan a OERA
            return this.modulos.filter(modulo => {
                let aulasDelModulo = this.aulas.filter(a =>
                    a.aulaSuperior && a.aulaSuperior.id === modulo.id &&
                    a.oficinaSupervisora && a.oficinaSupervisora.codigo === 'OERA'
                );
                return aulasDelModulo.length > 0;
            });
        },
        aulasParaFiltro() {
            if (!this.moduloSeleccionado) {
                // Si no hay módulo seleccionado, mostrar todas las aulas de OERA
                return this.aulas.filter(a =>
                    a.oficinaSupervisora && a.oficinaSupervisora.codigo === 'OERA' &&
                    this.tipoAulaSeleccionado && a.tipoAula && a.tipoAula.codigo === this.tipoAulaSeleccionado.codigo
                );
            }
            // Si hay módulo seleccionado, filtrar aulas de ese módulo
            return this.aulas.filter(a =>
                a.aulaSuperior && a.aulaSuperior.id === this.moduloSeleccionado.id
            );
        },
        aulasDisponibles() {
            // Calcular aulas disponibles para día y hora específicos
            if (!this.diaSeleccionado || !this.horaSeleccionada) {
                return [];
            }

            let key = this.diaSeleccionado.id + '_' + this.horaSeleccionada.id;

            // Filtrar aulas que NO están ocupadas en ese día/hora
            let disponibles = this.aulasFiltradas.filter(aula => {
                return !aula.horariosOcupadosSet.has(key);
            });

            // Ordenar por capacidad (de mayor a menor)
            return disponibles.sort((a, b) => {
                let capA = parseInt(a.capacidadAula) || 0;
                let capB = parseInt(b.capacidadAula) || 0;
                return capB - capA;
            });
        },
        aulasMostradas() {
            let $vue = this;
            let aulas = $vue.aulasFiltradas;

            // Filtrar por búsqueda
            if ($vue.busquedaAula && $vue.busquedaAula.trim() !== '') {
                let busqueda = $vue.busquedaAula.toLowerCase().trim();
                aulas = aulas.filter(aula => {
                    let codigo = (aula.codigo || '').toLowerCase();
                    let nombre = (aula.nombre || '').toLowerCase();
                    return codigo.includes(busqueda) || nombre.includes(busqueda);
                });
            }

            // Ordenar
            if ($vue.campoOrden) {
                aulas = aulas.slice().sort((a, b) => {
                    let valorA, valorB;

                    switch ($vue.campoOrden) {
                        case 'codigo':
                            valorA = (a.codigo || '').toLowerCase();
                            valorB = (b.codigo || '').toLowerCase();
                            break;
                        case 'nombre':
                            valorA = (a.nombre || '').toLowerCase();
                            valorB = (b.nombre || '').toLowerCase();
                            break;
                        case 'tipoAula':
                            valorA = (a.tipoAula && a.tipoAula.nombre || '').toLowerCase();
                            valorB = (b.tipoAula && b.tipoAula.nombre || '').toLowerCase();
                            break;
                        case 'capacidadAula':
                            valorA = parseInt(a.capacidadAula) || 0;
                            valorB = parseInt(b.capacidadAula) || 0;
                            break;
                        case 'horasOcupadas':
                            valorA = parseInt(a.horasOcupadas) || 0;
                            valorB = parseInt(b.horasOcupadas) || 0;
                            break;
                        case 'porcentajeOcupacion':
                            valorA = parseInt(a.porcentajeOcupacion) || 0;
                            valorB = parseInt(b.porcentajeOcupacion) || 0;
                            break;
                        default:
                            return 0;
                    }

                    // Comparar valores
                    let comparacion = 0;
                    if (valorA < valorB) comparacion = -1;
                    if (valorA > valorB) comparacion = 1;

                    // Aplicar dirección de orden
                    return $vue.ordenAscendente ? comparacion : -comparacion;
                });
            }

            return aulas;
        }
    },
    watch: {
        oficinaSeleccionada() {
            // Limpiar filtros de módulo y aula cuando cambia la oficina
            this.moduloSeleccionado = null;
            this.aulaSeleccionada = null;
        },
        tipoAulaSeleccionado() {
            // Limpiar filtros de módulo y aula cuando cambia el tipo de aula
            this.moduloSeleccionado = null;
            this.aulaSeleccionada = null;
        }
    },
    mounted: function () {
        let $vue = this;

        // Establecer OERA como oficina por defecto
        $vue.oficinaSeleccionada = $vue.oficinas.find(o => o.codigo === 'OERA');

        $vue.procesarAulas();
        $vue.filtrarAulas();
    },
    methods: {
        volver() {
            location.href = APP.url(rutaModulo);
        },

        limpiarFiltros() {
            let $vue = this;
            $vue.oficinaSeleccionada = null;
            $vue.tipoAulaSeleccionado = null;
            $vue.moduloSeleccionado = null;
            $vue.aulaSeleccionada = null;
            $vue.diaSeleccionado = null;
            $vue.horaSeleccionada = null;
            $vue.busquedaAula = '';
            $vue.filtrarAulas();
        },

        exportarExcel() {
            let $vue = this;

            // Construir la URL con los parámetros de filtro
            let params = new URLSearchParams();

            if ($vue.oficinaSeleccionada != null) {
                params.append('oficinaId', $vue.oficinaSeleccionada.id);
            }

            if ($vue.tipoAulaSeleccionado != null) {
                params.append('tipoAulaId', $vue.tipoAulaSeleccionado.id);
            }

            if ($vue.mostrarFiltrosOera && $vue.moduloSeleccionado != null) {
                params.append('moduloId', $vue.moduloSeleccionado.id);
            }

            if ($vue.mostrarFiltrosOera && $vue.aulaSeleccionada != null) {
                params.append('aulaId', $vue.aulaSeleccionada.id);
            }

            // Construir URL completa
            let url = APP.url(rutaModulo + '/exportarReporteAulasLibres');
            if (params.toString()) {
                url += '?' + params.toString();
            }

            // Abrir en nueva ventana para descargar
            window.open(url, '_blank');
        },

        ordenarPor(campo) {
            let $vue = this;
            // Si es el mismo campo, cambiar dirección
            if ($vue.campoOrden === campo) {
                $vue.ordenAscendente = !$vue.ordenAscendente;
            } else {
                // Si es un campo nuevo, ordenar ascendente por defecto
                $vue.campoOrden = campo;
                $vue.ordenAscendente = true;
            }
        },

        getIconoOrden(campo) {
            let $vue = this;
            if ($vue.campoOrden !== campo) {
                return 'fa-sort text-muted';
            }
            return $vue.ordenAscendente ? 'fa-sort-asc' : 'fa-sort-desc';
        },

        procesarAulas() {
            let $vue = this;
            let totalHorasPosibles = $vue.dias.length * $vue.horas.length;

            // Procesar cada aula
            for (let aula of $vue.aulas) {
                // Crear un Set de horarios ocupados (dia_hora)
                let horariosOcupados = new Set();
                if (aula.horarios) {
                    for (let horario of aula.horarios) {
                        horariosOcupados.add(horario.diaId + '_' + horario.horaId);
                    }
                }

                // Calcular estadísticas del aula
                aula.horasOcupadas = horariosOcupados.size;
                aula.porcentajeOcupacion = Math.round((aula.horasOcupadas / totalHorasPosibles) * 100);
                aula.horariosOcupadosSet = horariosOcupados;
            }

            $vue.aulasFiltradas = $vue.aulas;
        },

        onModuloChange() {
            let $vue = this;
            // Limpiar selección de aula cuando cambia el módulo
            $vue.aulaSeleccionada = null;
            $vue.filtrarAulas();
        },

        filtrarAulas() {
            let $vue = this;

            $vue.aulasFiltradas = $vue.aulas;

            // Filtrar por oficina
            if ($vue.oficinaSeleccionada != null) {
                $vue.aulasFiltradas = $vue.aulasFiltradas.filter(aula => {
                    return aula.oficinaSupervisora && aula.oficinaSupervisora.codigo === $vue.oficinaSeleccionada.codigo;
                });
            }

            // Filtrar por tipo de aula
            if ($vue.tipoAulaSeleccionado != null) {
                $vue.aulasFiltradas = $vue.aulasFiltradas.filter(aula =>
                    aula.tipoAula && aula.tipoAula.codigo === $vue.tipoAulaSeleccionado.codigo
                );
            }

            // Filtrar por módulo (solo si es OERA y Aulas)
            if ($vue.mostrarFiltrosOera && $vue.moduloSeleccionado != null) {
                $vue.aulasFiltradas = $vue.aulasFiltradas.filter(aula =>
                    aula.aulaSuperior && aula.aulaSuperior.id === $vue.moduloSeleccionado.id
                );
            }

            // Filtrar por aula específica (solo si es OERA y Aulas)
            if ($vue.mostrarFiltrosOera && $vue.aulaSeleccionada != null) {
                $vue.aulasFiltradas = $vue.aulasFiltradas.filter(aula =>
                    aula.id === $vue.aulaSeleccionada.id
                );
            }

            $vue.calcularEstadisticas();
        },

        calcularEstadisticas() {
            let $vue = this;

            // Resetear estadísticas
            $vue.estadisticas = {
                totalAulas: 0,
                aulasLibres: 0,
                aulasOcupadas: 0,
                porcentajeOcupacion: 0
            };

            // Resetear matriz de ocupación
            $vue.matrizOcupacion = {};

            let totalAulas = $vue.aulasFiltradas.length;
            let aulasOcupadas = 0;
            let aulasLibres = 0;

            // Si hay filtros de día/hora específicos
            if ($vue.diaSeleccionado != null && $vue.horaSeleccionada != null) {
                // Calcular para ese día y hora específicos
                let key = $vue.diaSeleccionado.id + '_' + $vue.horaSeleccionada.id;

                for (let aula of $vue.aulasFiltradas) {
                    if (aula.horariosOcupadosSet.has(key)) {
                        aulasOcupadas++;
                    } else {
                        aulasLibres++;
                    }
                }

                $vue.estadisticas.totalAulas = totalAulas;
                $vue.estadisticas.aulasOcupadas = aulasOcupadas;
                $vue.estadisticas.aulasLibres = aulasLibres;
                $vue.estadisticas.porcentajeOcupacion = totalAulas > 0
                    ? Math.round((aulasOcupadas / totalAulas) * 100)
                    : 0;

            } else if ($vue.diaSeleccionado != null) {
                // Calcular para un día específico (todas las horas)
                let horasOcupadasTotal = 0;
                let horasTotales = $vue.horas.length * totalAulas;

                for (let hora of $vue.horas) {
                    let key = $vue.diaSeleccionado.id + '_' + hora.id;
                    let ocupadas = 0;

                    for (let aula of $vue.aulasFiltradas) {
                        if (aula.horariosOcupadosSet.has(key)) {
                            ocupadas++;
                        }
                    }

                    horasOcupadasTotal += ocupadas;
                }

                $vue.estadisticas.totalAulas = totalAulas;
                $vue.estadisticas.aulasOcupadas = horasOcupadasTotal;
                $vue.estadisticas.aulasLibres = horasTotales - horasOcupadasTotal;
                $vue.estadisticas.porcentajeOcupacion = horasTotales > 0
                    ? Math.round((horasOcupadasTotal / horasTotales) * 100)
                    : 0;

            } else if ($vue.horaSeleccionada != null) {
                // Calcular para una hora específica (todos los días)
                let diasOcupadosTotal = 0;
                let diasTotales = $vue.dias.length * totalAulas;

                for (let dia of $vue.dias) {
                    let key = dia.id + '_' + $vue.horaSeleccionada.id;
                    let ocupadas = 0;

                    for (let aula of $vue.aulasFiltradas) {
                        if (aula.horariosOcupadosSet.has(key)) {
                            ocupadas++;
                        }
                    }

                    diasOcupadosTotal += ocupadas;
                }

                $vue.estadisticas.totalAulas = totalAulas;
                $vue.estadisticas.aulasOcupadas = diasOcupadosTotal;
                $vue.estadisticas.aulasLibres = diasTotales - diasOcupadosTotal;
                $vue.estadisticas.porcentajeOcupacion = diasTotales > 0
                    ? Math.round((diasOcupadosTotal / diasTotales) * 100)
                    : 0;

            } else {
                // Calcular estadísticas generales (sin filtros de día/hora)
                let totalHorasPosibles = $vue.dias.length * $vue.horas.length;
                let totalHorasOcupadas = 0;

                for (let aula of $vue.aulasFiltradas) {
                    totalHorasOcupadas += aula.horasOcupadas;
                }

                let totalSlots = totalAulas * totalHorasPosibles;

                $vue.estadisticas.totalAulas = totalAulas;
                $vue.estadisticas.aulasOcupadas = totalHorasOcupadas;
                $vue.estadisticas.aulasLibres = totalSlots - totalHorasOcupadas;
                $vue.estadisticas.porcentajeOcupacion = totalSlots > 0
                    ? Math.round((totalHorasOcupadas / totalSlots) * 100)
                    : 0;
            }

            // Calcular matriz de ocupación por día/hora
            $vue.calcularMatrizOcupacion();
        },

        calcularMatrizOcupacion() {
            let $vue = this;

            for (let dia of $vue.dias) {
                for (let hora of $vue.horas) {
                    let key = dia.id + '_' + hora.id;
                    let ocupadas = 0;
                    let libres = 0;

                    for (let aula of $vue.aulasFiltradas) {
                        if (aula.horariosOcupadosSet.has(key)) {
                            ocupadas++;
                        } else {
                            libres++;
                        }
                    }

                    $vue.matrizOcupacion[key] = {
                        ocupadas: ocupadas,
                        libres: libres,
                        total: $vue.aulasFiltradas.length,
                        porcentaje: $vue.aulasFiltradas.length > 0
                            ? Math.round((ocupadas / $vue.aulasFiltradas.length) * 100)
                            : 0
                    };
                }
            }
        },

        getAulasLibresPorDiaHora(diaId, horaId) {
            let $vue = this;
            let key = diaId + '_' + horaId;
            return $vue.matrizOcupacion[key] ? $vue.matrizOcupacion[key].libres : 0;
        },

        getAulasOcupadasPorDiaHora(diaId, horaId) {
            let $vue = this;
            let key = diaId + '_' + horaId;
            return $vue.matrizOcupacion[key] ? $vue.matrizOcupacion[key].ocupadas : 0;
        },

        getCellClass(diaId, horaId) {
            let $vue = this;
            let key = diaId + '_' + horaId;
            let data = $vue.matrizOcupacion[key];

            if (!data || data.total === 0) {
                return 'ocupacion-vacio';
            }

            let porcentaje = data.porcentaje;

            if (porcentaje < 30) {
                return 'ocupacion-disponible';
            } else if (porcentaje < 60) {
                return 'ocupacion-moderado';
            } else if (porcentaje < 80) {
                return 'ocupacion-ocupado';
            } else {
                return 'ocupacion-muy-ocupado';
            }
        },

        getProgressClass(porcentaje) {
            if (porcentaje < 30) {
                return 'progress-bar-success';
            } else if (porcentaje < 60) {
                return 'progress-bar-info';
            } else if (porcentaje < 80) {
                return 'progress-bar-warning';
            } else {
                return 'progress-bar-danger';
            }
        },

        getStatusClass(porcentaje) {
            if (porcentaje < 30) {
                return 'label-success';
            } else if (porcentaje < 60) {
                return 'label-info';
            } else if (porcentaje < 80) {
                return 'label-warning';
            } else {
                return 'label-danger';
            }
        },

        getStatusText(porcentaje) {
            if (porcentaje < 30) {
                return 'Disponible';
            } else if (porcentaje < 60) {
                return 'Moderado';
            } else if (porcentaje < 80) {
                return 'Ocupado';
            } else {
                return 'Muy Ocupado';
            }
        }
    }
});
