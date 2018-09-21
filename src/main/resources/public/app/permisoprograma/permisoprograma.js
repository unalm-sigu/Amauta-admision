Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#permisoProgramacionVUE',
    data: {
        colaboradorURL: APP.url("permisoprograma/buscar/list"),
        anexoBoletin: JSON.parse(anexoBoletin),
        programaCurso: JSON.parse(programaCurso),
        programaSeccion: JSON.parse(programaSeccion),
        programaGpoSeccion: JSON.parse(programaGpoSeccion),
        programaDocente: JSON.parse(programaDocente),
        colaboradorSelect: 0,
        facultadSelect: 0,
        modalAddPermiso: {
            id: 'modalAddPermiso',
            header: true,
            title: 'Agregar Cita',
            showaccept: true
        },
    },
    computed: {

    },
    mounted: function () {
        let $vue = this;
        console.log($vue.list);
        console.log($vue.programaCurso);
        console.log($vue.programaSeccion);
        console.log($vue.programaGpoSeccion);
        console.log($vue.programaDocente);
    },
    methods: {
        seleccionar(item) {
            var $vue = this;
            $vue.colaboradorSelect = item.colaborador.id;
            $vue.facultadSelect = item.id;
        },
        deseleccionar() {
            var $vue = this;
            $vue.colaboradorSelect = 0;
        },
        classTable(item) {
            var $vue = this;
            if (item.id == $vue.facultadSelect) {
                return "fondo-gray";
            }
            return "";
        },
        customLabel( { nombre }) {
            return nombre
        },
        modifyPermiso(value) {
            console.log(value)
        }
    }
});
