var AulaHorarioVue = Vue.component("aula-horario-component", {
    template: "#modalAulaHorarioComp",
    props: {

    },
    data: function () {
        return {
            aula: null,
            dias: null,
            horas: null,
            jsonHorarioAula: null
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.loadComponent($vue, $vue.aula);
    },
    methods: {
        loadComponent($vue, aula) {
            $.ajax({
                method: 'POST',
                url: APP.url('general/aula/loadModalAulaHorario'),
                data: {
                    aula: aula.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.dias = response.data.dias;
                        $vue.horas = response.data.horas;
                        $vue.aula = response.data.aula;
                        $vue.jsonHorarioAula = response.data.jsonHorarioAula;
                        console.dir($vue.jsonHorarioAula);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        onmousein(e) {
            $(e.target).parents('.dropdown:first').find('.dropdown-menu').stop(true, true).delay(20).slideDown(500);
        },
        onmouseout(e) {
            $(e.target).parents('.dropdown:first').find('.dropdown-menu').stop(true, true).delay(20).slideUp(500);
        }
    }
});