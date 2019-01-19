Vue.component("aula-horario-component", {
    template: "#aulaHorarioComp",
    props: {
        dias: {type: Array, default: []},
        horas: {type: Array, default: []},
        jsonaulahorario: {type: Array, default: []}
    },
    mounted: function () {
        let $vue = this;
        $vue.loadComponent($vue);
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
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        selectHora(dia, hora) {
            let $vue = this;
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
        }
    }
});

