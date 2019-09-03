new Vue({
    el: '#horarioDocenteVue',
    data: {
        ciclo: JSON.parse(cicloJson),
        docente: JSON.parse(docenteJson)
    },
    methods: {

    },
    mounted: function () {
        this.$refs.loadHorario.cargaHorario();
    }
});







