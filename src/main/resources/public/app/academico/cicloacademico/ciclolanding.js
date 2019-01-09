new Vue({
    el: '#main',
    data: {
        ciclo: {id: idciclo, descripcion2: nameciclo},
        ciclosVisibles: JSON.parse(cicloVisible)
    },
    mounted: function () {
        let vue = this;
    },
    methods: {
        activarCiclo() {
            let vue = this;
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

