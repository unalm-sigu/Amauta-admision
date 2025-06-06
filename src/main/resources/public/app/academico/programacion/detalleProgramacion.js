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
            this.tienesFiltrosLocales = !!(this.searchTerm || this.filtroEstadoSeccion);

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
            if (this.filtroEstadoSeccion || this.searchTerm.trim()) {
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

            this.cursosFiltrados = filtered;
            this.totalCursos = this.cursosFiltrados.length;
            this.totalPaginas = Math.ceil(this.totalCursos / this.perPage);

            // Actualizar flag de filtros locales
            this.tienesFiltrosLocales = !!(this.searchTerm.trim() || this.filtroEstadoSeccion);

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
        }
    }
});