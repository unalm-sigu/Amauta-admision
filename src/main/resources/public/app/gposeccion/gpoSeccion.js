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
    data: {
        gpoSeccionesURL: APP.url('academico/gposeccion/list'),
        seleccionado: '',
        bgColorClass: {ingresantes: '', departamentos: '', postgrados: '', actividades: ''},
        anexosSup: {ingresantes: 1, departamentos: 2, postgrados: 4, actividades: 3},
        anexos: [],
        anexosVisibles: [],
        anexoSelect: {},
        restriccionModal: {
            id: 'modalRestriccion',
            header: true,
            seccionSelect: {},
            title: 'Restricciones Modalidad / Facultad / Especialidad',
            //okbtn: 'Aceptar',
            modalsize: 'modal-md'
        },
        seccionSelect: {},
        tipoRestriccion: ''
    },
    mounted: function () {
        let $vue = this;


        let tipo = $vue.$refs.load.getParameterByName('queries[anexo-superior]');
        tipo = (tipo == null) ? '' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.load.querie.push({name: 'anexo-superior', value: tipo});
        }
        $vue.loadDataInicial();

        let anx = $vue.$refs.load.getParameterByName('queries[anexo]');
        anx = (anx == null) ? '' : anx;
        if (anx == '') {
            $vue.$refs.load.repreload();
        }
    },
    methods: {
        verRestriccion(seccion, gpoSecc, tipo) {
            let $vue = this;
            seccion.grupoSeccion = gpoSecc;
            $vue.seccionSelect = seccion;
            $vue.tipoRestriccion = tipo;
            if (seccion.restriccionesRepitencia.length > 0 && tipo == "REP") {
                $vue.restriccionModal.title = "Restricciones de Repitencia";
            } else if (seccion.restriccionesCarrera.length > 0 && tipo == "CARR") {
                $vue.restriccionModal.title = "Restricciones de Carrera";
            } else if (seccion.restriccionesFacultad.length > 0 && tipo == "FAC") {
                $vue.restriccionModal.title = "Restricciones de Facultad";
            } else if (seccion.restriccionesModalidad.length > 0 && tipo == "MOD") {
                $vue.restriccionModal.title = "Restricciones de Modalidad de Estudio";
            }
            $vue.$refs.modalRestriccion.open();
        },
        tieneRestricciones(seccion) {
            if (seccion.restriccionCapa != "") {
                return true;
            }
            if (seccion.restriccionesRepitencia.length > 0) {
                return true;
            }
            if (seccion.restriccionesModalidad.length > 0) {
                return true;
            }
            if (seccion.restriccionesFacultad.length > 0) {
                return true;
            }
            if (seccion.restriccionesCarrera.length > 0) {
                return true;
            }
            return false;
        },
        existeAnexoSelect() {
            let $vue = this;
            if ($vue.anexoSelect == null) {
                return false;
            }
            if ($vue.anexoSelect.id === undefined) {
                return false;
            }
            return true;
        },
        clearAll() {
            let $vue = this;
            $vue.anexoSelect = {};
            $vue.gpoSeccionesAnexo(null);
        },
        verificarAnexoSelect() {
            let $vue = this;
            if ($vue.seleccionado == '') {
                return;
            }
            if ($vue.anexoSelect.id === undefined) {
                return;
            }
            let sup = $vue.anexosSup[$vue.seleccionado];
            if (sup !== $vue.anexoSelect.anexoSuperior.id) {
                $vue.anexoSelect = {};
            }

        },
        loadAnexosVisibles() {
            let $vue = this;
            $vue.anexosVisibles = [];
            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if ($vue.seleccionado == '') {
                    $vue.anexosVisibles.push(anx);
                } else {
                    let sup = $vue.anexosSup[$vue.seleccionado];
                    if (sup == anx.anexoSuperior.id) {
                        $vue.anexosVisibles.push(anx);
                    }
                }
            }
        },
        loadDataInicial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/gposeccion/allData'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.anexos = response.data.anexos;
                        $vue.loadAnexosVisibles();

                        let anx = $vue.$refs.load.getParameterByName('queries[anexo]');
                        anx = (anx == null) ? '' : anx;
                        if (anx == '') {
                            return;
                        }
                        for (var i = 0; i < $vue.anexosVisibles.length; i++) {
                            let anexo = $vue.anexosVisibles[i];
                            let idAnx = parseInt(anx);
                            if (idAnx == anexo.id) {
                                $vue.anexoSelect = anexo;
                                $vue.$refs.load.querie.push({name: 'anexo', value: $vue.anexoSelect.id});
                            }
                        }

                        $vue.$refs.load.repreload();
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
            location.href = APP.url("academico/gposeccion/" + item.id + "/editar") + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        verAnexos(tipo) {
            let $vue = this;
            $vue.$refs.load.querie = [];

            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'anexo-superior', value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'anexo-superior', value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.load.changeUrl('queries[anexo-superior]', null);
            }

            $vue.loadAnexosVisibles();
            $vue.verificarAnexoSelect();
            $vue.settingUrlAnexoSelect();
            $vue.$refs.load.loadRemoteData();

        },
        settingUrlAnexoSelect() {
            let $vue = this;

            if ($vue.anexoSelect == null) {
                $vue.$refs.load.changeUrl('queries[anexo]', null);
                return;
            }
            if ($vue.anexoSelect.id === undefined) {
                $vue.$refs.load.changeUrl('queries[anexo]', null);
                return;
            }
            $vue.$refs.load.querie.push({name: 'anexo', value: $vue.anexoSelect.id});
        },
        gpoSeccionesAnexo(item) {
            let $vue = this;
            $vue.$refs.load.querie = [];
            if ($vue.seleccionado !== '') {
                $vue.$refs.load.querie.push({name: 'anexo-superior', value: $vue.seleccionado});
            }
            $vue.settingUrlAnexoSelect();
            $vue.$refs.load.loadRemoteData();

        }
    }
});

