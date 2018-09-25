Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ordenmeritoVUE',
    data: {
        cicloAcademico: {},
        URL: APP.url('academico/ordenmerito')
    },
    mounted() {
        $("#cicloChange").select2();
    },
    methods: {
        generarDatos() {
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/generardatos`, this.cicloAcademico)
                    .then(response => {
                        this.$refs.raptor.loadRemoteData();
                        MODAL.hideWait();
                    })
        },
        calcularMeritos() {
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/calcularmeritos`, this.cicloAcademico)
                    .then(response => {
                        this.$refs.raptor.loadRemoteData();
                        MODAL.hideWait();
                    })
        },
    }
});


$(function () {
    $("body").delegate("#cicloChange", "change", function (e) {
        $.ajax({
            url: APP.url('academico/ordenmerito/changeciclo'),
            type: 'POST',
            async: false,
            data: {ciclo: $("#cicloChange").val()}
        }).done(function (html) {
            location.reload();
        });
    });
});




