var AulaHorarioVue = Vue.component("aula-horario-component", {
    template: "#modalAulaHorarioComp",
    props: {

    },
    data: function () {
        return {
            aula: null,
            dias: null,
            horas: null,
            fechaInicio: null,
            fechaFin: null,
        }
    },
    mounted: function () {

        let $vue = this;
        var now = moment();
        var day = now.day();
        var first = parseInt(day) - 1;
        var init = now.add(-first, 'days').format('DD/MM/YYYY');
        var end = now.add(6, 'days').format('DD/MM/YYYY');

        $vue.fechaInicio = init;
        $vue.fechaFin = end;
        $vue.loadComponent();
    },
    methods: {
        loadComponent() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('general/aula/loadModalAulaHorario'),
                data: {
                    id: $vue.aula.id,
                    fechaInicio: $vue.fechaInicio,
                    fechaFin: $vue.fechaFin
                },
                success: function (response) {
                    if (response.success) {
                        $vue.dias = response.data.dias;
                        $vue.horas = response.data.horas;
                        $vue.aula = response.data.aula;
                    } else {
                        notify(Messages.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        onmousein(e) {
            $(e.target).parents('.dropdown:first').find('.dropdown-menu').stop(true, true).delay(20).slideDown(500);
        },
        onmouseout(e) {
            $(e.target).parents('.dropdown:first').find('.dropdown-menu').stop(true, true).delay(20).slideUp(500);
        },
        nextweek() {
            let $vue = this;
            var init = moment($vue.fechaFin, "DD/MM/YYYY");
            var inicio = init.add(1, 'days').format('DD/MM/YYYY');
            var fin = init.add(6, 'days').format('DD/MM/YYYY');
            $vue.fechaInicio = inicio;
            $vue.fechaFin = fin;
            $vue.loadComponent();
        },
        backweek() {
            let $vue = this;
            var init = moment($vue.fechaInicio, "DD/MM/YYYY");
            var fin = init.add(-1, 'days').format('DD/MM/YYYY');
            var inicio = init.add(-6, 'days').format('DD/MM/YYYY');
            $vue.fechaInicio = inicio;
            $vue.fechaFin = fin;
            $vue.loadComponent();
        },
        classByTipo(tipo) {
            if (tipo == 'DICT') {
                return 'text-primary';
            }
            if (tipo == 'RESERV') {
                return 'text-warning';
            }
            if (tipo == 'EXAM') {
                return 'text-success';
            }
        }
    }
});