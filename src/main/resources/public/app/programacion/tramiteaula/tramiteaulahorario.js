Vue.component("aula-horario-component", {
    template: "#aulaHorarioComp",
    props: {
        dias: {type: Array, default: []},
        horas: {type: Array, default: []},
        jsonaulahorario: {type: Array, default: []},
        validarrangofecha: {type: Function, default: () => {
            }},
    },
    mounted: function () {
        let $vue = this;
        $vue.loadComponent($vue);

        $global.$on("clearhorario", function () {
            $vue.clearhorario();
        });
    },
    methods: {
        loadComponent($vue) {
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/aula/loadHorario'),
                success: function (response) {
                    if (response.success) {
                        $vue.dias = response.data.dias;
                        $vue.horas = response.data.horas;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        selectHora(dia, hora) {
            let $vue = this;
            let estado = $vue.validarrangofecha(dia, hora);
            if (estado) {
                return;
            }
            if (dia.selecionado) {
                dia.selecionado = false;
            } else {
                dia.selecionado = true;
            }
            let idd = dia.id + "-" + hora.id
            let item = $vue.jsonaulahorario.find(x => x.id == idd);
            if (item != null) {
                let indexx = $vue.jsonaulahorario.indexOf(item);
                $vue.jsonaulahorario.splice(indexx, 1);
            } else {
                $vue.jsonaulahorario.push({id: idd, dia: {id: dia.id}, hora: {id: hora.id}});
            }
            $global.$emit('changehorario');
        },
        clearhorario() {
            let $vue = this;
            $vue.horas.map(function (vall, inx) {
                vall.dias.map(function (valll, inx) {
                    valll.selecionado = false;
                });
            });
            $vue.jsonaulahorario = [];
        }
    }
});

