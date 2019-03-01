Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    data: {
        raptorurl: APP.url('docente/ampliacionvacante/list'),
        dataModalAmpliacionVacante: {
            id: 'idModalAmpliacionVacante',
            header:true,
            title:'Ampliación Vacante'
        }
    },
    created: function () {
        let $vue = this;
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        changeVacante(seccion) {
            let $vue = this;
            $vue.$refs.modalAmpliacionVacante.open();
        },
        saveModalAmpliacionVacante(){
            
        }
    }
});


