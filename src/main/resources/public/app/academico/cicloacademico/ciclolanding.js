new Vue({
    el: '#main',
    data: {
        ciclo: {id: idciclo, descripcion2: nameciclo},
        ciclosVisibles: cicloVisible == "" ? "" : JSON.parse(cicloVisible)
    },
    mounted: function () {
        let vue = this;
    },
    methods: {
        activarCiclo(item) {
            let vue = this;
            if (item != null) {
                vue.ciclo = item;
            }
            $.ajax({
                url: APP.url('cicloland'),
                method: 'post',
                data: {ciclo: vue.ciclo.id}
            }).done(function (html) {
                location.href = "/route66";
            });
        }
    }
});

