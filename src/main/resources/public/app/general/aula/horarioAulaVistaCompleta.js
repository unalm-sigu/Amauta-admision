Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#horariosVUE',
    data: {
        listAulaSuperior: JSON.parse(listAulaSuperiorJson),
        listAula: JSON.parse(listAulaJson),
        listAulaOptions: [],
        aula: null,
        aulaSuperior: null,
        horas: [],
        dias: [],
        fechaInicio: null,
        fechaFin: null,
        dateIni: null,
        dateFin: null
    },
    created: function () {
        let $vue = this;
        $vue.aulaSuperior = $vue.listAulaSuperior[0];
        $vue.getListAulas();
        $vue.aula = $vue.listAulaOptions[0];

        var now = moment();
        var now2 = moment();
        var day = now.day();
        var first = parseInt(day) - 1;
        $vue.dateIni = now.add(-first, 'days');
        $vue.dateFin = now2.add(6 - first, 'days');

        $vue.fechaInicio = $vue.dateIni.format('DD/MM/YYYY');
        $vue.fechaFin = $vue.dateFin.format('DD/MM/YYYY');

        $vue.loadComponent();
    },
    mounted: function () {
        let $vue = this;

    },
    methods: {
        inputAulaSuperior() {
            let $vue = this;
            $vue.listAulaOptions = [];
            $vue.getListAulas();
            $vue.aula = $vue.listAulaOptions[0];
            $vue.loadComponent();
        },
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
        getListAulas() {
            let $vue = this;
            var aulaSuperiorSelected = $vue.aulaSuperior.id;

            for (var i = 0; i < $vue.listAula.length; i++) {
                if ($vue.listAula[i].aulaSuperior.id == aulaSuperiorSelected) {
                    $vue.listAulaOptions.push(Object.assign({}, $vue.listAula[i]));
                }
            }
        },
        inputAula() {
            let $vue = this;
            $vue.loadComponent();
        },
        changeAulaSuperior(direccion) {
            let $vue = this;
            var total = $vue.listAulaSuperior.length;
            var aulaSuperiorSelected = $vue.aulaSuperior.id;

            for (var i = 0; i < $vue.listAulaSuperior.length; i++) {
                if ($vue.listAulaSuperior[i].id == aulaSuperiorSelected) {

                    var status = (direccion == 'next' ? i + 1 : i - 1)

                    if (status >= total || status < 0) {
//                        console.log("excedio");
                    } else {
                        $vue.aulaSuperior = Object.assign({}, $vue.listAulaSuperior[status]);
                        $vue.listAulaOptions = [];
                        $vue.getListAulas();
                        $vue.aula = $vue.listAulaOptions[0];
                        $vue.loadComponent();
                    }
                }
            }
        },
        changeAula(direccion) {
            let $vue = this;
            var total = $vue.listAulaOptions.length;

            if ($vue.aula == null) {
                return null;
            }

            var aulaSelected = $vue.aula.id;

            for (var i = 0; i < $vue.listAulaOptions.length; i++) {
                if ($vue.listAulaOptions[i].id == aulaSelected) {

                    var status = (direccion == 'next' ? i + 1 : i - 1)

                    if (status >= total || status < 0) {
//                        console.log("excedio");
                    } else {
                        $vue.aula = Object.assign({}, $vue.listAulaOptions[status]);
                        $vue.loadComponent();
                    }
                }
            }
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
        },
        changeFecha(direccion) {
            let $vue = this;
            let days = (direccion === "back") ? -7 : 7;
            $vue.dateIni = $vue.dateIni.add(days, 'days');
            $vue.dateFin = $vue.dateFin.add(days, 'days');

            $vue.fechaInicio = $vue.dateIni.format('DD/MM/YYYY');
            $vue.fechaFin = $vue.dateFin.format('DD/MM/YYYY');

            $vue.loadComponent();
        }
    }
});








