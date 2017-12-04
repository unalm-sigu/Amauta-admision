var app = new Vue({
    el: '#divProgramacion',
    data: {
        message: 'Hello Vue!'
    },
    methods: {
        nuevaProgramacion: function (e) {
            e.preventDefault();
            console.log("entro a nueva programacion");
            location.href = APP.url("academico/programacion/nuevo");
        }
    }
})