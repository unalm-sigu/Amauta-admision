new Vue({
    el: '#gpoSeccionesVUE',
    data: {
        baseURL: typeof APP !== 'undefined' ? APP.url(rutaModulo + '/listGrupo') : '/listGrupo',
        idDepartamento: window.location.pathname.split("/")[4] || '1',

        isLoading: false,
        cursos: [],
        cursosFiltrados: [],
        totalCursos: 0,
        currentPage: 1,
        perPage: 10,
        totalPaginas: 1,
        paginasMostradas: 5,
        filtroEstadoSeccion: '',
        filtroDocentesNN: false,
        filtroPocoMatriculados: false,
        searchTerm: '',

        searchTimeout: null,
        searchDelay: 500,

        nombreDepartamento: '',
        tienesFiltrosLocales: false,
    },

    mounted: function() {
        this.cargarDatos();
    },

    computed: {
        cursosPaginados: function() {
            // Solo paginar localmente si hay filtros de búsqueda o estado
            if (this.tienesFiltrosLocales) {
                const inicio = (this.currentPage - 1) * this.perPage;
                const fin = inicio + this.perPage;
                return this.cursosFiltrados.slice(inicio, fin);
            }
            // Si no hay filtros, los datos ya vienen paginados del servidor
            return this.cursosFiltrados;
        },

        paginasVisibles: function() {
            if (this.totalPaginas <= this.paginasMostradas) {
                return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
            }
            let start = Math.max(1, this.currentPage - Math.floor(this.paginasMostradas / 2));
            let end = start + this.paginasMostradas - 1;

            if (end > this.totalPaginas) {
                end = this.totalPaginas;
                start = Math.max(1, end - this.paginasMostradas + 1);
            }

            return Array.from({ length: end - start + 1 }, (_, i) => start + i);
        },

        estadisticas: function() {
            let totalCursos = this.cursosFiltrados.length;
            let totalSecciones = 0;
            let totalMatriculados = 0;
            let totalVacantes = 0;
            let seccionesSinDocente = 0;
            let seccionesPocoMatriculados = 0;
            let docentesUnicos = new Set();

            this.cursosFiltrados.forEach(curso => {
                if (curso.secciones && Array.isArray(curso.secciones)) {
                    curso.secciones.forEach(seccion => {
                        totalSecciones++;

                        // Contar matriculados y vacantes
                        const matriculados = this.getMatriculados(seccion);
                        const vacantes = this.getVacantes(seccion);
                        totalMatriculados += matriculados;
                        totalVacantes += vacantes;

                        // Contar secciones con poco matriculados
                        if (matriculados < 6) {
                            seccionesPocoMatriculados++;
                        }

                        // Contar docentes y secciones sin docente
                        if (!seccion.docenteSeccion || seccion.docenteSeccion.length === 0) {
                            seccionesSinDocente++;
                        } else {
                            const tieneDocenteReal = seccion.docenteSeccion.some(ds =>
                                ds.docente && ds.docente.codigo && ds.docente.codigo !== 'N.N.'
                            );

                            if (!tieneDocenteReal) {
                                seccionesSinDocente++;
                            } else {
                                // Agregar docentes únicos
                                seccion.docenteSeccion.forEach(ds => {
                                    if (ds.docente && ds.docente.codigo && ds.docente.codigo !== 'N.N.') {
                                        docentesUnicos.add(ds.docente.codigo);
                                    }
                                });
                            }
                        }
                    });
                }
            });

            const promedioMatriculados = totalSecciones > 0
                ? Math.round((totalMatriculados / totalSecciones) * 10) / 10
                : 0;

            const totalCapacidad = totalMatriculados + totalVacantes;
            const porcentajeOcupacion = totalCapacidad > 0
                ? Math.round((totalMatriculados / totalCapacidad) * 100)
                : 0;

            const seccionesConProblemas = seccionesSinDocente + seccionesPocoMatriculados;

            return {
                totalCursos: totalCursos,
                totalSecciones: totalSecciones,
                totalMatriculados: totalMatriculados,
                totalVacantes: totalVacantes,
                docentesAsignados: docentesUnicos.size,
                seccionesSinDocente: seccionesSinDocente,
                seccionesPocoMatriculados: seccionesPocoMatriculados,
                promedioMatriculados: promedioMatriculados,
                porcentajeOcupacion: porcentajeOcupacion,
                seccionesConProblemas: seccionesConProblemas
            };
        },

        // Detectar si hay filtros activos
        hayFiltrosActivos: function() {
            return !!(this.searchTerm.trim() || this.filtroEstadoSeccion ||
                      this.filtroDocentesNN || this.filtroPocoMatriculados);
        },

        // Contar cantidad de filtros activos
        cantidadFiltrosActivos: function() {
            let cantidad = 0;
            if (this.searchTerm.trim()) cantidad++;
            if (this.filtroEstadoSeccion) cantidad++;
            if (this.filtroDocentesNN) cantidad++;
            if (this.filtroPocoMatriculados) cantidad++;
            return cantidad;
        },

        // Lista de filtros activos para mostrar como tags
        filtrosActivos: function() {
            const filtros = [];

            if (this.searchTerm.trim()) {
                filtros.push({
                    key: 'search',
                    label: `Búsqueda: "${this.searchTerm}"`,
                    icon: 'fa fa-search'
                });
            }

            if (this.filtroEstadoSeccion) {
                const estadoLabels = {
                    'ACT': 'Activo',
                    'INA': 'Inactivo',
                    'FUS': 'Fusionado'
                };
                filtros.push({
                    key: 'estado',
                    label: `Estado: ${estadoLabels[this.filtroEstadoSeccion] || this.filtroEstadoSeccion}`,
                    icon: 'fa fa-toggle-on'
                });
            }

            if (this.filtroDocentesNN) {
                filtros.push({
                    key: 'docentesNN',
                    label: 'Solo docentes N.N.',
                    icon: 'fa fa-user-slash'
                });
            }

            if (this.filtroPocoMatriculados) {
                filtros.push({
                    key: 'pocoMatriculados',
                    label: 'Menos de 6 matriculados',
                    icon: 'fa fa-user-minus'
                });
            }

            return filtros;
        },
    },

    methods: {
        goBack() {
            window.history.back();
        },

        cargarDatos: function() {
            this.isLoading = true;

            const params = {
                departamento: this.idDepartamento,
            };

            // Determinar si tenemos filtros locales activos
            this.tienesFiltrosLocales = !!(this.searchTerm || this.filtroEstadoSeccion || this.filtroDocentesNN || this.filtroPocoMatriculados);

            // Si no hay filtros locales, usar paginación del servidor
            if (!this.tienesFiltrosLocales) {
                params.page = this.currentPage;
                params.perPage = this.perPage;
                params.offset = (this.currentPage - 1) * this.perPage;
            }

            axios.get(this.baseURL, { params: params })
                .then(response => {
                    if (response.data && Array.isArray(response.data.data)) {
                        this.cursos = response.data.data;

                        if (this.tienesFiltrosLocales) {
                            // Si hay filtros locales, aplicar filtrado y paginación local
                            this.aplicarFiltrosBusqueda();
                        } else {
                            // Si no hay filtros, usar datos del servidor directamente
                            this.cursosFiltrados = [...this.cursos];
                            this.totalCursos = response.data.total || this.cursos.length;
                            this.totalPaginas = Math.ceil(this.totalCursos / this.perPage);
                        }

                        // Obtener nombre del departamento
                        if (this.cursos.length > 0 && this.cursos[0].anexoBoletin && this.cursos[0].anexoBoletin.nombre) {
                            this.nombreDepartamento = this.cursos[0].anexoBoletin.nombre;
                        } else if (this.cursos.length > 0 && this.cursos[0].curso && this.cursos[0].curso.departamentoAcademico) {
                            this.nombreDepartamento = this.cursos[0].curso.departamentoAcademico.nombre;
                        } else {
                            this.nombreDepartamento = 'Departamento ' + this.idDepartamento;
                        }

                        // Validar página actual
                        if (this.currentPage > this.totalPaginas && this.totalPaginas > 0) {
                            this.irAPagina(this.totalPaginas);
                            return;
                        }
                    } else {
                        this.resetearDatos();
                    }
                    this.isLoading = false;
                })
                .catch(error => {
                    console.error('Error al obtener datos:', error);
                    this.resetearDatos();
                    this.isLoading = false;
                });
        },

        resetearDatos: function() {
            this.cursos = [];
            this.cursosFiltrados = [];
            this.totalCursos = 0;
            this.totalPaginas = 0;
            this.nombreDepartamento = this.isLoading ? 'Error al cargar' : 'Sin datos';
        },

        buscarCursos: function() {
            if (this.searchTimeout) {
                clearTimeout(this.searchTimeout);
            }

            this.searchTimeout = setTimeout(() => {
                this.currentPage = 1;

                // Si hay término de búsqueda, necesitamos cargar todos los datos para filtrar localmente
                if (this.searchTerm.trim()) {
                    this.cargarTodosLosRegistros();
                } else {
                    this.cargarDatos();
                }
            }, this.searchDelay);
        },

        cargarTodosLosRegistros: function() {
            this.isLoading = true;

            const params = {
                departamento: this.idDepartamento,
                // No enviar parámetros de paginación para obtener todos los registros
                perPage: 9999, // O el máximo que permita tu backend
            };

            axios.get(this.baseURL, { params: params })
                .then(response => {
                    if (response.data && Array.isArray(response.data.data)) {
                        this.cursos = response.data.data;
                        this.aplicarFiltrosBusqueda();

                        // Obtener nombre del departamento
                        if (this.cursos.length > 0 && this.cursos[0].anexoBoletin && this.cursos[0].anexoBoletin.nombre) {
                            this.nombreDepartamento = this.cursos[0].anexoBoletin.nombre;
                        }
                    } else {
                        this.resetearDatos();
                    }
                    this.isLoading = false;
                })
                .catch(error => {
                    console.error('Error al obtener todos los datos:', error);
                    this.resetearDatos();
                    this.isLoading = false;
                });
        },

        normalizarTexto: function(texto) {
            if (!texto) return '';

            return texto
                .toString()
                .toLowerCase()
                .normalize('NFD')
                .replace(/[\u0300-\u036f]/g, '')
                .trim();
        },

        aplicarFiltros: function() {
            this.currentPage = 1;

            // Si hay filtros activos, cargar todos los registros para filtrar localmente
            if (this.filtroEstadoSeccion || this.searchTerm.trim() || this.filtroDocentesNN || this.filtroPocoMatriculados) {
                this.cargarTodosLosRegistros();
            } else {
                this.cargarDatos();
            }
        },

        aplicarFiltrosBusqueda: function() {
            let filtered = [...this.cursos];

            // Aplicar filtro por estado
            if (this.filtroEstadoSeccion) {
                filtered = filtered.filter(curso => {
                    return curso.secciones && curso.secciones.some(
                        seccion => seccion.estadoEnum && seccion.estadoEnum.name === this.filtroEstadoSeccion
                    );
                });
            }

            // Aplicar filtro de búsqueda
            if (this.searchTerm && this.searchTerm.trim()) {
                const termNormalizado = this.normalizarTexto(this.searchTerm);
                filtered = filtered.filter(curso => {
                    // Buscar en nombre del curso
                    const nombreCurso = this.normalizarTexto(curso.curso.nombre);
                    if (nombreCurso.includes(termNormalizado)) return true;

                    // Buscar en código del curso
                    const codigoCurso = this.normalizarTexto(curso.curso.codigo);
                    if (codigoCurso.includes(termNormalizado)) return true;

                    if (curso.secciones && curso.secciones.length > 0) {
                        for (const seccionx of curso.secciones) {
                            const codigoSeccion = this.normalizarTexto(seccionx.codigo2);
                            if(codigoSeccion.includes(termNormalizado)) return true;
                        }
                    }

                    // Buscar en docentes
                    if (curso.secciones && curso.secciones.length > 0) {
                        for (const seccion of curso.secciones) {
                            if (seccion.docenteSeccion && seccion.docenteSeccion.length > 0) {
                                for (const docenteSeccion of seccion.docenteSeccion) {
                                    if (docenteSeccion.docente && docenteSeccion.docente.persona) {
                                        const nombreDocente = this.normalizarTexto(docenteSeccion.docente.persona.apellidosNombres);
                                        if (nombreDocente.includes(termNormalizado)) return true;

                                        const codigoDocente = this.normalizarTexto(docenteSeccion.docente.codigo);
                                        if (codigoDocente.includes(termNormalizado)) return true;
                                    }
                                }
                            }
                        }
                    }
                    return false;
                });
            }

            // Aplicar filtro de docentes N.N.
            if (this.filtroDocentesNN) {
                filtered = filtered.filter(curso => {
                    if (!curso.secciones || !Array.isArray(curso.secciones)) {
                        return false;
                    }

                    // Un curso pasa el filtro si tiene al menos una sección con docente N.N.
                    return curso.secciones.some(seccion => {
                        if (!seccion.docenteSeccion || seccion.docenteSeccion.length === 0) {
                            return true;
                        }

                        const tieneDocenteReal = seccion.docenteSeccion.some(ds =>
                            ds.docente && ds.docente.codigo && ds.docente.codigo !== 'N.N.'
                        );
                        return !tieneDocenteReal;
                    });
                });
            }

            // Aplicar filtro de poco matriculados (menos de 6)
            if (this.filtroPocoMatriculados) {
                filtered = filtered.filter(curso => {
                    if (!curso.secciones || !Array.isArray(curso.secciones)) {
                        return false;
                    }

                    // Un curso pasa el filtro si tiene al menos una sección con menos de 6 matriculados
                    return curso.secciones.some(seccion => {
                        const matriculados = this.getMatriculados(seccion);
                        return matriculados < 6;
                    });
                });
            }

            this.cursosFiltrados = filtered;
            this.totalCursos = this.cursosFiltrados.length;
            this.totalPaginas = Math.ceil(this.totalCursos / this.perPage);

            // Actualizar flag de filtros locales
            this.tienesFiltrosLocales = !!(this.searchTerm.trim() || this.filtroEstadoSeccion || this.filtroDocentesNN || this.filtroPocoMatriculados);

            // Validar página actual
            if (this.currentPage > this.totalPaginas && this.totalPaginas > 0) {
                this.currentPage = 1;
            }
        },

        irAPagina: function(pagina) {
            if (pagina < 1 || pagina > this.totalPaginas) return;

            this.currentPage = pagina;

            // Solo recargar datos del servidor si no hay filtros locales
            if (!this.tienesFiltrosLocales) {
                this.cargarDatos();
            }

            // Scroll suave a la tabla
            setTimeout(() => {
                const tabla = document.querySelector('.table-responsive');
                if (tabla) {
                    tabla.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }, 100);
        },

        cambiarRegistrosPorPagina: function() {
            this.currentPage = 1;

            // Si hay filtros activos, recalcular paginación local
            if (this.tienesFiltrosLocales) {
                this.totalPaginas = Math.ceil(this.totalCursos / this.perPage);
            } else {
                // Si no hay filtros, recargar del servidor con nueva paginación
                this.cargarDatos();
            }
        },

        getCursoRowspan: function(item) {
            let totalFilas = 0;

            if (!item.secciones || !Array.isArray(item.secciones)) {
                return 1;
            }

            item.secciones.forEach(seccion => {
                if (seccion.docenteSeccion && Array.isArray(seccion.docenteSeccion) && seccion.docenteSeccion.length > 0) {
                    totalFilas += seccion.docenteSeccion.length;
                } else {
                    totalFilas += 1;
                }
            });

            return totalFilas > 0 ? totalFilas : 1;
        },

        getCursoFilas: function(item) {
            const filasTotal = this.getCursoRowspan(item);
            return Array(filasTotal).fill(null);
        },

        getSeccionParaFila: function(item, rowIndex) {
            if (!item.secciones || !Array.isArray(item.secciones)) {
                return null;
            }

            let filaActual = 0;

            for (let i = 0; i < item.secciones.length; i++) {
                const seccion = item.secciones[i];
                const filasSeccion = seccion.docenteSeccion && seccion.docenteSeccion.length > 0 ?
                    seccion.docenteSeccion.length : 1;

                if (rowIndex >= filaActual && rowIndex < filaActual + filasSeccion) {
                    return seccion;
                }

                filaActual += filasSeccion;
            }

            return null;
        },

        getDocenteParaFila: function(item, rowIndex) {
            const seccion = this.getSeccionParaFila(item, rowIndex);
            if (!seccion || !seccion.docenteSeccion || !Array.isArray(seccion.docenteSeccion)) {
                return null;
            }

            let filaActual = 0;

            for (let i = 0; i < item.secciones.length; i++) {
                const s = item.secciones[i];

                if (s === seccion) {
                    const indiceRelativo = rowIndex - filaActual;

                    if (indiceRelativo < seccion.docenteSeccion.length) {
                        return seccion.docenteSeccion[indiceRelativo];
                    }

                    break;
                }

                filaActual += s.docenteSeccion && s.docenteSeccion.length > 0 ?
                    s.docenteSeccion.length : 1;
            }

            return null;
        },

        getTipoSeccion: function(seccion) {
            return seccion && seccion.tipoSeccionEnum ? seccion.tipoSeccionEnum.value : '';
        },

        redireccionCurso: function(item) {
            if (typeof APP !== 'undefined' && APP.url) {
                const url = APP.url('academico/gposeccion/' + item.id + '/editar');
                window.location.href = url;
            } else {
                alert('Grupo seccion: ' + item.curso.nombre);
            }
        },

        // Nuevos métodos para manejar información de matrícula y vacantes
        getMatriculados: function(seccion) {
            if (!seccion) return 0;

            // Buscar el número de matriculados en diferentes posibles propiedades
            if (seccion.matriculados !== undefined) return seccion.matriculados;

            // Si hay una lista de estudiantes matriculados
            if (seccion.estudiantes && Array.isArray(seccion.estudiantes)) {
                return seccion.estudiantes.length;
            }
            if (seccion.matriculas && Array.isArray(seccion.matriculas)) {
                return seccion.matriculas.length;
            }

            return 0;
        },

        getVacantes: function(seccion) {
            if (!seccion) return 0;

            // Buscar el número de vacantes en diferentes posibles propiedades
            if (seccion.vacantes !== undefined) return seccion.vacantes;

            return 0;
        },

        // Métodos para contar secciones con filtros especiales
        contarDocentesNN: function() {
            let contador = 0;

            this.cursosFiltrados.forEach(curso => {
                if (curso.secciones && Array.isArray(curso.secciones)) {
                    curso.secciones.forEach(seccion => {
                        // Una sección tiene docente N.N. si no tiene docentes asignados
                        // o si todos sus docentes tienen código "N.N."
                        if (!seccion.docenteSeccion || seccion.docenteSeccion.length === 0) {
                            contador++;
                        } else {
                            const tieneDocenteReal = seccion.docenteSeccion.some(ds =>
                                ds.docente && ds.docente.codigo && ds.docente.codigo !== 'N.N.'
                            );
                            if (!tieneDocenteReal) {
                                contador++;
                            }
                        }
                    });
                }
            });

            return contador;
        },

        contarPocoMatriculados: function() {
            let contador = 0;

            this.cursosFiltrados.forEach(curso => {
                if (curso.secciones && Array.isArray(curso.secciones)) {
                    curso.secciones.forEach(seccion => {
                        const matriculados = this.getMatriculados(seccion);
                        if (matriculados < 6) {
                            contador++;
                        }
                    });
                }
            });

            return contador;
        },

        // Limpiar todos los filtros
        limpiarFiltros: function() {
            this.searchTerm = '';
            this.filtroEstadoSeccion = '';
            this.filtroDocentesNN = false;
            this.filtroPocoMatriculados = false;
            this.currentPage = 1;
            this.cargarDatos();
        },

        // Quitar un filtro específico
        quitarFiltro: function(filtroKey) {
            switch(filtroKey) {
                case 'search':
                    this.searchTerm = '';
                    break;
                case 'estado':
                    this.filtroEstadoSeccion = '';
                    break;
                case 'docentesNN':
                    this.filtroDocentesNN = false;
                    break;
                case 'pocoMatriculados':
                    this.filtroPocoMatriculados = false;
                    break;
            }
            this.aplicarFiltros();
        },

        // Detectar si una sección tiene docente N.N.
        seccionSinDocente: function(seccion) {
            if (!seccion) return false;

            if (!seccion.docenteSeccion || seccion.docenteSeccion.length === 0) {
                return true;
            }

            const tieneDocenteReal = seccion.docenteSeccion.some(ds =>
                ds.docente && ds.docente.codigo && ds.docente.codigo !== 'N.N.'
            );

            return !tieneDocenteReal;
        },

        // Detectar si una sección tiene pocos matriculados
        seccionPocoMatriculados: function(seccion) {
            if (!seccion) return false;
            const matriculados = this.getMatriculados(seccion);
            return matriculados < 6;
        },

        // Detectar si una sección está inactiva
        seccionInactiva: function(seccion) {
            if (!seccion) return false;
            return seccion.estadoEnum && seccion.estadoEnum.name !== 'ACT';
        },

        // Obtener clase CSS para la fila según sus problemas
        getRowClass: function(item, rowIndex) {
            const seccion = this.getSeccionParaFila(item, rowIndex);
            if (!seccion) return '';

            const problemas = [];

            if (this.seccionSinDocente(seccion)) {
                problemas.push('sin-docente');
            }

            if (this.seccionPocoMatriculados(seccion)) {
                problemas.push('poco-matriculados');
            }

            if (this.seccionInactiva(seccion)) {
                problemas.push('inactivo');
            }

            // Si tiene múltiples problemas, usar clase especial
            if (problemas.length > 1) {
                return 'row-multiples-problemas';
            }

            // Si tiene un solo problema
            if (problemas.length === 1) {
                return 'row-' + problemas[0];
            }

            return '';
        },

        // Obtener indicadores de problemas para mostrar
        getProblemasIndicadores: function(seccion) {
            if (!seccion) return [];

            const indicadores = [];

            if (this.seccionSinDocente(seccion)) {
                indicadores.push({
                    texto: 'Sin docente',
                    icono: 'fa fa-user-times',
                    clase: 'critical'
                });
            }

            if (this.seccionPocoMatriculados(seccion)) {
                indicadores.push({
                    texto: 'Pocos alumnos',
                    icono: 'fa fa-exclamation-triangle',
                    clase: 'warning'
                });
            }

            if (this.seccionInactiva(seccion)) {
                indicadores.push({
                    texto: 'Inactiva',
                    icono: 'fa fa-ban',
                    clase: 'info'
                });
            }

            return indicadores;
        }
    }
});