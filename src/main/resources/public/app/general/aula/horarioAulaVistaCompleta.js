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
        horariosAulaPDFBean: {}
    },
    created: function () {
        let $vue = this;
        $vue.aulaSuperior = $vue.listAulaSuperior[0];
        $vue.getListAulas();
        $vue.aula = $vue.listAulaOptions[0];

        var now = moment();
        var day = now.day();
        var first = parseInt(day) - 1;
        var init = now.add(-first, 'days').format('DD/MM/YYYY');
        var end = now.add(6, 'days').format('DD/MM/YYYY');

        $vue.fechaInicio = init;
        $vue.fechaFin = end;

        console.log(JSON.stringify($vue.listAulaOptions));
        console.log(JSON.stringify($vue.fechaFin));

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
        },
        loadComponent() {
            let $vue = this;
            console.log($vue.fechaInicio);
            console.log($vue.fechaFin);

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
                        console.log("excedio");
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
                        console.log("excedio");
                    } else {
                        $vue.aula = Object.assign({}, $vue.listAulaOptions[status]);
                        $vue.loadComponent();
                    }
                }
            }
        },
        descargaPDF() {
            let $vue = this;

            $vue.horariosAulaPDFBean.aulaSuperior = $vue.aulaSuperior;
            $vue.horariosAulaPDFBean.aula = $vue.aula;
            $vue.horariosAulaPDFBean.aula.tipoAmbienteEnum = undefined;

            $.fileDownload("/" + rutaModulo + "/generatorpdf", {
                httpMethod: "POST",
                data: {
                    strAula: JSON.stringify($vue.aula),
                    strAulaSuperior: JSON.stringify($vue.aulaSuperior),
                    fechaInicio: $vue.fechaInicio,
                    fechaFin: $vue.fechaFin
                },
                successCallback: function (responseHtml, url) {
                    console.log('aqui');
                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(MESSAGES.errorComunicacion, 'error')
                }
            });
        }
    }
});








