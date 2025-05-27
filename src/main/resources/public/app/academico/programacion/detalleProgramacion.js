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
        seleccionado: '',
        filtroEstadoSeccion:'',
        dictadoSeleccionado: '',
        searchTerm: '',
        filtrosAvanzados: false,

        // 👇 NUEVO: Para el debounce
        searchTimeout: null,
        searchDelay: 500, // 500ms de espera después de dejar de escribir

        ordenActual: '',
        direccionOrden: 'asc',
        nombreDepartamento: '',

        bgColorClass: {ingresantes: '', departamentos: '', postgrados: '', actividades: ''},
        bgColorEstadoClass: {activos: '', inactivos: ''},
        bgColorDictadoClass: {modulares: '', semestrales: ''},

        anexosSup: {ingresantes: 1, departamentos: 2, postgrados: 4, actividades: 3},
        resumen: {
            ingresantes: 0,
            departamentos: 0,
            postgrados: 0,
            actividades: 0,
            activos: 0,
            inactivos: 0,
            semestrales: 0,
            modulares: 0
        },
    },

    mounted: function() {
        this.cargarDatos();
        console.log('Estado de las secciones:',
            this.cursos.map(curso => ({
                nombre: curso.curso.nombre,
                estados: curso.secciones?.map(s => s.estadoEnum?.name)
            }))
        );
    },

    computed: {
        cursosPaginados: function() {
            const inicio = (this.currentPage - 1) * this.perPage;
            const fin = inicio + this.perPage;
            return this.cursosFiltrados.slice(inicio, fin);
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

            if (!this.searchTerm) {
                params.page = this.currentPage;
                params.perPage = this.perPage;
                params.offset = (this.currentPage - 1) * this.perPage;
            }

            axios.get(this.baseURL, { params: params })
                .then(response => {
                    if (response.data && Array.isArray(response.data.data)) {
                        this.cursos = response.data.data;

                        if (this.searchTerm) {
                            this.aplicarFiltrosBusqueda();
                        } else {
                            this.cursosFiltrados = [...this.cursos];
                            this.totalCursos = response.data.total || this.cursos.length;
                        }

                        this.totalPaginas = Math.ceil(this.totalCursos / this.perPage);

                        if (this.cursos.length > 0 && this.cursos[0].anexoBoletin && this.cursos[0].anexoBoletin.nombre) {
                            this.nombreDepartamento = this.cursos[0].anexoBoletin.nombre;
                        } else if (this.cursos.length > 0 && this.cursos[0].curso && this.cursos[0].curso.departamentoAcademico) {
                            this.nombreDepartamento = this.cursos[0].curso.departamentoAcademico.nombre;
                        } else {
                            this.nombreDepartamento = 'Departamento ' + this.idDepartamento;
                        }

                        if (this.currentPage > this.totalPaginas && this.totalPaginas > 0) {
                            this.irAPagina(this.totalPaginas);
                            return;
                        }
                    } else {
                        this.cursos = [];
                        this.cursosFiltrados = [];
                        this.totalCursos = 0;
                        this.totalPaginas = 0;
                        this.nombreDepartamento = 'Sin datos';
                    }
                    this.isLoading = false;
                })
                .catch(error => {
                    console.error('Error al obtener datos:', error);
                    this.isLoading = false;
                    this.cursos = [];
                    this.cursosFiltrados = [];
                    this.totalCursos = 0;
                    this.totalPaginas = 0;
                    this.nombreDepartamento = 'Error al cargar';
                });
        },

        buscarCursos: function() {
            if (this.searchTimeout) {
                clearTimeout(this.searchTimeout);
            }

            this.searchTimeout = setTimeout(() => {
                console.log('🔍 Ejecutando búsqueda para:', this.searchTerm);
                this.currentPage = 1;

                if (this.searchTerm) {
                    this.cargarDatos();
                } else {
                    this.cargarDatos();
                }
            }, this.searchDelay);
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
            this.filtrarCursos();
        },

        filtrarCursos() {
            let filtrado = this.cursos;

            if (this.searchTerm) {
                const termino = this.searchTerm.toLowerCase();
                filtrado = filtrado.filter(curso =>
                    curso.curso.nombre.toLowerCase().includes(termino) ||
                    curso.curso.codigo.toLowerCase().includes(termino) ||
                    (curso.docente?.persona?.apellidosNombres?.toLowerCase().includes(termino))
                );
            }

            if (this.filtroEstadoSeccion) {
                filtrado = filtrado.filter(curso =>
                    curso.secciones?.some(seccion =>
                        seccion.estadoEnum?.name === this.filtroEstadoSeccion
                    )
                );
            }

            this.cursosFiltrados = filtrado;
            this.totalCursos = this.cursosFiltrados.length;
            this.totalPaginas = Math.ceil(this.totalCursos / this.perPage);
        },

        aplicarFiltrosBusqueda: function() {
            let filtered = this.filtroEstadoSeccion
                ? this.cursos.filter(curso => {

                    return curso.secciones && curso.secciones.some(
                        seccion => seccion.estadoEnum && seccion.estadoEnum.name === this.filtroEstadoSeccion
                    );
                })
                : [...this.cursos];

            if (this.searchTerm) {
                const termNormalizado = this.normalizarTexto(this.searchTerm);
                filtered = filtered.filter(curso => {
                    const nombreCurso = this.normalizarTexto(curso.curso.nombre);
                    if (nombreCurso.includes(termNormalizado)) return true;

                    const codigoCurso = this.normalizarTexto(curso.curso.codigo);
                    if (codigoCurso.includes(termNormalizado)) return true;

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

            if (this.currentPage > this.totalPaginas && this.totalPaginas > 0) {
                this.currentPage = 1;
            }
        },

        irAPagina: function(pagina) {
            if (pagina < 1 || pagina > this.totalPaginas) return;

            this.currentPage = pagina;

            if (!this.searchTerm && !this.filtroEstadoSeccion) {
                this.cargarDatos();
            }

            setTimeout(() => {
                const tabla = document.querySelector('.table-responsive');
                if (tabla) {
                    tabla.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }, 100);
        },

        cambiarRegistrosPorPagina: function() {
            this.currentPage = 1;
            this.cargarDatos();
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
                console.log(item)
            } else {
                alert('Grupo seccion: ' + item.curso.nombre);
            }
        }
    }
});