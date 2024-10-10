const CursoNivelacion = httpVueLoader('/app/nivelacioneegg/cursonivelacion/CursoNivelacion.vue');

new Vue({
    el: '#firstVue',
    components: {CursoNivelacion},
    template: "<curso-nivelacion></curso-nivelacion>"
});