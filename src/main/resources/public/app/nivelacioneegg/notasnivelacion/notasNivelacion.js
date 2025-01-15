const NotasNivelacion = httpVueLoader('/app/nivelacioneegg/notasnivelacion/NotasNivelacionInicio.vue');

new Vue({
    el: '#firstVue',
    components: {NotasNivelacion},
    template: "<notas-nivelacion></notas-nivelacion>"
});