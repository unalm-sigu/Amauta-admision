Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ordenmeritoVUE',
    data: {
        cicloAcademico: {},
        URL: APP.url('academico/ordenmerito'),
        processreporte: false
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
        generarReporteCiclo() {
            let $vue = this;
            console.log($("#cicloChange").val());
            $vue.processreporte = true;          
            var urll = APP.url('academico/ordenmerito/reportePdfOrdenMeritoCiclo');
            axios({
                url: urll,
                method: 'POST',
                responseType: 'blob',
                params: {cicloId: $("#cicloChange").val()}
            }).then((response) => {
                var namee = response
                        .headers["content-disposition"]
                        .replace("attachment; filename=", "")
                        .replace(/"/g, '');
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', namee);
                document.body.appendChild(link);
                link.click();
                $vue.processreporte = false;
            }).catch(error => {
                $vue.processreporte = false;
                notify(MESSAGES.errorComunicacion, "error");
            });
        }
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




