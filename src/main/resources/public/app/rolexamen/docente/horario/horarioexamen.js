
new Vue({
    el: '#main',
    data: {
        rolExamen: [],
        semanasExamen: [],
        grupoHorasExamen: [],
        semanaExamenActiva: null,
        grupoActivo: null,
        fechaInicio: null,
        fechaFin: null,
        nombreExamen: ""
    },
    mounted() {

        this.plantilla();
    },
    methods: {
        plantilla: function () {
            let $vue = this;
            AXIOS.post("horariodocente/plantilla/")
                    .then(response => {
                        if (response.data.success) {
                            this.semanasExamen = response.data.data;
                            this.semanasExamen[0].selected = true;
                            $vue.semanaExamenActiva = this.semanasExamen[0];
                            this.fechaInicio = this.semanasExamen[0].fechaInicio;
                            this.fechaFin = this.semanasExamen[0].fechaFin;
                            this.nombreExamen = this.semanasExamen[0].nombreExamen;
                        }
                    });

        },
        fechaGrupoHoraItem(fechaGrupoHora) {
            if (this.grupoActivo != null && fechaGrupoHora.grupoHorasExamen.id == this.grupoActivo.id) {
                return "border-color:#600D63; background-color:#DCDFE3"
            }

            return "border-color:#DFE7EE; background-color:#FFFFFF;"
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
            this.seleccionarSemana();
        },
        backweek() {
            let $vue = this;
            var init = moment($vue.fechaInicio, "DD/MM/YYYY");
            var fin = init.add(-1, 'days').format('DD/MM/YYYY');
            var inicio = init.add(-6, 'days').format('DD/MM/YYYY');
            $vue.fechaInicio = inicio;
            $vue.fechaFin = fin;
            this.seleccionarSemana();
        },
        seleccionarSemana() {
            let $vue = this;
            this.semanasExamen.forEach(function (x) {
                x.selected = false;
            });
            this.semanasExamen.forEach(function (x) {
                if (x.fechaInicio == $vue.fechaInicio) {
                    x.selected = true;
                    $vue.semanaExamenActiva = x;
                }
            });
        }
    }
});
