new Vue({
    el: '#gpoSeccionesVUE',
    data: {
        // URLs y configuración
        baseURL: typeof APP !== 'undefined' ? APP.url(rutaModulo + '/listGrupo') : '/listGrupo',
        idDepartamento: window.location.pathname.split("/")[4] || '1',

        // Estados de carga y paginación
        isLoading: false,
        cursos: [],
        cursosFiltrados: [],
        totalCursos: 0,
        currentPage: 1,
        perPage: 10,
        totalPaginas: 1,
        paginasMostradas: 5, // Controla cuántas páginas mostrar en la paginación

        // Filtros
        seleccionado: '',
        estadoSeleccionada: '',
        dictadoSeleccionado: '',
        searchTerm: '',
        filtrosAvanzados: false, // Para mostrar/ocultar filtros avanzados

        // Ordenamiento
        ordenActual: '',
        direccionOrden: 'asc',

        // Estilos y clases
        bgColorClass: {ingresantes: '', departamentos: '', postgrados: '', actividades: ''},
        bgColorEstadoClass: {activos: '', inactivos: ''},
        bgColorDictadoClass: {modulares: '', semestrales: ''},

        // Datos para filtros y contadores
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

        // Datos para edición
        seccionSelect: {},
        anexoSelect: null,
    },

    mounted: function() {
        this.cargarDatos();

        // Agregar evento para detectar tecla "Escape" y limpiar filtros
        document.addEventListener('keydown', this.handleKeyDown);
    },

    beforeDestroy: function() {
        // Eliminar event listener cuando el componente se destruye
        document.removeEventListener('keydown', this.handleKeyDown);
    },

    computed: {
        // Calcula si hay algún filtro activo
        hayFiltrosActivos: function() {
            return this.seleccionado !== '' ||
                this.estadoSeleccionada !== '' ||
                this.dictadoSeleccionado !== '' ||
                this.searchTerm !== '';
        },

        // Calcula las páginas visibles para la paginación
        paginasVisibles: function() {
            if (this.totalPaginas <= this.paginasMostradas) {
                // Si hay menos páginas que el número a mostrar, devuelve todas
                return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
            }

            // Calcular el rango de páginas a mostrar
            let start = Math.max(1, this.currentPage - Math.floor(this.paginasMostradas / 2));
            let end = start + this.paginasMostradas - 1;

            // Ajustar si el final se pasa del total
            if (end > this.totalPaginas) {
                end = this.totalPaginas;
                start = Math.max(1, end - this.paginasMostradas + 1);
            }

            // Crear array con las páginas a mostrar
            return Array.from({ length: end - start + 1 }, (_, i) => start + i);
        },
    },

    methods: {
        // Métodos de carga de datos
        cargarDatos: function() {
            this.isLoading = true;

            // Construir parámetros para el filtro
            const params = {
                departamento: this.idDepartamento,
                page: this.currentPage,
                perPage: this.perPage,
                offset: (this.currentPage - 1) * this.perPage
            };

            // Añadir filtros si están activos
            if (this.seleccionado) {
                params['anexoSuperior'] = this.seleccionado;
            }

            if (this.estadoSeleccionada) {
                params['estado'] = this.estadoSeleccionada === 'activos' ? 'ACT' : 'INA';
            }

            if (this.dictadoSeleccionado) {
                params['tipoDictado'] = this.dictadoSeleccionado === 'modulares' ? 'MOD' : 'SEM';
            }

            if (this.searchTerm) {
                params['query'] = this.searchTerm;
            }

            if (this.ordenActual) {
                params['sortFields'] = [{
                    name: this.ordenActual,
                    dir: this.direccionOrden
                }];
            }

            // Mostrar mensaje de carga después de 300ms si la carga tarda
            const loadingTimeout = setTimeout(() => {
                this.isLoading = true;
            }, 300);

            axios.get(this.baseURL, { params: params })
                .then(response => {
                    clearTimeout(loadingTimeout);

                    if (response.data && Array.isArray(response.data.data)) {
                        this.cursos = response.data.data;
                        this.actualizarListaFiltrada();
                        this.totalCursos = response.data.total || this.cursos.length;
                        this.totalPaginas = Math.ceil(this.totalCursos / this.perPage);
                        this.calcularResumen();

                        // Si la página actual es mayor que el total de páginas y no es la primera página
                        if (this.currentPage > this.totalPaginas && this.totalPaginas > 0) {
                            this.irAPagina(this.totalPaginas);
                            return; // Evita continuar para no mostrar datos incorrectos
                        }
                    } else {
                        this.cursos = [];
                        this.actualizarListaFiltrada();
                    }

                    this.isLoading = false;
                })
                .catch(error => {
                    clearTimeout(loadingTimeout);
                    console.error('Error al obtener datos:', error);
                    this.isLoading = false;

                    // Mostrar mensaje de error más informativo
                    let mensajeError = 'Error al cargar los datos de cursos.';
                    if (error.response) {
                        // Error de respuesta del servidor
                        mensajeError += ` El servidor respondió con código ${error.response.status}.`;
                    } else if (error.request) {
                        // No se recibió respuesta
                        mensajeError += ' No se recibió respuesta del servidor.';
                    }

                    this.$bvToast ?
                        this.$bvToast.toast(mensajeError, {
                            title: 'Error',
                            variant: 'danger',
                            solid: true
                        }) :
                        alert(mensajeError + ' Por favor, inténtelo de nuevo más tarde.');
                });
        },

        // Cálculo de estadísticas para los filtros
        calcularResumen: function() {
            // Reiniciar contadores
            const resumen = {
                ingresantes: 0,
                departamentos: 0,
                postgrados: 0,
                actividades: 0,
                activos: 0,
                inactivos: 0,
                semestrales: 0,
                modulares: 0
            };

            // Calcular valores para cada curso
            this.cursos.forEach(curso => {
                // Anexo superior
                if (curso.anexoBoletin && curso.anexoBoletin.anexoSuperior) {
                    const anexoSup = curso.anexoBoletin.anexoSuperior.nombre || '';
                    if (anexoSup.toLowerCase().includes('ingresante')) resumen.ingresantes++;
                    else if (anexoSup.toLowerCase().includes('departamento')) resumen.departamentos++;
                    else if (anexoSup.toLowerCase().includes('postgrado')) resumen.postgrados++;
                    else if (anexoSup.toLowerCase().includes('actividad')) resumen.actividades++;
                }

                // Estado
                if (curso.estado === 'ACT') resumen.activos++;
                else resumen.inactivos++;

                // Tipo dictado
                if (curso.tipoDictadoEnum && curso.tipoDictadoEnum.name === 'MOD') resumen.modulares++;
                else resumen.semestrales++;
            });

            this.resumen = resumen;
        },

        // Métodos de filtrado
        filtrarPorAnexoSuperior: function(anexo) {
            if (this.seleccionado === anexo) {
                // Si ya está seleccionado, quitar filtro
                this.seleccionado = '';
                this.bgColorClass = {ingresantes: '', departamentos: '', postgrados: '', actividades: ''};
            } else {
                this.seleccionado = anexo;
                this.bgColorClass = {ingresantes: '', departamentos: '', postgrados: '', actividades: ''};
                this.bgColorClass[anexo] = 'bg-light';
            }

            this.cargarDatos();
        },

        filtrarPorEstado: function(estado) {
            if (this.estadoSeleccionada === estado) {
                // Si ya está seleccionado, quitar filtro
                this.estadoSeleccionada = '';
                this.bgColorEstadoClass = {activos: '', inactivos: ''};
            } else {
                this.estadoSeleccionada = estado;
                this.bgColorEstadoClass = {activos: '', inactivos: ''};
                this.bgColorEstadoClass[estado] = 'bg-light';
            }

            this.cargarDatos();
        },

        filtrarPorDictado: function(dictado) {
            if (this.dictadoSeleccionado === dictado) {
                // Si ya está seleccionado, quitar filtro
                this.dictadoSeleccionado = '';
                this.bgColorDictadoClass = {modulares: '', semestrales: ''};
            } else {
                this.dictadoSeleccionado = dictado;
                this.bgColorDictadoClass = {modulares: '', semestrales: ''};
                this.bgColorDictadoClass[dictado] = 'bg-light';
            }

            this.cargarDatos();
        },

        buscarCursos: function() {
            clearTimeout(this._timeoutSearch);
            this._timeoutSearch = setTimeout(() => {
                this.currentPage = 1; // Resetear a primera página al buscar
                this.cargarDatos();
            }, 500);
        },

        limpiarFiltros: function() {
            this.seleccionado = '';
            this.estadoSeleccionada = '';
            this.dictadoSeleccionado = '';
            this.searchTerm = '';
            this.bgColorClass = {ingresantes: '', departamentos: '', postgrados: '', actividades: ''};
            this.bgColorEstadoClass = {activos: '', inactivos: ''};
            this.bgColorDictadoClass = {modulares: '', semestrales: ''};
            this.currentPage = 1; // Resetear a primera página
            this.cargarDatos();
        },

        limpiarFiltroEspecifico: function(filtro) {
            // Limpiar solo un filtro específico
            this[filtro] = '';

            // Resetear clase de fondo si es necesario
            if (filtro === 'seleccionado') {
                this.bgColorClass = {ingresantes: '', departamentos: '', postgrados: '', actividades: ''};
            } else if (filtro === 'estadoSeleccionada') {
                this.bgColorEstadoClass = {activos: '', inactivos: ''};
            } else if (filtro === 'dictadoSeleccionado') {
                this.bgColorDictadoClass = {modulares: '', semestrales: ''};
            }

            this.currentPage = 1; // Resetear a primera página
            this.cargarDatos();
        },

        // Método para detectar tecla Escape
        handleKeyDown: function(event) {
            if (event.key === 'Escape') {
                this.limpiarFiltros();
            }
        },

        // Obtener nombre legible para los filtros activos
        obtenerNombreFiltro: function(tipo, valor) {
            if (tipo === 'programa') {
                const programas = {
                    'ingresantes': 'Ingresantes',
                    'departamentos': 'Departamentos',
                    'postgrados': 'Postgrados',
                    'actividades': 'Actividades'
                };
                return programas[valor] || valor;
            } else if (tipo === 'estado') {
                const estados = {
                    'activos': 'Activos',
                    'inactivos': 'Inactivos'
                };
                return estados[valor] || valor;
            } else if (tipo === 'dictado') {
                const dictado = {
                    'modulares': 'Modulares',
                    'semestrales': 'Semestrales'
                };
                return dictado[valor] || valor;
            }
            return valor;
        },

        actualizarListaFiltrada: function() {
            this.cursosFiltrados = [...this.cursos];
        },

        // Métodos de paginación
        irAPagina: function(pagina) {
            if (pagina < 1 || pagina > this.totalPaginas) return;
            this.currentPage = pagina;
            this.cargarDatos();

            // Scroll al inicio de la tabla
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

        // Métodos de ordenamiento
        ordenarPor: function(campo) {
            if (this.ordenActual === campo) {
                // Invertir dirección si el campo ya está seleccionado
                this.direccionOrden = this.direccionOrden === 'asc' ? 'desc' : 'asc';
            } else {
                this.ordenActual = campo;
                this.direccionOrden = 'asc';
            }

            this.cargarDatos();
        },

        getOrdenIcon: function(campo) {
            if (this.ordenActual !== campo) return 'fa-sort';
            return this.direccionOrden === 'asc' ? 'fa-sort-up' : 'fa-sort-down';
        },

        // Métodos auxiliares para renderizar la tabla con estructura compleja
        getCursoRowspan: function(item) {
            let totalFilas = 0;

            if (!item.secciones || !Array.isArray(item.secciones)) {
                return 1;
            }

            item.secciones.forEach(seccion => {
                if (seccion.docenteSeccion && Array.isArray(seccion.docenteSeccion) && seccion.docenteSeccion.length > 0) {
                    totalFilas += seccion.docenteSeccion.length;
                } else {
                    totalFilas += 1; // Al menos una fila por sección aunque no tenga docentes
                }
            });

            return totalFilas > 0 ? totalFilas : 1;
        },

        getCursoFilas: function(item) {
            // Devuelve un array con elementos dummy para facilitar el v-for que crea las filas del curso
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
                    // Encontramos la sección, ahora calculamos qué docente corresponde a esta fila
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

        editarCurso: function(item) {
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