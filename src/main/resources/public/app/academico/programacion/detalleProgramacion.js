Vue.component("multiselect", window.VueMultiselect.default);
Vue.component("seccion-det-component", {
    template: "#seccionDetComp",
    props: {
        seccion: null
    }, watch: {
        seccion(newValue) {
        }
    }
});

new Vue({
    el: '#gpoSeccionesVUE',
    mixins: [VueLoader],
    data: {
        gpoSeccionesURL: APP.url(rutaModulo + '/listGrupo'),
        seleccionado: '',
        estadoSeleccionada: '',
        dictadoSeleccionado: '',
        bgColorClass: {ingresantes: '', departamentos: '', postgrados: '', actividades: ''},
        bgColorEstadoClass: {activos: '', inactivos: ''},
        bgColorDictadoClass: {modulares: '', semestrales: ''},
        anexosSup: {ingresantes: 1, departamentos: 2, postgrados: 4, actividades: 3},
        anexos: [],
        anexosVisibles: [],
        anexoSelect: null,
        restriccionModal: {
            id: 'modalRestriccion',
            header: true,
            title: 'Restricciones Modalidad / Facultad / Especialidad',
            modalsize: 'modal-md'
        },
        nuevoGpoSeccModal: VUE_MODAL.structFormAjax({
            id: 'modalNuevoGpoSecc',
            form: 'formNuevoGpoSecc',
            header: true,
            title: 'Nuevo grupo de secciones',
            revisarForm: false
        }),
        dataCloneCiclo: VUE_MODAL.structFormAjax({
            id: 'modalCloneCiclo',
            title: 'Clonación de información por ciclo',
            header: true
        }),
        seccionSelect: {},
        tipoRestriccion: '',
        resumen: {
            ingresantes: 0,
            departamentos: 0,
            postGrados: 0,
            actividades: 0,
            activos: 0,
            inactivos: 0,
            semestrales: 0,
            modulares: 0
        },
        cursos: [],
        isLoadingCursos: false,
        anexosPadres: [],
        anexosHijos: [],
        listCicloAcademico: [],
        anexoPadreCurso: {},
        newGrupoSeccion: {curso: {}, anexoBoletin: {}},
        configConfirmAction: VUE_MODAL.structConfirm({}),
        cicloClonacionBean: {copiarAulasOera: false, copiarAulasDptos: false, copiarAulasPosgrado: false},
        gpoSeccionesSelects: [],
        direccionSeccion: 0,
        direccionCurso: 0,
        styleOrdenSeccion: "",
        styleOrdenCurso: "",
        ordenRegistros: "",

    },
    computed: {

    },
    mounted: function () {
        let $vue = this;
        // $vue.loadDataInicial();
    },
    methods: {
        getParameterQuery(param) {
            let $vue = this;
            let value = $vue.$refs.raptorGpoSecc.getParameterByName('queries[' + param + ']');
            value = (value === null) ? '' : value;
            return value;
        },
        setParameterQuery(param, value) {
            let $vue = this;
            if (value !== '') {
                $vue.$refs.raptorGpoSecc.querie.push({name: param, value: value});
            }
        },
        configInicio() {
            let $vue = this;
            let anxSup = $vue.getParameterQuery('anexo-superior');
            if (anxSup !== '') {
                $vue.bgColorClass[anxSup] = 'bg-light';
                $vue.seleccionado = anxSup;
            }

            $vue.loadAnexosVisibles();
            let anx = $vue.getParameterQuery('anexo');
            if (anx !== '') {
                for (var i = 0; i < $vue.anexosVisibles.length; i++) {
                    let anxVis = $vue.anexosVisibles[i];
                    if (anxVis.id == anx) {
                        $vue.anexoSelect = anxVis;
                    }
                }
            }

            let orden = $vue.getParameterQuery('orden-registros');
            if (orden !== '') {
                $vue.ordenRegistros = orden;
                $vue.direccionSeccion = $vue.getOrdenador(orden, "seccion");
                $vue.direccionCurso = $vue.getOrdenador(orden, "curso");
            }

            let estado = $vue.getParameterQuery('estado');
            if (estado !== '') {
                $vue.bgColorEstadoClass[estado] = 'bg-light';
                $vue.estadoSeleccionada = estado;
            }

            let dictado = $vue.getParameterQuery('dictado');
            if (dictado !== '') {
                $vue.bgColorDictadoClass[dictado] = 'bg-light';
                $vue.dictadoSeleccionado = dictado;
            }

            $vue.setStyleOrdenSeccion();
            $vue.setStyleOrdenCurso();
            $vue.loadRegistros();
        },
        revisarFormNuevoGpoSeccion() {
            let $vue = this;
            var form = $("#" + $vue.nuevoGpoSeccModal.form);
            form.parsley().destroy();
            if ($vue.nuevoGpoSeccModal.revisarForm) {
                setTimeout(function () {
                    form.parsley().validate();
                }, 300);
            }
        },
        verAnexoSuperior(item) {
            let $vue = this;
            let anxSup = {};
            $vue.anexosHijos = [];

            for (var i = 0; i < $vue.anexosPadres.length; i++) {
                if (item.modalidadEstudio.codigo == 'PRE' && $vue.anexosPadres[i].id == 2) {
                    anxSup = $vue.anexosPadres[i];
                }
                if (item.modalidadEstudio.codigo == 'EPG' && $vue.anexosPadres[i].id == 4) {
                    anxSup = $vue.anexosPadres[i];
                }
            }

            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if (anxSup.id == anx.anexoSuperior.id) {

                    $vue.anexosHijos.push(anx);
                    if (item.modalidadEstudio.codigo == 'PRE' && item.departamentoAcademico.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                    if (item.modalidadEstudio.codigo == 'EPG' && item.carrera.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                }
            }
            $vue.anexoPadreCurso = anxSup;
            $vue.revisarFormNuevoGpoSeccion();
        },
        verAnexosHijos(item) {
            let $vue = this;
            let modal = $vue.newGrupoSeccion.curso.modalidadEstudio;
            let dpto = $vue.newGrupoSeccion.curso.departamentoAcademico;
            let carr = $vue.newGrupoSeccion.curso.carrera;
            console.log(modal.codigo + " ::: " + dpto.codigo + " ::: " + carr.codigo)

            $vue.anexosHijos = [];
            $vue.newGrupoSeccion.anexoBoletin = {};

            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if (item.id == anx.anexoSuperior.id) {
                    $vue.anexosHijos.push(anx);
                    if (modal.codigo == 'PRE' && dpto.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                    if (modal.codigo == 'EPG' && carr.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                }
            }
        },
        labelCurso(item) {
            if (item.id == undefined) {
                return "";
            }
            return item.codigo + " - " + item.nombre;
        },
        searchCursos(nombre) {
            let $vue = this;
            $vue.isLoadingCursos = true;
            $.ajax({
                url: APP.url(rutaModulo + '/allCursos'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: nombre},
                success(response) {
                    $vue.isLoadingCursos = false;
                    if (response.success) {
                        $vue.cursos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        clearAnexo() {
            let $vue = this;
            $vue.anexoSelect = null;
            $vue.loadRegistros();
        },
        verificarAnexoSelect() {
            let $vue = this;
            if ($vue.seleccionado === '') {
                return;
            }
            if ($vue.anexoSelect === null) {
                return;
            }
            let sup = $vue.anexosSup[$vue.seleccionado];
            if (sup !== $vue.anexoSelect.anexoSuperior.id) {
                $vue.anexoSelect = null;
            }

        },
        loadAnexosVisibles() {
            console.log("33333")
            let $vue = this;
            $vue.anexosVisibles = [];
            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if ($vue.seleccionado === '') {
                    console.log("1111")
                    $vue.anexosVisibles.push(anx);
                } else {
                    console.log("2222")
                    let sup = $vue.anexosSup[$vue.seleccionado];
                    if (sup === anx.anexoSuperior.id) {
                        $vue.anexosVisibles.push(anx);
                    }
                }
            }
        },
        loadDataInicial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url(rutaModulo + '/listGrupo'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.anexos = response.data.anexos;
                        $vue.anexosPadres = response.data.anexosSup;
                        console.log("$vue.anexos=" + $vue.anexos.length)
                        $vue.configInicio();
                    }
                }
            });
        },
        getRowspanGpoSecc(item) {
            let rows = 0;
            for (var i = 0; i < item.secciones.length; i++) {
                var secc = item.secciones[i];
                var docs = secc.docenteSeccion.length;
                rows += (docs == 0) ? 1 : docs;
            }
            rows = (rows == 0) ? 1 : rows;
            return rows;
        },
        getRowspanSecc(item) {
            let rows = item.docenteSeccion.length;
            rows = (rows == 0) ? 1 : rows;
            return rows;
        },
        tipoSeccion(item) {
            return item.tipoSeccionEnum.value.replace("Curso", "");
        },
        esPrincipal(profeSecc) {
            if (profeSecc.estadoEnum.name == 'ACT' && profeSecc.principal == 1) {
                return true;
            }
            return false;
        },
        editarGpoSeccion(item) {
            let $vue = this;
            location.href = APP.url(rutaModulo + "/" + item.id + "/editar") + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            console.log(url)
            return "?origen=" + Base64.encode(url);
        },
        loadRegistros() {
            let $vue = this;
            $vue.$refs.raptorGpoSecc.querie = [];
            $vue.$refs.raptorGpoSecc.changeUrl('queries[anexo-superior]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[anexo]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[orden-registros]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[estado]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[dictado]', null);

            if ($vue.seleccionado !== '') {
                $vue.setParameterQuery('anexo-superior', $vue.seleccionado);
            }
            if ($vue.anexoSelect !== null) {
                $vue.setParameterQuery('anexo', $vue.anexoSelect.id);
            }
            if ($vue.ordenRegistros !== "") {
                $vue.setParameterQuery('orden-registros', $vue.ordenRegistros);
            }
            if ($vue.estadoSeleccionada !== "") {
                $vue.setParameterQuery('estado', $vue.estadoSeleccionada);
            }
            if ($vue.dictadoSeleccionado !== "") {
                $vue.setParameterQuery('dictado', $vue.dictadoSeleccionado);
            }
            $vue.$refs.raptorGpoSecc.loadRemoteData(true);
            $vue.updateDataCiclo();
        },
        clonarCiclo() {
            let $vue = this;
            $vue.cicloClonacion = {id: null};
            $vue.$refs.modalCloneCiclo.open();
        },
        saveCloneCiclo() {
            let $vue = this;
            if ($vue.cicloClonacionBean.cicloOrigen.id == null) {
                return;
            }

            $vue.$refs.modalCloneCiclo.beginProcessing();
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/clonarciclo'),
                data: JSON.stringify($vue.cicloClonacionBean),
                contentType: "application/json",
                success: function (response) {
                    $vue.$refs.modalCloneCiclo.confirmReaction(response.success);
                    if (response.success) {
                        $vue.updateDataCiclo();
                        $vue.$refs.raptorGpoSecc.loadRemoteData();
                        notifyBootbox(response.message, "success");

                    } else {
                        notifyBootbox(response.message, "warning");
                    }
                },
                error: function () {
                    $vue.$refs.modalCloneCiclo.confirmReaction(false);
                    notifyBootbox(Messages.errorComunicacion, "error");
                }
            });
        },
        removeOrdenador(string, parte) {
            let ords = string.split(",");
            let idx = -100;
            for (var i = 0; i < ords.length; i++) {
                let existe = (ords[i].indexOf(parte) === 0);
                if (existe) {
                    idx = i;
                }
            }
            if (idx > -100) {
                ords.splice(idx, 1);
            }
            return ords.join(",");
        },
        getOrdenador(string, parte) {
            let ords = string.split(",");
            let ordenador = "";
            for (var i = 0; i < ords.length; i++) {
                let existe = (ords[i].indexOf(parte) === 0);
                if (existe) {
                    ordenador = ords[i];
                }
            }
            if (ordenador === "") {
                return 0;
            }

            let dir = ordenador.split(".")[1];
            if (dir === "asc")
                return 1;
            if (dir === "desc")
                return -1;
            return 0;
        },
        verEstado(tipo) {
            let $vue = this;
            if ($vue.estadoSeleccionada === '') {
                $vue.bgColorEstadoClass[tipo] = 'bg-light';
                $vue.estadoSeleccionada = tipo;
                $vue.loadRegistros();

            } else if ($vue.estadoSeleccionada !== '' && $vue.estadoSeleccionada !== tipo) {
                $vue.bgColorEstadoClass[$vue.estadoSeleccionada] = '';
                $vue.bgColorEstadoClass[tipo] = 'bg-light';
                $vue.estadoSeleccionada = tipo;
                $vue.loadRegistros();

            } else if ($vue.estadoSeleccionada !== '' && $vue.estadoSeleccionada === tipo) {
                $vue.bgColorEstadoClass[$vue.estadoSeleccionada] = '';
                $vue.estadoSeleccionada = '';
                $vue.loadRegistros();
            }
        },
    }
});

