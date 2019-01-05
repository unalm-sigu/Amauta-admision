new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        ciclo: JSON.parse(cicloJson),
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        nuevoGpoSecc() {
            let $vue = this;
        },
    }
});

